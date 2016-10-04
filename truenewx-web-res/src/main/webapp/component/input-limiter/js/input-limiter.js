/**
 * input-limiter.js v1.0.0
 *
 * Depends on: truenewx.js
 *
 * 输入限制器
 */
$.tnx.namespace("$.tnx.component");

(function($) {
    $.extend($.tnx.component, {
        InputLimiter : {
            functions : {
                int : function() {
                    var value = $.tnx.component.InputLimiter.replaceValue.call(this, /[^-0-9]/g);
                    this.value = $.tnx.component.InputLimiter.limitFirstMinus(value);
                },
                number : function() {
                    var value = $.tnx.component.InputLimiter.replaceValue.call(this, /[^-0-9.]/g);
                    value = $.tnx.component.InputLimiter.limitFirstMinus(value);
                    this.value = $.tnx.component.InputLimiter.limitOneDot(value);
                },
                nonnegativeInt : function() {
                    var value = $.tnx.component.InputLimiter.replaceValue.call(this, /[^-0-9]/g);
                    this.value = $.tnx.component.InputLimiter.limitMinus(value);
                },
                nonnegativeNumber : function() {
                    var value = $.tnx.component.InputLimiter.replaceValue.call(this, /[^-0-9.]/g);
                    value = $.tnx.component.InputLimiter.limitMinus(value);
                    this.value = $.tnx.component.InputLimiter.limitOneDot(value);
                }
            },
            replaceValue : function(regex) {
                this.value = this.value.replace(regex, "");
                return this.value;
            },
            limitFirstMinus : function(value) {
                var index = value.lastIndexOf("-");
                if (index > 0) { // 存在不在首位的负号
                    var array = value.split("-"); // 因为存在负号，数组大小至少为2
                    if (array[0].length == 0) {
                        value = "-";
                    } else {
                        value = array[0];
                    }
                    for (var i = 1; i < array.length; i++) {
                        value += array[i];
                    }
                }
                return value;
            },
            limitMinus : function(value) {
                var index = value.lastIndexOf("-");
                if (index > -1) { // 存在负号
                    var array = value.split("-"); // 因为存在负号，数组大小至少为2
                    value = "";
                    for (var i = 0; i < array.length; i++) {
                        value += array[i];
                    }
                }
                return value;
            },
            limitOneDot : function(value) {
                var index = value.indexOf(".");
                if (index == 0) { // 首位小数点前补0
                    value = "0" + value;
                }
                var array = value.split(".");
                if (array.length > 2) { // 多于1个小数点
                    value = array[0] + "." + array[1];
                    for (var i = 2; i < array.length; i++) {
                        value += array[i];
                    }
                }
                return value;
            }
        }
    });

    $.fn.limitInput = function(options) {
        var fn = undefined;
        if (typeof options == "function") {
            fn = options;
        } else if (typeof options == "string") {
            if ($.tnx.component.InputLimiter.functions[options]) {
                options = {
                    type : options
                };
            } else {
                options = {
                    regex : new RegExp(options, "g")
                };
            }
        } else if (options instanceof RegExp) {
            options = {
                regex : options
            };
        } else if (!options) {
            options = {};
        }
        if (!fn) {
            if (typeof options.regex == "string") {
                options.regex = new RegExp(options.regex, "g");
            }
            if (!options.type && !options.regex) { // 未指定类型和正则表达式，则从校验规则中取相关规则
                var validation = this.attr("validation");
                if (validation) {
                    try {
                        validation = $.parseJSON(validation.trim().replace(/'/g, "\""));
                    } catch (e) {
                        validation = undefined;
                    }
                }
                if (validation) {
                    if (validation.int) {
                        if (validation.minValue != undefined && validation.minValue >= 0) {
                            options.type = "nonnegativeInt";
                        } else {
                            options.type = "int";
                        }
                    } else if (validation.number) {
                        if (validation.minValue != undefined && validation.minValue >= 0) {
                            options.type = "nonnegativeNumber";
                        } else {
                            options.type = "number";
                        }
                    } else if (validation.mobilePhone) {
                        options.regex = /[^0-9]/g;
                    }
                }
            }
        }
        if (options.type) {
            fn = $.tnx.component.InputLimiter.functions[options.type];
        } else if (options.regex) {
            fn = function() {
                $.tnx.component.InputLimiter.replaceValue.call(this, options.regex);
            };
        }
        if (fn) {
            this.keyup(fn);
        }
    };

})(jQuery);
