/**
 * editable-selector.js v1.0.0
 *
 * Depends on: jquery.js
 *
 * 可编辑的下拉菜单选择器组件
 */
(function($) {

    var EditableSelector = function(element, options) {
        this.init(element, options);
    };

    EditableSelector.prototype = {
        init : function(element, options) {
            this.element = $(element);
            this.setOptions(options);
            this.render();
        },
        setOptions : function(options) {
            // 如果页面控件设置了options参数，合并参数值
            var domOptions = $.parseJSON(this.element.attr("options"), true);
            this.options = $.extend({}, $.fn.editableSelector.defaults, domOptions, options);
        },
        render : function() {
            var div = $("<div></div>").addClass("input-group");
            var textElement = this._buildTextElement();
            div.append(textElement);
            var valueElement = this._buildValueElement();
            div.append(valueElement);

            var inputGroupBtn = $("<div></div>").addClass("input-group-btn");
            div.append(inputGroupBtn);
            var btn = $("<button type=\"button\"></button>").addClass(
                    "btn btn-default dropdown-toggle").attr("data-toggle", "dropdown");
            inputGroupBtn.append(btn);
            btn.append($("<span></span>").addClass("caret"));
            var ul = $("<ul></ul>").addClass("dropdown-menu dropdown-menu-right").attr("role",
                    "menu").css("overflow", "auto");
            inputGroupBtn.append(ul);
            $("option", this.element).each(function(index, option) {
                var li = $("<li></li>");
                var value = option.value;
                li.attr("value", value);

                option = $(option);
                var text = option.text();
                li.append($("<a href=\"javascript:void(0)\"></a>").append(text));
                li.click(function() {
                    textElement.val(text);
                    valueElement.val(value);
                });
                ul.append(li);
                if (option.is(":selected")) {
                    li.trigger("click");
                }
            });

            // 绑定文本输入框内容改变时的事件处理
            textElement.blur(function() {
                var text = textElement.val();
                var lis = $("li", div);
                for (var i = 0; i < lis.length; i++) {
                    var li = $(lis[i]);
                    if (text == li.find("a").text()) {
                        valueElement.val(li.attr("value"));
                        return;
                    }
                }
                valueElement.val("");
            });

            if (this.options.typeahead) {
                textElement.keyup(function(event) {
                    inputGroupBtn.removeClass("open");
                    var text = textElement.val();
                    if (text) {
                        var open = false;
                        $("li", ul).each(function(index, li) {
                            li = $(li);
                            li.show();
                            if ($("a", li).text().indexOf(text) >= 0) { // 输入值匹配
                                open = true;
                            } else {
                                li.hide();
                            }
                        });
                        if (open) {
                            inputGroupBtn.addClass("open");
                        }
                    }
                });

                inputGroupBtn.on('show.bs.dropdown', function() {
                    ul.outerWidth(textElement.outerWidth() + inputGroupBtn.outerWidth());
                    $("li", ul).show();
                });
            }

            this.element.replaceWith(div);

            var size = this.options.rows || parseInt(this.element.attr("rows")) || 10; // 默认最多显示10行
            var rowObj = $("li:first a:first", ul);
            var liHeight = parseInt(rowObj.css("lineHeight")) + parseInt(rowObj.css("paddingTop"))
                    + parseInt(rowObj.css("paddingBottom")); // 行高
            var maxHeight = parseInt(ul.css("borderTopWidth")) + parseInt(ul.css("paddingTop"))
                    + liHeight * size + parseInt(ul.css("paddingBottom"))
                    + parseInt(ul.css("borderBottomWidth"));
            if (maxHeight < ul.height()) {
                ul.css("maxHeight", maxHeight);
            }
        },
        /**
         * 构建文本输入框元素
         *
         * @param element
         *            原始下拉菜单元素
         * @returns 构建好的文本输入框元素
         */
        _buildTextElement : function() {
            var textOptions = this.options.textInput;
            if (textOptions) {
                if (typeof textOptions == "string") { // 字符串，则视为html，构造dom元素对象返回
                    return $(textOptions);
                } else if (textOptions instanceof jQuery) { // jquery对象，则视为dom元素对象返回
                    return textOptions;
                } else if (typeof textOptions == "object") { // 普通对象，则视为设定集，创建dom元素对象返回
                    var textElement = $("<input type=\"text\" />");
                    textElement.attr("autocomplete", "off");
                    textElement.attr("id", textOptions.id || this.element.attr("id"));
                    textElement.attr("name", textOptions.name
                            || (this.element.attr("name") + "Text")); // 编辑框名称默认为原始下拉框名称+Text
                    textElement.attr("class", textOptions.cssClass || this.element.attr("class"));
                    textElement.attr("style", textOptions.style || this.element.attr("style"));
                    textElement.attr("validation", textOptions.validation
                            || this.element.attr("validation"));
                    return textElement;
                }
            }
            return undefined;
        },
        /**
         * 构建值元素
         *
         * @param element
         *            原始下拉菜单元素
         * @returns 构建好的值元素
         */
        _buildValueElement : function() {
            var valueElement = $("<input type=\"hidden\" />");
            valueElement.attr("name", this.element.attr("name")); // 隐藏的值域名称默认为下拉框名称
            return valueElement;
        }
    };

    $.fn.editableSelector = function(method) {
        if (typeof method === "object" || method == undefined) {
        		if ($(this).length > 1) {
        			var selectors = new Array();
        			$(this).each(function () {
        				selectors.push(new EditableSelector(this, method));
        			});
        			return selectors;
        		} else {
        			return new EditableSelector(this, method);
        		}
        } else {
            return $.error("Method " + method + " does not exist in component: EditableSelector");
        }
    };

    $.fn.editableSelector.defaults = {
        typeahead : true, // 默认开启输入提示
        textInput : { // 可编辑时的文本输入框设定，该项为undefined时表示不可编辑
            id : undefined,
            name : undefined,
            cssClass : undefined,
            style : undefined,
            validation : undefined
        }
    };

})(jQuery);