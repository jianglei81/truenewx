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
                var value = child.attr("data-" + _this.options.valueProperty);
                if (_this.requiresSelect(value)) {
                    _this.selectOption(child);
                }
            });
            if (this.options.data instanceof Array) {
                $.each(this.options.data, function(index, option) {
                    _this.addOption(option);
                });
            }
            if (this.options.filterInput) { // 如果指定了过滤输入器，则绑定过滤输入器事件
                var filterInput = $(this.options.filterInput);
                filterInput.keyup(function() {
                    var condition = $(this).val();
                    _this.filter(condition);
                });
            }
            this.element.removeClass("hidden");
            if (typeof (this.options.onRendered) == "function") {
                this.options.onRendered.call(this.element);
            }
            this.element.data("labelOptions", this);
        },
        findByText : function(text) {
            var options = [];
            $(".option", this.element).each(function() {
                var $option = $(this);
                if ($option.text() == text) {
                    options.push($option);
                }
            });
            if (options.length == 0) {
                return undefined;
            } else if (options.length == 1) {
                return options[0];
            } else {
                return options;
            }
        },
        addOption : function(option, selected) {
            var $option = this.buildOption(option);
            this.bindOptionEventHandler($option);
            if (selected == undefined) {
                var value = option[this.options.valueProperty];
                selected = this.requiresSelect(value);
            }
            if (selected) {
                this.selectOption($option);
            }
            return $option;
        },
        requiresSelect : function(value) {
            if (value == undefined || value == null) {
                return false;
            }
            var selectedValues;
            if (this.options.selectedValues instanceof Array) { // 已选值数组
                selectedValues = this.options.selectedValues;
            } else if (typeof (this.options.selectedValues) == "function") { // 获取已选值数组的函数
                selectedValues = this.options.selectedValues();
            } else if (typeof (this.options.selectedValues) == "string") { // 承载已选值的DOM元素的选择器
                selectedValues = [];
                $(this.options.selectedValues).each(function() {
                    selectedValues.push($(this).val());
                });
            }
            if (selectedValues instanceof Array) {
                return selectedValues.findIndex(value)>-1
            }
            return false;
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
            if (!this.themes.findIndex(theme)>-1) {
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
            if (!this.themes.findIndex(theme)>-1) {
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
        },
        getSelectedValues : function(type) {
            var _this = this;
            var values = [];
            $("[theme]", this.element).each(function() {
                var $option = $(this);
                var value = $option.attr("data-" + _this.options.valueProperty);
                if (type == "int") {
                    value = parseInt(value);
                }
                values.push(value);
            });
            return values;
        },
        filter : function(condition) {
            if (condition) {
                var chars = [];
                for (var i = 0; i < condition.length; i++) {
                    chars[i] = condition.substr(i, 1);
                }
                var _this = this;
                var options = $(".option:not([theme])", this.element);
                options.each(function() {
                    var $option = $(this);
                    var text = $option.text();
                    var index = _this.options.indexProperty ? $option.attr("data-"
                            + _this.options.indexProperty) : undefined;
                    if (_this.matches(text, chars) || _this.matches(index, chars)) {
                        $option.removeClass("hidden");
                    } else {
                        $option.addClass("hidden");
                    }
                });
            } else {
                $("[data-" + this.options.indexProperty + "]", this.element).removeClass("hidden");
            }
        },
        matches : function(s, chars) {
            if (s) {
                var index = -1;
                for (var i = 0; i < chars.length; i++) {
                    index = s.indexOf(chars[i], index + 1);
                    if (index < 0) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    };

    var methods = {
        findByText : function(text) {
            return $(this).data("labelOptions").findByText(text);
        },
        add : function(option, selected) {
            return $(this).data("labelOptions").addOption(option, selected);
        },
        select : function($option) {
            var labelOptions = $(this).data("labelOptions");
            if ($option instanceof Array) {
                $.each($option, function() {
                    labelOptions.selectOption(this);
                });
            } else {
                return labelOptions.selectOption($option);
            }
        },
        unselect : function($option) {
            var labelOptions = $(this).data("labelOptions");
            if ($option instanceof Array) {
                $.each($option, function() {
                    labelOptions.unselectOption(this);
                });
            } else {
                return labelOptions.unselectOption($option);
            }
        },
        getSelected : function(type) {
            return $(this).data("labelOptions").getSelectedData(type);
        },
        getSelectedValues : function(type) {
            return $(this).data("labelOptions").getSelectedValues(type);
        },
        filter : function(condition) {
            $(this).data("labelOptions").filter(condition);
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
        optionTag : undefined,
        data : [], // 加入的数据清单
        selectedValues : [], // 初始选中的值清单
        filterInput : undefined, // 过滤输入器，可以是jQuery对象，DOM元素，或者字符串型的选择器
        onRendered : function() { // 渲染完之后的事件处理函数
        }
    };

})(jQuery);