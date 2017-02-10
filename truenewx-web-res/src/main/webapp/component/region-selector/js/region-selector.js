/**
 * region-selector.js v1.0.0
 */

(function($) {

    var isIE = navigator.userAgent.indexOf('MSIE') != -1;

    var Region = function(element, options) {
        this.init(element, options);
    };

    Region.prototype = {
        init : function(element, options) {
            this.element = element;
            this.setOptions(options);
        },
        setOptions : function(options) {
            this.options = $.extend({
                regionName : null, // 隐藏域name值，用作提交时取值
                defaultCountry : "CN", // 默认国家代码
                startLevel : 1, // 起始层级
                endLevel : 4, // 结束层级
                nullLevel : null, // 设置从第几级开始，允许为空；默认都不可为空
                isAutoChange : true,
                subHideByNotGroundSon : false, // 没有孙子级地区时，子级也不显示。
                                                // 开关(false：不开启，true：开启)
                changeCallBack : function(hasSub) {
                },
                intnull : false,
                regionData : null,
                limits : {}
            }, options);
            this.initData();
        },
        initData : function() { // 地区数据初始化
            this.rpc = $.isf.rpc.imports("regionController");
            if (!this.options.regionData) { // 判断没有初始化地区时，去服务器取数据
                if (this.options.limits.length > 0) {
                    this.options.regionData = this.rpc.getLimits(this.options.limits);
                } else {
                    this.options.regionData = this.rpc.getAll();
                }
            }
            if (this.options.regionData) {
                this.data = this.options.regionData;
            } else {
                return $.error("无法获取地区数据，请刷新后重试！");
            }
            this.codes = this.loadDefaultCodes($(this.element).val());
            this.render();
        },
        loadDefaultCodes : function(val) { // 获取默认值
            if (val) {
                return this.rpc.getParentCodes(val).add(val);
            } else {
                return null;
            }
        },
        render : function() { // 渲染页面效果
            var opt = this.options, len = opt.endLevel - opt.startLevel;
            if (opt.startLevel === 3 || opt.startLevel === 4) {
                return;
            }
            if (opt.startLevel === 2) {
                if (opt.defaultCountry) {
                    this.initSelectElm(eval("this.data." + opt.defaultCountry + ".subs"),
                            opt.startLevel, false);
                    for (var i = 2; i <= len + 1; i++) {
                        if (this.codes && this.codes[i]) {
                            var str = [];
                            for (var j = 0; j < i; j++) {
                                str.push(this.codes[j]);
                            }
                            this.initSelectElm(eval("this.data." + str.join(".subs.") + ".subs"),
                                    i + 1, false);
                        } else {
                            this.initSelectElm(null, i + 1, true);
                        }
                    }
                } else {
                    return $.error("没有指定默认国家标识, defaultCountry does not exist!");
                }
            } else {
                for (var i = 0; i <= len; i++) {
                    if (i == 0) {
                        this.initSelectElm(this.data, opt.startLevel, false);
                    } else {
                        if (this.codes && this.codes[i]) {
                            var str = [];
                            for (var j = 0; j < i; j++) {
                                str.push(this.codes[j]);
                            }
                            this.initSelectElm(eval("this.data." + str.join(".subs.") + ".subs"),
                                    i + 1, false);
                        } else {
                            this.initSelectElm(null, i + 1, true);
                        }
                    }
                }
            }
            if (opt.isAutoChange) {
                this.autoChange();
            }
        },
        initSelectElm : function(result, level, disable) { // 创建国家级下拉选择框
            var _id = $(this.element).attr("id"), selectElm = $("<select>").attr("class",
                    "input-small").attr("disabled", disable).attr("data-level", level).on("change",
                    this.onChange);
            if (result) {
                $.data(document, _id + "-level" + level, result);
                var optionElms = new Array();
                // if (this.options.nullLevel && this.options.nullLevel <=
                // level) {
                // optionElms.push($("<option>"));
                // }
                // if (isIE) { // IE浏览器默认值兼容控制
                // optionElms.push($("<option>"));
                // }
                optionElms.push($("<option>"));
                $.map(result, function(val, key) {
                    optionElms.push($("<option>").attr("value", key).text(val.caption));
                });
                selectElm.append(optionElms);
            }
            selectElm.val(disable ? "" : this.codes ? this.codes[level - 1] : "");
            $(this.element).before(selectElm).before(" ");
        },
        /**
         * 下拉框内容发生改变时事件处理
         */
        onChange : function() { // 地区联动事件(手动)
            var _id = $(this).nextAll("input").attr("id"), $el = $.data(document, _id), val = $(
                    this).val(), level = $(this).attr("data-level");
            if ($el != undefined) {
                var data = $.data(document, _id + "-level" + level);
                if (val === "") {
                    $(this).nextAll("select").html("").attr("disabled", true);
                    return;
                }
                var subs = eval("data." + val + ".subs"), showNext = true;
                if ($el.options.subHideByNotGroundSon) {
                    showNext = eval("data." + val + ".includingGrandSub");
                }
                if ($(this).data("level") >= $el.options.endLevel) {
                    showNext = false;
                }
                if (subs != undefined && showNext) {
                    $(this).nextAll("select").html("").attr("disabled", true);
                    $el.createOption(this, subs, $(this).data("level"));
                } else {
                    $(this).nextAll("select").html("").attr("disabled", true);
                }
                var code = "";
                $(this).parent().find("select").each(function() {
                    if ($(this).val()) {
                        code = $(this).val();
                    }
                });
                $("input[name=" + $el.options.regionName + "]").val(code);
                $el.options.changeCallBack(subs != undefined);
            }
        },
        /**
         * 自动触发地区下拉框联动事件
         */
        autoChange : function() {
            var _this = this, _id = $(this.element).attr("id");
            $(this.element)
                    .parent()
                    .find("select")
                    .each(
                            function() {
                                if ($(this).prop("disabled")) {
                                    var optionObj = $(this).prev(), val = optionObj.val(), level = optionObj
                                            .attr("data-level"), data = $.data(document, _id
                                            + "-level" + level);
                                    if (val !== "" && val !== null) {
                                        var subs = eval("data." + val + ".subs");
                                        if (subs != undefined) {
                                            _this.createOption(optionObj, subs, level);
                                        } else {
                                            $(this).nextAll("select").html("").attr("disabled",
                                                    true);
                                        }
                                        $("input[name=" + _this.options.regionName + "]").val(
                                                _this.getValue());
                                    }
                                }
                            });
        },
        /**
         * 获取当前选择地区的最小单元code
         */
        getValue : function() {
            var code = "";
            $(this.element).parent().find("select").each(function() {
                if ($(this).val()) {
                    code = $(this).val();
                }
            });
            return code;
        },
        createOption : function(dom, data, level) {
            var selectElm = $(dom).next().html(""), _id = $(dom).nextAll("input").attr("id"), nextLelve = selectElm
                    .attr("data-level"), opts = new Array();
            if (this.options.nullLevel) {
                if (this.options.nullLevel <= nextLelve) {
                    opts.push($("<option></option>"));
                }
            }
            $.map(data, function(val, key) {
                opts.push($("<option></option>").attr("value", key).text(val.caption));
            });
            selectElm.append(opts);
            $(selectElm).attr("disabled", false);
            $.data(document, _id + "-level" + nextLelve, data);
            if (nextLelve < this.options.endLevel) {
                this.autoChange();
            }
        }
    };

    var methods = {
        init : function(option) {
            var args = arguments, result = null;
            $(this)
                    .each(
                            function(index, item) {
                                var data = $.data(document, $(item).attr("id")), options = (typeof option !== 'object') ? null
                                        : option;
                                if (!data) {
                                    var domOptions = $(item).attr("options");
                                    if (domOptions) { // 如果页面控件设置了options参数，合并参数值
                                        options = $.extend(options, $.parseJSON(domOptions.replace(
                                                /'/g, "\"")));
                                    }
                                    data = new Region(item, options);
                                    $.data(document, $(item).attr("id"), data);
                                    result = $.extend({
                                        "element" : data.element
                                    }, methods);
                                    return false;
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
        change : function(codes) {
            var el = !this.element ? $(this) : this.element;
            var region = $.data(document, $(el).attr("id")), codeArry = region
                    .loadDefaultCodes(codes), selects = $(el).siblings("select");
            console.info(codeArry);
            if (codeArry == null) {
                $(selects[0]).val("");
                $(el).val("");
                for (var i = 1; i < selects.length; i++) {
                    $(selects[i]).val("").attr("disabled", true);
                    $.data(document,
                            $(el).attr("id") + "-level" + $(selects[i]).attr("data-level"), "");
                }
                return;
            }
            for (var i = region.options.startLevel - 1; i < codeArry.length; i++) {
                if (region.options.startLevel == 2) {
                    $(selects[i - 1]).val(codeArry[i]).trigger("change");
                } else {
                    $(selects[i]).val(codeArry[i]).trigger("change");
                }
            }
        }
    };

    $.fn.regionSelector = function(method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            return $.error("Method " + method + " does not exist on plug-in: region-selector");
        }
    };

})(jQuery);
