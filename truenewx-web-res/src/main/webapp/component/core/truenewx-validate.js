/**
 * truenewx-validate.js v1.0.0
 * 
 * Depends on: truenewx.js
 */
// 为避免一行过宽导致整个文档格式缩进过多，单独设置Email的正则表达式
var _regExps_email = /^[a-zA-Z0-9_\-]([a-zA-Z0-9_\-\.]{0,62})@[a-zA-Z0-9_\-]([a-zA-Z0-9_\-\.]{0,62})$/;
// 注意：代码中不能出现int标识符，否则浏览器执行没问题，但eclipse格式化无法进行
$.tnx.Validator = Class.extend({
    checkers : {
        parent : undefined,
        regExps : {
            number : /^-?([1-9]\d{0,2}((,?\d{3})*|\d*)(\.\d*)?|0?\.\d*|0)$/,
            integer : /^(-?[1-9]\d{0,2}(,?\d{3}))|0*$/,
            email : _regExps_email,
            mobilePhone : /^1[34578]\d{9}$/,
            url : /^https?:\/\/[A-Za-z0-9]+(\.?[A-Za-z0-9_-]+)*(:[0-9]+)?(\/\S*)?$/
        },
        required : function(validationValue, fieldValue) {
            if (validationValue) {
                if (typeof (fieldValue) == "string") {
                    return fieldValue.length > 0;
                } else {
                    return fieldValue != undefined && fieldValue != null;
                }
            }
            return true; // 不要求必填，则检查通过
        },
        notBlank : function(validationValue, fieldValue) {
            if (typeof (fieldValue) == "string") {
                if (fieldValue.length > 0 && fieldValue.trim().length == 0) { // 为纯空格时校验失败
                    return false;
                } else { // 否则转为使用required规则
                    return "required";
                }
            }
            return true; // 非字符串值，视为检查通过
        },
        maxLength : function(validationValue, fieldValue) {
            var checker = function(validationValue, fieldValue) {
                if (typeof (validationValue) == "number") {
                    var enterLength = fieldValue.indexOf("\n") == -1 ? 0
                            : fieldValue.match(/\n/g).length;
                    return fieldValue.length != undefined
                            && (fieldValue.length + enterLength) <= validationValue;
                }
                return true; // 校验值不为数字无法校验，忽略该检查器
            };
            if (!checker(validationValue, fieldValue)) {
                var enterLength = fieldValue.indexOf("\n") == -1 ? 0
                        : fieldValue.match(/\n/g).length;
                return [ validationValue, fieldValue.length + enterLength - validationValue ];
            }
            return undefined;
        },
        minLength : function(validationValue, fieldValue) {
            var checker = function(validationValue, fieldValue) {
                if (typeof (validationValue) == "number") {
                    var enterLength = fieldValue.indexOf("\n") == -1 ? 0
                            : fieldValue.match(/\n/g).length;
                    return fieldValue.length != undefined && fieldValue.length != 0
                            && (fieldValue.length + enterLength) >= validationValue;
                }
                return true; // 校验值不为数字无法校验，忽略该检查器
            };
            if (!checker(validationValue, fieldValue)) {
                var enterLength = fieldValue.indexOf("\n") == -1 ? 0
                        : fieldValue.match(/\n/g).length;
                return [ validationValue, validationValue - (fieldValue.length + enterLength) ];
            }
            return undefined;
        },
        number : function(validationValue, fieldValue) {
            if (validationValue && fieldValue != "") {
                return typeof (fieldValue) == "number" || this.regExps.number.test(fieldValue);
            }
            return true; // 不要求为数字，则检查通过
        },
        integerLength : function(validationValue, fieldValue) {
            if (typeof (validationValue) == "number") { // 校验值为数字才可校验
                if (typeof (fieldValue) != "string") { // 字段值一律转换为字符串再进行校验
                    fieldValue = String(fieldValue);
                }
                if (this.regExps.number.test(fieldValue)) { // 字段值为数值才可进行校验
                    // 取整数部分
                    var index = fieldValue.indexOf(".");
                    if (index >= 0) {
                        fieldValue = fieldValue.substr(0, index);
                    }
                    // 去掉整数部分可能包含的逗号分隔符
                    fieldValue = fieldValue.replace(/,/g, "");

                    if (fieldValue.length > validationValue) {
                        return [ validationValue, fieldValue.length - validationValue ];
                    }
                }
            }
            return undefined;
        },
        scale : function(validationValue, fieldValue) {
            if (typeof (validationValue) == "number" && validationValue > 0) { // 校验值为数字且大于0才可校验
                if (typeof (fieldValue) != "string") { // 字段值一律转换为字符串再进行校验
                    fieldValue = String(fieldValue);
                }
                if (this.regExps.number.test(fieldValue)) { // 字段值为数值才可进行校验
                    var index = fieldValue.indexOf(".");
                    if (index >= 0) { // 存在小数部分才校验
                        fieldValue = fieldValue.substr(index + 1); // 取小数部分

                        if (fieldValue.length > validationValue) {
                            return [ validationValue, fieldValue.length - validationValue ];
                        }
                    }
                }
            }
            return undefined;
        },
        integer : function(validationValue, fieldValue) {
            if (validationValue && fieldValue != "") {
                // 本身为数值时，四舍五入为整数时与原值相等，说明其为整数
                return (typeof (fieldValue) == "number" && Math.ceil(fieldValue) == fieldValue)
                        || this.regExps.integer.test(fieldValue);
            }
            return true; // 不要求为整数，则检查通过
        },
        maxValue : function(validationValue, fieldValue) {
            if (typeof (validationValue) == "number") {
                if (typeof (fieldValue) == "number") {
                    return fieldValue <= validationValue;
                } else if (this.regExps.number.test(fieldValue)) {
                    return Number(fieldValue) <= validationValue;
                } // 字段值不为数字无法校验，忽略该检查器
            }
            return true; // 校验值不为数字无法校验，忽略该检查器
        },
        minValue : function(validationValue, fieldValue) {
            if (typeof (validationValue) == "number") {
                if (typeof (fieldValue) == "number") {
                    return fieldValue >= validationValue;
                } else if (this.regExps.number.test(fieldValue)) {
                    return Number(fieldValue) >= validationValue;
                } // 字段值不为数字无法校验，忽略该检查器
            }
            return true; // 校验值或字段值不为数字无法校验，忽略该检查器
        },
        regex : function(validationValue, fieldValue) {
            var checker = function(expression, fieldValue) {
                if (expression && fieldValue != "") {
                    return new RegExp(expression).test(fieldValue);
                }
                return true;
            };
            var expression = validationValue;
            var message = "";
            if ($.isArray(validationValue)) {
                if (validationValue.length > 0) {
                    expression = validationValue[0];
                    if (validationValue.length > 1) {
                        message = "，" + validationValue[1];
                    }
                } else {
                    expression = undefined;
                }
            }
            if (!checker(expression, fieldValue)) {
                return [ message ];
            }
            return undefined;
        },
        email : function(validationValue, fieldValue) {
            if (validationValue && fieldValue != "") {
                return this.regExps.email.test(fieldValue);
            }
            return true;
        },
        mobilePhone : function(validationValue, fieldValue) {
            if (validationValue && fieldValue != "") {
                return this.regExps.mobilePhone.test(fieldValue);
            }
            return true;
        },
        url : function(validationValue, fieldValue) {
            if (validationValue && fieldValue != "") {
                return this.regExps.url.test(fieldValue);
            }
            return true;
        },
        notContains : function(validationValue, fieldValue) {
            if (validationValue && fieldValue != "") {
                var values = validationValue.split(" ");
                for (var i = 0; i < values.length; i++) {
                    if (fieldValue.indexOf(values[i]) >= 0) {
                        return false;
                    }
                }
            }
            return true;
        },
        notContainsAngleBracket : function(validationValue, fieldValue) {
            if (validationValue && fieldValue != "") {
                var values = [ "<", ">" ];
                for (var i = 0; i < values.length; i++) {
                    if (fieldValue.indexOf(values[i]) >= 0) {
                        return false;
                    }
                }
            }
            return true;
        },
        notContainsHtmlChars : function(validationValue, fieldValue) {
            if (validationValue && fieldValue != "") {
                var values = [ "<", ">", "'", "\"" ];
                for (var i = 0; i < values.length; i++) {
                    if (fieldValue.indexOf(values[i]) >= 0) {
                        return false;
                    }
                }
            }
            return true;
        },
        rejectTags : function(validationValue, fieldValue) {
            if (validationValue && fieldValue != "") {
                fieldValue = fieldValue.trim();
                var regExp = new RegExp("^.*<[a-z]+.*>.*$", "i");
                return !regExp.test(fieldValue);
            }
        },
        allowedTags : function(validationValue, fieldValue) {
            if (validationValue && fieldValue != "") {
                fieldValue = fieldValue.trim();
                var tags = validationValue.split(",");
                var leftIndex = fieldValue.indexOf("<");
                var rightIndex = leftIndex >= 0 ? fieldValue.indexOf(">", leftIndex) : -1;
                while (leftIndex >= 0 && rightIndex >= 0) {
                    var sub = fieldValue.substring(leftIndex + 1, rightIndex); // <>中间的部分
                    var spaceIndex = sub.indexOf(" ");
                    var tag = spaceIndex >= 0 ? sub.substring(0, spaceIndex) : sub;
                    if (tag.startsWith("/")) {
                        tag = tag.substr(1);
                    }
                    if (tags.indexOf(tag.toLowerCase()) < 0) {
                        return false; // 存在不允许的标签，则直接返回false
                    }
                    leftIndex = fieldValue.indexOf("<", rightIndex);
                    rightIndex = leftIndex >= 0 ? fieldValue.indexOf(">", leftIndex) : -1;
                }
            }
            return true;
        },
        forbiddenTags : function(validationValue, fieldValue) {
            if (validationValue && fieldValue != "") {
                fieldValue = fieldValue.trim().toLowerCase();
                var tags = validationValue.split(",");
                for (var i = 0; i < tags.length; i++) {
                    var tag = tags[i];
                    if (fieldValue.indexOf("<" + tag + ">") > -1
                            || fieldValue.indexOf("<" + tag + " ") > -1) {
                        return false;
                    }
                }
            }
            return true;
        }
    },
    messages : {
        required : "{0}不能为空",
        notBlank : "{0}不能为空或纯空格",
        maxLength : "{0}长度最多可以有{1}位，已超出{2}位",
        minLength : "{0}长度最少必须有{1}位，还缺少{2}位",
        number : "{0}必须为数字",
        integer : "{0}必须为整数",
        integerLength : "{0}整数位最多可以有{1}位，已超出{2}位",
        scale : "{0}小数位最多可以有{1}位，已超出{2}位",
        maxValue : "{0}最大可以为{1}",
        minValue : "{0}最小可以为{1}",
        email : "{0}只能包含字母、数字、下划线、-和.，@两边各自的长度应小于64",
        mobilePhone : "{0}只能是以1开头的11位数字手机号码",
        url : "{0}应为格式正确的网址链接",
        regex : "{0}格式错误{1}",
        notContains : "{0}不能包含：{1}",
        notContainsAngleBracket : "{0}不能包含：< >",
        notContainsHtmlChars : "{0}不能包含：< > \" '",
        rejectTags : "{0}不能包含任何标签",
        allowedTags : "{0}只能包含标签：{1}",
        forbiddenTags : "{0}不能包含标签：{1}"
    },
    /**
     * 扩展校验
     * 
     * @param name
     *            校验名
     * @param message
     *            错误消息
     * @param check
     *            检查函数或正则表达式
     */
    extend : function(name, message, checker) {
        this.messages[name] = message;
        if (typeof (checker) == "function") {
            this.checkers[name] = check;
        } else if (checker instanceof RegExp) { // 为正则表达式时生成正则表达式检查函数
            this.checkers.regExps[name] = check;
            var validator = this;
            this.checkers[name] = function(validationValue, fieldValue) {
                if (validationValue && fieldValue != "") {
                    return validator.checkers.regExps[name].test(fieldValue);
                }
                return true;
            };
        }
    },
    getErrorMessage : function(fieldObj, validationName, validationValue) {
        var message = this.messages[validationName];
        if (message) {
            var caption = undefined;
            if (!this.getFieldErrorObj(fieldObj)) { // 字段没有对应错误显示对象，才在消息中显示字段名
                caption = fieldObj.attr("caption");
            }
            if (!caption) {
                caption = "";
            }
            var args = [ caption ];
            if ($.isArray(validationValue)) { // 拼接数组
                args = args.concat(validationValue);
            } else {
                args.push(validationValue);
            }
            return message.format.apply(message, args);
        }
        return undefined;
    },
    getFieldValidation : function(fieldObj) {
        var validation = fieldObj.attr("validation");
        if (validation == "") { // 空字符视为有校验设置，但设置项均为空
            validation = {};
        } else if (validation) {
            try {
                validation = $.parseJSON(validation.trim().replace(/'/g, "\""));
                if ($.isArray(validation)) { // 规则为数组则合并
                    var v = {};
                    validation.each(function(obj) {
                        $.extend(v, obj);
                    });
                    validation = v;
                }
            } catch (e) {
                validation = undefined;
            }
        }
        return validation;
    },
    _markFieldError : function(formObj, fieldObj) {
        if (formObj.data("firstErrorFieldObj") == undefined) {
            formObj.data("firstErrorFieldObj", fieldObj); // 校验未通过，标记表单有错误
        }
        formObj.attr("validateError", "true");
    },
    validateField : function(fieldObj) {
        if (!(fieldObj instanceof jQuery)) {
            fieldObj = $(fieldObj);
        }
        var formObj = fieldObj.parents("form");
        // 先隐藏字段正确和错误提示框
        this.hideFieldCorrect(fieldObj);
        this.hideFieldErrors(fieldObj);

        if (fieldObj.prop("disabled")) { // 禁用组件不做验证
            return undefined;
        }

        var validation = this.getFieldValidation(fieldObj);
        var fieldValue = fieldObj.val() || fieldObj.text();
        var validator = this;
        var errorMessages = [];
        if (validation) {
            $.each(validation,
                    function(validationName, validationValue) {
                        // 不能为空的校验规则在有最小长度限制时无效
                        if ((validationName == "required" || validationName == "notBlank")
                                && validation.minLength) {
                            return;
                        }
                        var checker = validator.checkers[validationName];
                        if (checker) {
                            var checkResult = checker.call(validator.checkers, validationValue,
                                    fieldValue);
                            var message = undefined;
                            if (typeof (checkResult) == "boolean") { // 若检查方法返回布尔值，则按默认规则格式化错误消息
                                if (!checkResult) {
                                    validator._markFieldError(formObj, fieldObj);
                                    message = validator.getErrorMessage(fieldObj, validationName,
                                            validationValue);
                                    if (message) {
                                        errorMessages.push(message);
                                    }
                                }
                            } else if (typeof (checkResult) == "string") { // 若检查方法返回字符串，则转为使用该字符串表示的校验规则
                                arguments.callee(checkResult, validationValue);
                            } else if ($.isArray(checkResult)) { // 若检查方法返回数组，则将数组作为消息格式化参数
                                validator._markFieldError(formObj, fieldObj);
                                message = validator.getErrorMessage(fieldObj, validationName,
                                        checkResult);
                                if (message) {
                                    errorMessages.push(message);
                                }
                            }
                        }
                    });
        }
        var additionalErrorMessage = this.getFieldErrorMessage(fieldObj, errorMessages.length);
        if (additionalErrorMessage) { // 添加附加错误消息
            if ($.isArray(additionalErrorMessage)) {
                errorMessages.concat(additionalErrorMessage);
            } else {
                errorMessages.push(additionalErrorMessage);
            }
        }
        if (errorMessages.length > 0) { // 存在错误
            errorMessages = this.showFieldErrors(fieldObj, errorMessages); // 返回处理完后剩余的错误消息
            validator._markFieldError(formObj, fieldObj);
            return errorMessages;
        } else {
            this.showFieldCorrect(fieldObj);
        }
        return undefined;
    },
    /**
     * 获取校验字段的错误消息。为对外提供的插入校验的入口，可通过自定义该方法实现附加的字段校验
     */
    getFieldErrorMessage : function(fieldObj, errorCount) {
        return undefined;
    },
    /**
     * 从错误显示区域对象中获取错误文本显示对象
     */
    getErrorTextObj : function(errorObj) {
        var errorTextSelector = errorObj.attr("error-text-selector");
        if (errorTextSelector) {
            return $(errorTextSelector, errorObj);
        }
        return errorObj;
    },
    showErrorObj : function(errorObj, errorMessages) {
        if (!$.isArray(errorMessages)) {
            errorMessages = [ errorMessages ];
        }
        var errorTextObj = this.getErrorTextObj(errorObj);
        errorTextObj.html(errorMessages.join("<br/>"));
        var children = errorTextObj.children().not("br");
        if (children.length) {
            errorTextObj.prepend(children);
        }
        errorObj.show();
    },
    getFieldTipObj : function(fieldObj, type) {
        if (!(fieldObj instanceof jQuery)) {
            fieldObj = $(fieldObj);
        }
        var tipId = fieldObj.attr(type.firstToLowerCase() + "Id");
        if (!tipId) {
            var fieldName = fieldObj.attr("name");
            if (!fieldName) {
                fieldName = fieldObj.attr("id");
            }
            if (fieldName) {
                tipId = fieldName.replace(/\./g, "_") + type.firstToUpperCase();
            }
        }
        if (tipId) {
            var tipObj = $("#" + tipId);
            if (tipObj.length) {
                return tipObj;
            }
        }
        return undefined;
    },
    getFieldErrorObj : function(fieldObj) {
        return this.getFieldTipObj(fieldObj, "Error");
    },
    showFieldErrors : function(fieldObj, errorMessages) {
        // 显示错误提示框前先隐藏正确提示框
        this.hideFieldCorrect(fieldObj);

        var errorObj = this.getFieldErrorObj(fieldObj);
        if (errorObj) {
            this.showErrorObj(errorObj, errorMessages);
            return undefined; // 表示已完成错误消息显示
        }
        return errorMessages; // 交由表单错误消息一起处理
    },
    hideFieldErrors : function(fieldObj) {
        var errorObj = this.getFieldErrorObj(fieldObj);
        if (errorObj) {
            errorObj.hide();
        }
    },
    getFieldCorrectObj : function(fieldObj) {
        return this.getFieldTipObj(fieldObj, "Correct");
    },
    showFieldCorrect : function(fieldObj) {
        var _this = this;
        // 显示正确提示框前先隐藏错误提示框
        this.hideFieldErrors(fieldObj);

        var correctObj = this.getFieldCorrectObj(fieldObj);
        if (correctObj) {
            correctObj.show();
        }
    },
    hideFieldCorrect : function(fieldObj) {
        var correctObj = this.getFieldCorrectObj(fieldObj);
        if (correctObj) {
            correctObj.hide();
        }
    },
    validateForm : function(formObj) {
        if (!(formObj instanceof jQuery)) {
            formObj = $(formObj);
        }
        formObj.removeData("firstErrorFieldObj"); // 初始化表单无错误
        formObj.removeAttr("validateError");
        this.hideFormErrors(formObj, true); // 先隐藏所有错误框
        var validator = this;
        var formErrorMessages = [];
        var fieldObjs = $("[validation]", formObj);
        $.each(fieldObjs, function(i, fieldObj) {
            var fieldErrorMessages = validator.validateField(fieldObj);
            if (fieldErrorMessages) {
                formErrorMessages = formErrorMessages.concat(fieldErrorMessages);
            }
        });
        formErrorMessages = formErrorMessages.unique();
        this.showFormErrors(formObj, formErrorMessages);
        var firstErrorFieldObj = formObj.data("firstErrorFieldObj");
        if (firstErrorFieldObj) {
            firstErrorFieldObj.focus();
        }
        return !this.hasFormError(formObj); // 表单无错误视为校验通过
    },
    hasFormError : function(formObj) {
        return formObj.attr("validateError") == "true";
    },
    getFormErrorObj : function(formObj) {
        var errorId = formObj.attr("errorId");
        if (!errorId) {
            var formId = formObj.attr("id");
            if (formId) {
                errorId = formId + "Error";
            }
        }
        if (errorId) {
            var errorObj = $("#" + errorId);
            if (errorObj.length) {
                return errorObj;
            }
        }
        return undefined;
    },
    showFormErrors : function(formObj, formErrorMessages) {
        if (formErrorMessages.length) {
            var errorObj = this.getFormErrorObj(formObj);
            if (errorObj) {
                this.showErrorObj(errorObj, formErrorMessages);
            } else {
                $.tnx.alert(formErrorMessages.join("<br/>"), "错误");
            }
        }
    },
    hideFormErrors : function(formObj, hideFieldErrors) {
        var errorObj = this.getFormErrorObj(formObj);
        if (errorObj) {
            errorObj.hide();
        }
        if (hideFieldErrors) {
            var validator = this;
            $.each($("[validation]", formObj), function(i, fieldObj) {
                validator.hideFieldCorrect(fieldObj);
                validator.hideFieldErrors(fieldObj);
            });
        }
    },
    getRequiredTag : function(fieldObj) {
        var requiredTag = fieldObj.attr("required-tag");
        if (requiredTag == undefined) { // 字段上无必填显示标签，则从所属表单上获取
            var formObj = fieldObj.parents("form");
            if (formObj.length > 0) {
                requiredTag = formObj.attr("required-tag");
            }
            if (requiredTag == undefined) { // 所属表单上无必填显示标签，则从BODY上取
                requiredTag = $("body").attr("required-tag");
                if (requiredTag == undefined) { // BODY无必填显示标签，则从站点框架上取
                    requiredTag = $.tnx.domain.site.requiredTag;
                    if (requiredTag == undefined) {
                        requiredTag = "span";
                    }
                    $("body").attr("required-tag", requiredTag);
                }
                formObj.attr("required-tag", requiredTag);
            }
        }
        return requiredTag;
    },
    getRequiredClass : function(fieldObj) {
        var requiredClass = fieldObj.attr("required-class");
        if (requiredClass == undefined) { // 字段上无必填样式，则从所属表单上获取
            var formObj = fieldObj.parents("form");
            if (formObj.length > 0) {
                requiredClass = formObj.attr("required-class");
            }
            if (requiredClass == undefined) { // 所属表单上无必填样式，则从BODY上取
                requiredClass = $("body").attr("required-class");
                if (requiredClass == undefined) { // BODY无必填样式，则从站点框架上取
                    requiredClass = $.tnx.domain.site.requiredClass;
                    $("body").attr("required-class", requiredClass);
                }
                formObj.attr("required-class", requiredClass);
            }
        }
        return requiredClass;
    },
    appendRequiredTag : function(fieldObj, requriedTag, requiredClass) {
        if (requriedTag) {
            var appendObj = $("<" + requriedTag + "></" + requriedTag + ">")
                    .addClass(requiredClass);
            var requiredAppend = fieldObj.attr("required-append");
            if (requiredAppend == undefined) { // 字段上无附加函数，则从所属表单上获取
                var formObj = $(fieldObj.from);
                requiredAppend = formObj.attr("required-append");
                if (requiredAppend == undefined) { // 所属表单上无附加函数，则从BODY上获取
                    requiredAppend = $("body").attr("required-append");
                    if (requiredAppend == undefined) { // BODY无附加函数，则从站点框架上取
                        if (typeof ($.tnx.domain.site.requiredAppend) == "function") {
                            requiredAppend = "$.tnx.domain.site.requiredAppend";
                            $("body").attr("required-append", requiredAppend);
                        }
                    }
                    formObj.attr("required-append", requiredAppend);
                }
            }

            var funcRequiredAppend;
            if (requiredAppend) {
                eval("funcRequiredAppend = " + requiredAppend);
            } else { // 都未指定附加函数，则使用默认的附加函数
                funcRequiredAppend = function(fieldObj, appendObj) {
                    var parent = fieldObj.parent();
                    if (parent.hasClass("input-group")) {
                        parent.append(appendObj);
                    } else {
                        fieldObj.after(appendObj);
                    }
                }
            }
            funcRequiredAppend(fieldObj, appendObj);
        }
    },
    render : function() {
        var validator = this;
        var formObj = $("form[validate='true']");
        formObj.submit(function(event) {
            return validator.validateForm(this);
        });

        var validatableFields = $(
                "input[validation],textarea[validation],[contenteditable='true'][validation]",
                formObj);

        validatableFields.focusout(function() {
            var fieldObj = $(this);
            if (validator.getFieldErrorObj(fieldObj)) { // 字段存在对应错误消息显示对象，则进行字段校验
                validator.validateField(fieldObj);
            }
        });

        validatableFields.each(function() {
            var fieldObj = $(this);
            var validation = validator.getFieldValidation(fieldObj);
            if (validation && (validation.required == true || validation.notBlank == true)) {
                var requiredClass = validator.getRequiredClass(fieldObj);
                if (requiredClass && requiredClass != "false") {
                    var requriedTag = validator.getRequiredTag(fieldObj);
                    validator.appendRequiredTag(fieldObj, requriedTag, requiredClass);
                }
            }
        });

        formObj.attr("validate", "false"); // 避免重复初始化
    },
    init : function() {
        this.checkers.parent = this;
        this.render();
    }
});

$(function() {
    $.tnx.validator = new $.tnx.Validator();
});
