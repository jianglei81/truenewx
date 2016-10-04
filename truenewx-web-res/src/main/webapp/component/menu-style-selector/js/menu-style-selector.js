/**
 * menu-style-selector.js v1.0.0
 * 
 * Depends on: jquery.js
 * 
 * 图床上传插件
 */

(function($) {

    // ======================================UPLOAD=================================================
    var MenuStyleSelector = function(element, options) {
        this.init(element, options);
    };

    // 2级菜单UL样式
    var level2MenuCss = {
        "background-clip" : "padding-box",
        "background-color" : "#FFFFFF",
        "border" : "1px solid rgba(0, 0, 0, 0.2)",
        "border-radius" : "6px",
        "box-shadow" : "0 5px 10px rgba(0, 0, 0, 0.2)",
        "display" : "none",
        "float" : "left",
        "left" : "0",
        "list-style" : "none outside none",
        "min-width" : "160px",
        "padding" : "5px 0",
        "position" : "absolute",
        "top" : "100%",
        "z-index" : "1000"
    };

    // 2级菜单子菜单样式
    var level2MenuOptionCss = {
        "clear" : "both",
        "color" : "#333333",
        "display" : "block",
        "font-weight" : "normal",
        "line-height" : "20px",
        "padding" : "3px 20px",
        "white-space" : "nowrap"
    };
    // 2级子菜单鼠标选中样式
    var leve2MenuOptionHoverCss = {
        "background-color" : "#0081c2",
        "background-image" : "linear-gradient(to bottom,#0088CC,#0077B3)",
        "background-repeat" : "repeat-x",
        "color" : "#FFFFFF",
        "text-decoration" : "none"
    };

    MenuStyleSelector.prototype = {
        /**
         * 初始化控件
         */
        init : function(element, options) {
            this.element = element;
            this.setOptions(options);
        },

        /**
         * 初始化参数
         */
        setOptions : function(options) {
            var domOptions = this.element.attr("options");
            if (domOptions) { // 如果页面控件设置了options参数，合并参数值
                options = $.extend(options, $.parseJSON(domOptions.replace(/'/g, "\"")));
            }
            this.options = $.extend({}, $.fn.menuStyleSelector.defaults, options);
            this.render();
        },

        /**
         * 获得样式字符串
         */
        getCssString : function(style) {
            var styleString = "";
            $.map(style, function(val, key) {
                styleString += (key + ":" + val + ";");
            });
            return styleString;
        },
        /**
         * 渲染控件
         */
        render : function() {
            var $ele = $(this.element), oOptions = this.options;
            $ele.addClass("dropdown-toggle");
            $ele.attr("data-toggle", "dropdown");
            $ele.attr("data-target", "#");
            if (oOptions.textField == "" || oOptions.data == null) {
                return;
            }
            var _this = this;
            var level2MenuOptionStyle = this.getCssString(level2MenuOptionCss);
            var menu = "";
            // 如果当前json数据是树级json
            if (oOptions.subField != "") {
                var $json = $.parseJSON(oOptions.data);
                menu = " <ul class=\"dropdown-menu "+ oOptions.menuClass
                        + "\" role=\"menu\">";
                $json.each(function(item, i) {
                    menu += "<li to-level2='" + i + "' index=\"" + i + "\" role=\"presentation\" ";
                    if (oOptions.subField == "") {
                        menu += (oOptions.valueField == "" ? "" : "select-value='"
                                + eval("item." + oOptions.valueField) + "'");
                    }
                    menu += ">";
                    menu += "<a " + (oOptions.subField == "" ? "href='#'" : "") + ">";
                    menu += eval("item." + oOptions.textField);
                    menu += "</a>";
                    menu += "</li>";
                    // 产生二级菜单
                    if (oOptions.subField != "" && oOptions.textField != "") {
                        var level2Style = "margin-top:"
                                + (20 * i + (i == 0 ? 2 : i * 8))
                                + "px;"
                                + (oOptions.menuClass != "" ? "" : _this
                                        .getCssString(level2MenuCss));
                        var $level2Obj = eval("item." + oOptions.subField);
                        var level2Menu = "<ul class='" + oOptions.menuClass
                                + "' level2-menu=\"true\" style=\"" + level2Style
                                + "\" target-level1=\"" + i + "\">";
                        $level2Obj.each(function(item, i) {
                            level2Menu += "<li ";
                            level2Menu += (oOptions.valueField == "" ? "" : "select-value='"
                                    + eval("item." + oOptions.valueField) + "'")
                                    + ">";
                            level2Menu += "<a style=\""
                                    + (oOptions.menuClass != "" ? "" : level2MenuOptionStyle)
                                    + "\" href=\"#\">";
                            level2Menu += eval("item." + oOptions.textField);
                            level2Menu += "</a></li>";
                        });
                        level2Menu += "</ul>";
                        $ele.after(level2Menu);
                    }
                });
                menu += "</ul>";
                var $level1Obj = $(menu);
                $ele.after($level1Obj);
                $level1Obj.parents(".dropdown").find("ul[level2-menu='true']").css("margin-left",
                        $level1Obj.width());
            } else {
                menu = " <ul class=\"dropdown-menu " + oOptions.menuClass
                        + "\" role=\"menu\"></ul>";
                var $level1Obj = $(menu);
                $ele.after($level1Obj);
                $.each(oOptions.data, function(index, item) {
                    var parentId = null;
                    if (oOptions.parentField != "") {
                        parentId = eval("item." + oOptions.parentField);
                    }
                    // 如果是1级菜单
                    if (oOptions.parentField == "") {
                        var option = "<li to-level2=\"" + eval("item." + oOptions.valueField)
                                + "\"";
                        if (oOptions.parentField == "") {
                            option += (oOptions.valueField == "" ? "" : " select-value='"
                                    + eval("item." + oOptions.valueField) + "'");
                        }
                        option += ">";
                        option = option + "<a" + (oOptions.parentField == "" ? " href='#'" : "")
                                + ">";
                        option += eval("item." + oOptions.textField);
                        option += "</a>";
                        option += "</li>";
                        $level1Obj.append(option);
                    } else {
                        var $level2Obj = $("ul[level2-menu='true'][target-level1='" + parentId
                                + "']");
                        if ($level2Obj.size() == 0) {
                            var level2Menu = "<ul level2-menu=\"true\" target-level1=\"" + parentId
                                    + "\"></ul>";
                            $level2Obj = $(level2Menu);
                            $ele.after($level2Obj);
                        }
                        var option = "<li ";
                        option += (oOptions.valueField == "" ? "" : "select-value='"
                                + eval("item." + oOptions.valueField) + "'")
                                + ">";
                        option += "<a style=\""
                                + (oOptions.menuClass != "" ? "" : level2MenuOptionStyle)
                                + "\" href=\"#\">";
                        option += eval("item." + oOptions.textField);
                        option += "</a></li>";
                        $level2Obj.append(option);
                    }
                });
                // 梳理2级菜单ul的margin-top坐标
                $level1Obj.find("li").each(
                        function(index) {
                            $(this).attr("index", index);
                            var target = $(this).attr("to-level2");
                            var $level2Obj = $("ul[level2-menu='true'][target-level1='" + target
                                    + "']");
                            var level2Style = "margin-top:"
                                    + (20 * index + (index == 0 ? 2 : index * 8)) + "px;"
                                    + _this.getCssString(level2MenuCss);
                            $level2Obj.attr("style", level2Style);
                        });
                $level1Obj.parents(".dropdown").find("ul[level2-menu='true']").css("margin-left",
                        $level1Obj.width());
            }
            this.eventRegister();
        },

        /**
         * 注册事件
         */
        eventRegister : function() {
            var $ele = $(this.element), oOptions = this.options;
            var $level1Obj = $ele.parents(".dropdown").find(".dropdown-menu");
            if (oOptions.parentField == "" && oOptions.subField == "") {
                $level1Obj.find("li").unbind("click").on("click", function() {
                    var selectText = $(this).find("a").text();
                    $ele.text(selectText);
                    $ele.attr("select-value", $(this).attr("select-value"));
                    oOptions.clickMenuOptionCallBack(this);
                });
            } else {
                var $leve2Obj = $ele.siblings("ul[level2-menu='true']");
                $level1Obj.find("li").unbind("click").on("click", function(event) {
                    $(".dropdown").css("style", "block");
                });
                $(window).on("click", function(event) {
                    $("ul[level2-menu='true']").hide();
                });
                $level1Obj.find("a").unbind("mouseover").on("mouseover", function() {
                    var target = $(this).parent().attr("to-level2");
                    $(this).parents(".dropdown").find("ul[level2-menu='true']").hide();
                    $(this).parents(".dropdown").find("ul[target-level1='" + target + "']").show();
                    if (oOptions.hoverClass != "") {
                        $(this).addClass(oOptions.hoverClass);
                    }

                });
                $level1Obj.find("a").unbind("mouseout").on(
                        "mouseout",
                        function(event) {
                            var target = $(this).parent().attr("to-level2");
                            if (oOptions.hoverClass != "") {
                                $(this).removeClass(oOptions.hoverClass);
                            }
                            setTimeout(function() {
                                $(event.target).parents(".dropdown").find(
                                        "ul[target-level1='" + target + "']").hide();
                            }, 5);

                        });
                $leve2Obj.find("a").unbind("mouseover").on("mouseover", function(event) {
                    setTimeout(function() {
                        $(event.target).parents("ul").show();
                    }, 7);
                    if (oOptions.hoverClass != "") {
                        $(this).removeClass(oOptions.hoverClass);
                    } else {
                        $.map(leve2MenuOptionHoverCss, function(val, key) {
                            $(event.target).css(key, val);
                        });
                    }

                });
                $leve2Obj.find("a").unbind("mouseout").on("mouseout", function(event) {
                    if (oOptions.hoverClass != "") {
                        $(this).removeClass(oOptions.hoverClass);
                    } else {
                        $.map(leve2MenuOptionHoverCss, function(val, key) {
                            if (key == "color") {
                                $(event.target).css(key, "#333333");
                            } else {
                                $(event.target).css(key, "");
                            }
                        });
                    }
                });
                $leve2Obj.unbind("click").on("mouseout", function() {
                    $(this).hide();
                });
                $leve2Obj.find("li").unbind("click").on("click", function() {
                    var selectText = $(this).find("a").text();
                    if ($ele.is("input")) {
                        $ele.val(selectText);
                    } else {
                        $ele.text(selectText);
                    }
                    $ele.attr("select-value", $(this).attr("select-value"));
                    oOptions.clickMenuOptionCallBack(this);
                });
            }
        }
    };

    var methods = [];

    $.fn.menuStyleSelector = function(method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return new MenuStyleSelector(this, method);
        } else {
            return $.error("Method " + method + " does not exist on plug-in: menuStyleSelector");
        }
    };

    $.fn.menuStyleSelector.defaults = {
        textField : "",
        valueField : "",
        subField : "",
        parentField : "",
        menuClass : "",
        hoverClass : "",
        clickMenuOptionCallBack:function(obj){},
        data : null
    };

})(jQuery);