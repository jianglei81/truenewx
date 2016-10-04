/**
 * multiselect-view.js v1.0.0
 * 
 * Depends on: jquery-ui.js(sortable), bootstrap.js(tooltip) 多选展示插件
 */

(function($) {

    var MultiSelectView = function(element, options) {
        this.init(element, options);
    };

    MultiSelectView.prototype = {
        init : function(element, options) {
            this.element = element;
            this.setOptions(options);
        },
        setOptions : function(options) {
            this.options = $.extend({
                id : "id", // 数据源ID对应字段名，默认：id
                caption : "caption", // 数据源内容对应字段名，默认：caption
                width : "469", // 控件宽度
                minColumn : 0, // 最小显示行数
                maxColumn : 0, // 最大显示行数
                vertical : false, // 是否纵向显示，默认false
                sortable : false, // 是否可拖动排序，默认false
                data : null
            // 数据源
            }, (options || $.fn.multiselectView.defaults));

            this.render();
        },
        render : function() {
            var el = $(this.element);
            $id = el.attr("id"), $name = el.attr("name"), $value = el.attr("value"), $class = el
                    .attr("class"), $style = el.attr("style"), initElm = $("<div></div>").addClass(
                    "modal treeSelector").attr("id", $id).attr(
                    "style",
                    "word-wrap: break-word;left: 0;position:static;margin-left:0;top:0;width:"
                            + this.options.width + "px"), bodyElm = $("<div></div>").addClass(
                    "modal-body clearfix").attr("style", this.initColumn()), headerELm = $(
                    "<div></div>").addClass("modal-header");
            if ($.isArray(this.options.btns) && this.options.btns.length > 0) {
                var titleElm = $("<span>&nbsp;</span>");
                this.createBtns(headerELm);
                if (!this.options.title) {
                    headerELm[0].appendChild(titleElm[0]);
                }
                initElm[0].appendChild(headerELm[0]);
            }
            if (this.options.title) {
                var titleElm = $("<span></span>").text(this.options.title);
                headerELm[0].appendChild(titleElm[0]);
                initElm[0].appendChild(headerELm[0]);
            }

            initElm[0].appendChild(bodyElm[0]);
            this.element = initElm.replaceAll(el);
            $(this.element).data("multiSelect", this);
            bodyElm.data("multiSelectBody", this.options);
            this.addItems(undefined, bodyElm);
        },
        initColumn : function() {
            var style = "padding: 0px; margin-bottom: 4px; overflow-y: auto;";
            if (this.options.minColumn > 0) {
                style += "min-height: " + (44 * this.options.minColumn) + "px;";
            }
            if (this.options.maxColumn > 0) {
                style += "max-height:" + (44 * this.options.maxColumn) + "px;";
            }
            return style;
        },
        createBtns : function(headerELm) {
            this.options.btns.each(function(btn) {
                if (btn) {
                    var btnObj = $("<button></button>").addClass("close").attr("type", "button")
                            .attr("style", "opacity:1;");
                    var iObj = $("<i></i>");
                    btn.icon ? iObj.addClass(btn.icon) : iObj.addClass("icon-hand-up");
                    if (btn.click) {
                        btnObj.click(btn.click);
                    }
                    if (btn.tooltipTitles) {
                        btnObj.tooltip($.extend({
                            animation : true,
                            html : true,
                            placement : 'top',
                            selector : false,
                            title : "",
                            container : false
                        }, {
                            title : btn.tooltipTitles
                        }));
                    }
                    btnObj[0].appendChild(iObj[0]);
                    headerELm[0].appendChild(btnObj[0]);
                }
            });
        },
        addItems : function(list, domElm) {
            var el = (!domElm) ? $(this.element) : domElm, $list = list ? list : this.options.data, len = $list !== null ? $list.length
                    : 0;
            if (len === 0) {
                return;
            }
            for (var i = 0; i < len; i++) {
                if ($list[i]) {
                    this.addItem(i, $list[i][this.options.id], $list[i][this.options.caption], el);
                }
            }
            if ((this.options.sortable && 2 <= $(domElm).children().length)) {
                $(domElm).sortable(); // 启用可拖动排序
            }
        },
        addItem : function(index, id, caption, domElm) {
            var itemElm = $("<div></div>").addClass("alert alert-info").attr("data-key", id).attr(
                    "data-value", caption);
            if (this.options.vertical) {
                itemElm.attr("style", "margin:5px;");
            } else {
                itemElm.attr("style", "float: left; margin:5px 0 0 5px;");
            }
            var _this = this;
            var closeElm = $("<button>×</button>").data("view-index", index).attr("type", "button")
                    .addClass("close").on("click", function() {
                        var sortable = _this.options.sortable;
                        $(this).parent().fadeOut(function() {
                            var parent = $(this).parent();
                            this.remove();
                            if (parent.children().length < 2) {
                                parent.sortable('disable');
                            }
                        });
                    });
            var textElm = $("<span></span>").text(caption);
            itemElm[0].appendChild(closeElm[0]);
            itemElm[0].appendChild(textElm[0]);

            domElm = $(domElm);
            domElm.append(itemElm);
            if (this.options.sortable && domElm.children().length >= 2) {
                domElm.sortable();
                domElm.sortable('enable'); // 启用可拖动排序
            }
        }
    };

    var methods = {
        init : function(option) {
            var args = arguments, result = null;
            $(this)
                    .each(
                            function(index, item) {
                                var $this = $(item), data = $this.data("multiSelect"), options = (typeof option !== 'object') ? null
                                        : option;
                                if (!data) {
                                    var domOptions = $this.attr("options");
                                    if (domOptions) { // 如果页面控件设置了options参数，合并参数值
                                        options = $.extend(options, $.parseJSON(domOptions.replace(
                                                /'/g, "\"")));
                                    }
                                    $this.data("multiSelect", (data = new MultiSelectView(this,
                                            options)));
                                    result = $.extend({
                                        "element" : data.element
                                    }, methods);
                                    return;
                                }
                                if (typeof option === 'string') {
                                    if (data[option]) {
                                        result = data[option].apply(data, Array.prototype.slice
                                                .call(args, 1));
                                    } else {
                                        throw "Method " + option + " does not exist";
                                    }
                                } else {
                                    result = data.setOptions(option);
                                }
                            });
            return result;
        },
        values : function() {
            var el = !this.element ? $(this) : this.element, strs = [];
            $(el).find(".modal-body").children().each(function() {
                strs.push($(this).attr("data-key"));
            });
            return strs.join(",");
        },
        getCaption : function() {
            var el = !this.element ? $(this) : this.element, strs = [];
            $(el).find(".modal-body").children().each(function() {
                strs.push($(this).attr("data-value"));
            });
            return strs.join(",");
        },
        getData : function() {
            var el = !this.element ? $(this) : this.element, data = [];
            $(el).find(".modal-body").children().each(function() {
                var key = $(this).attr("data-key");
                data.push({
                    key : key ? key : "",
                    value : $(this).attr("data-value")
                });
            });
            return data.length > 0 ? data : null;
        },
        empty : function() {
            var el = !this.element ? $(this) : this.element;
            $(el).find(".modal-body").children().remove();
        },
        data : function(list) {
            var el = !this.element ? $(this) : this, state = !this.element ? true : false;
            if ($.isArray(list)) {
                if (state) {
                    $(el).find(".modal-body").children().remove();
                    el = $(el).data("multiSelect");
                    el.addItems(list, $(el.element).find(".modal-body"));
                } else {
                    el.empty();
                    el = $(el.element).data("multiSelect");
                    el.addItems(list, $(this.element).children(".modal-body"));
                }
            }
        },
        add : function(caption) {
            var el = !this.element ? $(this) : this, multi = $(el.element).data("multiSelect");
            multi.addItem(null, null, caption, $(el.element).find(".modal-body"));
        },
        sort : function(enabled) {
            this.options.sortable = enabled;
            $(el.element).sortable();
            $(el.element).find(".modal-body").sortable(enabled ? 'enable' : 'disable');
        }
    };

    $.fn.multiselectView = function(method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            return $.error("Method " + method + " does not exist on plug-in: multiselect-view");
        }
    };

    $.fn.multiselectView.defaults = {
        title : "", // 显示标题
        btns : [ { // 自定义按钮数组
            icon : "icon-hand-up",
            tooltipTitles : null,
            click : null
        } ]
    };

})(jQuery);
