/**
 * label-options.js v1.0.0
 *
 * Depends on: jquery.js
 *
 * 标签型选项的选择器
 */
(function($) {

    var LabelOptions = function(element, options) {
        this.init(element, options);
    };

    LabelOptions.prototype = {
        themes : [ "primary", "info", "warning", "danger" ],
        init : function(element, options) {
            this.element = $(element);
            this.setOptions(options);
            this.render();
        },
        setOptions : function(options) {
            // 如果页面控件设置了options参数，合并参数值
            var domOptions = $.parseJSON(this.element.attr("options"), true);
            this.options = $.extend({}, $.fn.labelOptions.defaults, domOptions, options);
        },
        render : function() {
            this.element.addClass("label-options");
            var _this = this;
            $.each(this.element.children(), function(index, child) {
                child = $(child);
                child.addClass("option");
                _this.bindOptionEventHandler(child);
            });
            $.each(this.options.data, function(index, option) {
                _this.addOption(option);
            });
            this.element.data("labelOptions", this);
        },
        addOption : function(option) {
            var $option = this.buildOption(option);
            this.bindOptionEventHandler($option);
        },
        buildOption : function(option) {
            if (typeof (this.options.buildOption) == "function") {
                return this.options.buildOption.call(this.element, option);
            } else {
                var optionTag = this.getOptionTag();
                var $option = $("<" + optionTag + "></" + optionTag + ">");
                $option.attr("data-" + this.options.valueProperty,
                        option[this.options.valueProperty]);
                if (this.options.indexProperty) {
                    $option.attr("data-" + this.options.indexProperty,
                            option[this.options.indexProperty]);
                }
                $option.text(option[this.options.textProperty]);
                $option.addClass("option");
                this.element.append($option);
                return $option;
            }
        },
        getOptionTag : function() {
            var child = this.element.children()[0];
            if (child) {
                return child.tagName;
            } else {
                return this.options.optionTag || "span";
            }
        },
        bindOptionEventHandler : function($option) {
            var _this = this;
            $option.click(function() {
                if ($option.attr("theme")) {
                    _this.unselectOption($option);
                } else {
                    _this.selectOption($option);
                }
            });
        },
        _getOptionObj : function($option) {
            if (typeof ($option) == "object") {
                if (!($option instanceof jQuery)) {
                    $option = $($option);
                }
            } else { // 视为value取相应的选项对象
                $option = $("[data-" + this.options.valueProperty + "='" + $option + "']",
                        this.element);
            }
            return $option;
        },
        selectOption : function($option) {
            $option = this._getOptionObj($option);
            var theme = this.options.theme;
            $option.attr("theme", theme);
            if (!this.themes.includes(theme)) {
                $option.css({
                    borderColor : theme,
                    backgroundColor : theme
                });
            }
        },
        unselectOption : function($option) {
            $option = this._getOptionObj($option);
            var theme = $option.attr("theme");
            $option.removeAttr("theme");
            if (!this.themes.includes(theme)) {
                $option.css({
                    borderColor : "#d7d7d7",
                    backgroundColor : "#f3f3f3"
                });
            }
        },
        getSelectedData : function(type) {
            var _this = this;
            var data = [];
            $("[theme]", this.element).each(
                    function() {
                        var $option = $(this);
                        var option = {};
                        var value = $option.attr("data-" + _this.options.valueProperty);
                        if (type == "int") {
                            value = parseInt(value);
                        }
                        option[_this.options.valueProperty] = value;
                        option[_this.options.textProperty] = $option.text();
                        if (_this.options.indexProperty) {
                            option[_this.options.indexProperty] = $option.attr("data-"
                                    + _this.options.indexProperty);
                        }
                        data.push(option);
                    });
            return data;
        }
    };

    var methods = {
        addOption : function(option) {
            $(this).data("labelOptions").addOption(option);
        },
        selectOption : function($option) {
            $(this).data("labelOptions").selectOption($option);
        },
        unselectOption : function($option) {
            $(this).data("labelOptions").unselectOption($option);
        },
        getSelectedData : function(type) {
            return $(this).data("labelOptions").getSelectedData(type);
        }
    };

    $.fn.labelOptions = function(method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === "object" || !method) {
            return new LabelOptions(this, method);
        } else {
            return $.error("Method " + method + " does not exist on plug-in: LabelOptions");
        }
    };

    $.fn.labelOptions.defaults = {
        theme : "primary",
        defaultIcon : "plus",
        selectedIcon : "ok",
        valueProperty : "value", // 值的属性名
        textProperty : "text", // 显示文本的属性名
        indexProperty : undefined, // 索引的属性名，设置了才生成索引
        buildOption : undefined, // 自定义选项构建函数
        optionTag : undefined
    };

})(jQuery);