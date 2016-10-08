/**
 * 多级菜单组件
 * 
 * Depends on: jquery.js, jquery-ui.js
 */

;
(function($) {

    "use strict";

    var LevelMenu = function(container, options) {
        var bsVersion = $.bootstrap.getVersion();
        this._default = {
            options : {
                nodeIdName : "id", // 节点id名称
                nodeTextName : "text", // 节点文本名称
                parentNodeIcon : bsVersion == 3 ? "glyphicon-menu-right" : "icon-chevron-right", // 具有子节点的节点图标样式名
                showOnInit : true, // 初始化后是否显示一级菜单
                hideOnClickOutside : true, // 是否在点击菜单外部时隐藏菜单
                locateCache : true, // 是否缓存菜单位置，缓存可提高性能但菜单将始终在第一次显示的位置显示
                itemClick : undefined
            }
        };
        this.$container = $(container);
        this.$container.addClass("dropdown");
        // 不能用ul对象作为容器
        if (this.$container[0].tagName.toLowerCase() == "ul") {
            $.console.error("ul element can't be container of LevelMenu");
            return undefined;
        }
        this.init(options);

        return {
            reset : $.proxy(this.reset, this),
            getMenu : $.proxy(this.getMenu, this),
            showMenu : $.proxy(this.showMenu, this),
            hideMenu : $.proxy(this.hideMenu, this),
            bindToggle : $.proxy(this.bindToggle, this),
            locateMenu : $.proxy(this.locateMenu, this)
        };
    };

    LevelMenu.prototype.pluginName = "levelMenu";

    LevelMenu.prototype.init = function(options) {
        this.tree = [];
        if (options.data) {
            if (typeof options.data === "string") {
                options.data = $.parseJSON(options.data);
            }
            this.tree = $.extend(true, [], options.data);
            delete options.data;
        }
        this.options = $.extend(true, {}, this._default.options, options);
        this.subscribeEvents();
        this.menu = this.buildMenu(this.tree); // 构建第一级菜单
        if (this.options.showOnInit) {
            this.showMenu(this.menu);
        }
    }

    LevelMenu.prototype.reset = function(data) {
        if (data) {
            if (typeof data === "string") {
                data = $.parseJSON(data);
            }
            this.tree = $.extend(true, [], data);
        }
        var menu = this.menu.next();
        while (menu.is("ul.dropdown-menu")) {
            menu.remove();
            menu = this.menu.next();
        }
        this.menu.remove();
        this.menu = this.buildMenu(this.tree); // 构建第一级菜单
    }

    LevelMenu.prototype.subscribeEvents = function() {
        var _this = this;
        if (this.options.hideOnClickOutside) {
            $("html").on("click.context.data-api", function(event) {
                // var target = $(event.target);
                // if (_this.$container.find(target).length || _this.$container.is(target)) {
                // return;
                // }
                _this.hideMenu(null);
            });
        }
        $("html").on("keydown.context.data-api", function(event) {
            if (event.which == 27) {
                _this.hideMenu(null);
            }
        });
    }

    LevelMenu.prototype.buildMenu = function(nodes, parentItem) {
        var menu = $('<ul></ul>').addClass("dropdown-menu");
        if (parentItem) {
            menu.attr(this.options.nodeIdName, parentItem.attr(this.options.nodeIdName));
        }
        this.$container.append(menu);
        var _this = this;
        menu.mouseover(function() {
            _this.showMenu(menu);
        });
        menu.append(this.buildMenuItems(nodes));
        return menu;
    }

    LevelMenu.prototype.buildMenuItems = function(nodes) {
        var items = [];
        if (nodes) {
            var _this = this;
            nodes.each(function(node) {
                items.push(_this.buildMenuItem(node));
            });
        }
        return items;
    }

    LevelMenu.prototype.buildMenuItem = function(node) {
        var nodeId = node[this.options.nodeIdName];
        if (nodeId == undefined || nodeId == null) {
            $.console.error("node." + this.options.nodeIdName + " must be specified");
            return undefined;
        }
        var link = node.link || "javascript:void(0)";
        var text = node[this.options.nodeTextName];
        var item = $('<li><a href="' + link + '"></a></li>');
        item.attr(this.options.nodeIdName, nodeId);
        var _this = this;
        var nodes = this.getSubNodes(node);
        if (nodes && nodes.length) {
            $("a", item).html("<span>" + text + "</span>");
            var v3 = $.bootstrap.getVersion() == 3;
            var nodeClass = v3 ? "col-md-12" : "span12";
            $("span:first", item).addClass(nodeClass).css({
                margin : "0px",
                padding : "0px",
                minHeight : "0px"
            });

            var parentNodeIcon;
            if (v3) {
                parentNodeIcon = $('<span class="glyphicon"></span>');
            } else {
                parentNodeIcon = $("<i></i>");
            }
            $("a", item).append(parentNodeIcon.addClass(this.options.parentNodeIcon));

            item.mouseout(function() {
                _this.hideMenu(_this.getMenu(nodeId));
            });
        } else {
            $("a", item).html(text);
        }
        if ($.isFunction(node.click)) {
            item.click(node.click);
        } else if ($.isFunction(this.options.itemClick)) {
            item.click(this.options.itemClick);
        }
        item.mouseover(function() {
            // 隐藏兄弟节点对应的子菜单
            var siblings = $("li[" + _this.options.nodeIdName + "]", item.parent());
            siblings.each(function(index, sibling) {
                var siblingNodeId = parseInt($(sibling).attr(_this.options.nodeIdName));
                if (siblingNodeId != nodeId) {
                    _this.hideMenu(_this.getMenu(siblingNodeId));
                }
            });
            // 显示当前节点对应的子菜单
            if (nodes && nodes.length) {
                var subMenu = _this.getMenu(nodeId);
                if (subMenu == undefined) {
                    subMenu = _this.buildMenu(nodes, item);
                }
                _this.showMenu(subMenu);
            }
        });
        return item;
    }

    LevelMenu.prototype.getSubNodes = function(node) {
        if (node == undefined) { // 取顶级节点集
            return this.tree;
        } else { // 取指定节点的子节点集
            if ($.isArray(node.nodes)) {
                return node.nodes;
            } else if ($.isFunction(node.nodes)) {
                return node.nodes(node);
            } else {
                return undefined;
            }
        }
    }

    LevelMenu.prototype.getMenu = function(nodeId) {
        var menu;
        if (nodeId == undefined || nodeId === null) {
            menu = this.menu; // 第一级菜单
        } else {
            menu = $("ul[" + this.options.nodeIdName + "='" + nodeId + "']", this.$container);
        }
        if (menu.length == 0) {
            menu = undefined;
        }
        return menu;
    }

    LevelMenu.prototype.showMenu = function(menu) {
        if (menu === null) { // 参数为null表示第一级菜单
            menu = this.menu;
        } else if (typeof menu == "string" || typeof menu == "number") {
            menu = this.getMenu(menu);
        }
        if (menu != undefined && menu.is(":hidden")) {
            var parentNodeId = menu.attr(this.options.nodeIdName);
            if (parentNodeId != undefined) { // 不是第一级菜单才可定位
                // 先显示上级菜单
                var parentItem = $("li[" + this.options.nodeIdName + "='" + parentNodeId + "']",
                        this.$container);
                this.showMenu(parentItem.parent());
                // 再显示并定位当前菜单
                this.locateMenu(menu, parentItem);
            } else { // 顶级菜单直接显示
                menu.show();
            }
        }
        return menu;
    }

    /**
     * 将当前菜单与指定对象绑定显示切换，当鼠标移至目标对象上时显示当前菜单，移出时隐藏
     * 
     * @param target
     *            目标对象
     */
    LevelMenu.prototype.bindToggle = function(target) {
        var _this = this;
        target.mouseover(function() {
            _this.locateMenu(null, target);
        });
        target.mouseout(function() {
            _this.hideMenu(null);
        });
        $(":not(" + target.selector + ")", target.parent()).mouseover(function() {
            _this.hideMenu(null);
        });
    }

    LevelMenu.prototype.locateMenu = function(menu, cnchor) {
        if (menu === null) { // 参数为null表示第一级菜单
            menu = this.menu;
        } else if (typeof menu == "string" || typeof menu == "number") {
            menu = this.getMenu(menu);
        }
        if (menu != undefined) {
            menu.show();
            if (menu.attr("located") != "true") { // 未定位过才需要定位
                // 定位当前菜单位置
                var offset = cnchor.offset();
                if (offset) {
                    var top = offset.top - parseInt(menu.css("paddingTop")) - 1;
                    var left = offset.left + cnchor.width();
                    menu.offset({
                        top : top,
                        left : left
                    });
                }
                if (this.options.locateCache === true) {
                    menu.attr("located", "true");
                }
            }
        }
    }

    LevelMenu.prototype.hideMenu = function(menu) {
        if (menu === null) { // 参数为null表示第一级菜单
            menu = this.menu;
        } else if (typeof menu == "string" || typeof menu == "number") {
            menu = this.getMenu(menu);
        }
        if (menu != undefined && menu.is(":visible")) { // 菜单可见才需要隐藏
            // 隐藏子菜单
            var _this = this;
            var items = $("li[" + this.options.nodeIdName + "]", menu);
            items.each(function(index, item) {
                var nodeId = $(item).attr(_this.options.nodeIdName);
                var subMenu = _this.getMenu(nodeId);
                _this.hideMenu(subMenu);
            });
            // 隐藏当前菜单
            menu.hide();
        }
        return menu;
    }

    $.fn[LevelMenu.prototype.pluginName] = function(options, args) {
        var result = undefined;

        this.each(function() {
            var _this = $.data(this, LevelMenu.prototype.pluginName);
            if (typeof options === 'string') {
                if (!_this) {
                    $.console.error('Not initialized, can not call method : ' + options);
                } else if (!$.isFunction(_this[options]) || options.charAt(0) === '_') {
                    $.console.error('No such method : ' + options);
                } else {
                    if (!(args instanceof Array)) {
                        args = [ args ];
                    }
                    result = _this[options].apply(_this, args);
                }
            } else if (typeof options === 'boolean') {
                result = _this;
            } else {
                $.data(this, LevelMenu.prototype.pluginName, new LevelMenu(this, options));
            }
        });

        return result == undefined ? this : result;
    };

}(jQuery));
