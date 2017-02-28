/**
 * region-selector.js v1.0.0
 */

(function($) {

    var isIE = navigator.userAgent.indexOf('MSIE') != -1;

    var RegionSelector = function(element, options) {
        this.init(element, options);
    };

    RegionSelector.prototype = {
        element : undefined,
        options : { // 默认参数
            parentCode : "CN",// 上级行政区划的编码，可选项为该顶级行政区划的下级
            maxLevel : 3,// 最大级别
            showParent : function(code, caption) { // 显示上级行政区划的方法，返回要显示的内容，返回undefined则不显示
                return undefined;
            },
            onChange : function(code) { // 选项被变更后调用，传入参数为变更后的当前选项
            },
            selectClass : "",
            selectStyle : "",
            data : undefined,
            emptyText : null,
            // 初始化数据
            limits : []
        },
        init : function(element, options) {
            this.rpc = $.tnx.rpc.imports("regionController");
            this.element = $(element);
            this.setOptions(options);
        },
        setOptions : function(options) {
            this.options = $.extend(this.options, options);
            this.initData();
        },
        initData : function() { // 行政区划数据初始化
            if (!this.options.data) { // 没有初始化行政区划数据时，从服务器取数据
                if (this.options.limits.length > 0) {
                    this.options.data = this.rpc.getLimits(this.options.limits);
                } else {
                    this.parentRegion = this.rpc.getRegion(this.options.parentCode);
                    this.options.data = this.parentRegion.subs;
                    this.parentRegion.subs = undefined;
                }
            }
            if (this.options.data) {
                this.data = this.options.data;
            } else {
                return $.error("无法获取行政区划数据");
            }
            this.code = this.element.val();
            if (this.code) {
                this.codes = this.rpc.getParentCodes(this.code).add(this.code);
            }
            if (this.options.maxLevel > 3) {
                this.options.maxLevel = 3;
            }
            this.render();
        },
        render : function() { // 渲染页面效果
            if (this.options.data) {
                var ele = this.element, data = this.options.data, maxLevel = this.options.maxLevel, selectClass = this.options.selectClass, selectStyle = this.options.selectStyle, emptyText = this.options.emptyText;
                var startLevel = 0;
                var selectes = new Array();
                // 解析数据,同时创建起始select
                var _this = this;
                var initCode = "";
                var renderId = ele.attr("id");
                var $select = $("<select render-id='" + renderId + "' class='" + selectClass
                        + "' style='" + selectStyle + "'></select>");
                if (emptyText) {
                    $select.append("<option value=''>" + emptyText + "</option>");
                }
                for (var i = 0; i < data.length; i++) {
                    var region = data[i];
                    if (i == 0) {
                        initCode = region.code;
                    }
                    startLevel = region.level;
                    $select.attr("region-level", startLevel);
                    var $option = $("<option value='" + region.code + "'>" + region.caption
                            + "</option>");
                    $select.append($option);
                    _this.initOptions(region);
                }
                $select.on("change", function() {
                    _this.change($(this).attr("region-level") - 0);
                });
                $(ele).before($select);
                // 继续生成后续的select且初始化数据

                for (var i = (startLevel + 1); i <= maxLevel; i++) {
                    var parentCode = $("select[render-id='" + renderId + "'][region-level='" + (i - 1) + "']").val();
                    var options = $.data(document, parentCode);
                    var $subSelect = $("<select render-id='" + renderId + "' class='" + selectClass
                            + "' style='" + selectStyle + "' region-level='" + i + "'></select>");
                    if (emptyText) {
                        $subSelect.append("<option value=''>" + emptyText + "</option>");
                    }
                    if (options && options.length > 0) {
                        for (var j = 0; j < options.length; j++) {
                            $subSelect.append(options[j]);
                        }
                    }
                    $subSelect.on("change", function() {
                        _this.change($(this).attr("region-level") - 0);
                    });
                    ele.before($subSelect);
                }
                $(ele).val($("select[render-id='" + renderId + "'][region-level='" + maxLevel + "']").val());
            }
        },
        initOptions : function(region) {// 将数据生成option并缓存到document中
            var _this = this;
            if (region.subs != null) {
                var options = new Array();
                $.each(region.subs, function(index, subRegion) {
                    var option = "<option value='" + subRegion.code + "'>" + subRegion.caption
                            + "</option>";
                    options.push(option);
                    if (subRegion.subs != null) {
                        _this.initOptions(subRegion);
                    }
                });
                $.data(document, region.code, options);
            }
        },
        change : function(level) {// 下拉连级变动
            var maxLevel = this.options.maxLevel;
            var renderId = this.element.attr("id");
            for (var i = level; i < maxLevel; i++) {
                var subLevel = i + 1;
                var $subSelect = $("select[render-id='" + renderId + "'][region-level='" + subLevel
                        + "']");
                $subSelect.find("option[value!='']").remove();
                var parentCode = $("select[render-id='" + renderId + "'][region-level='" + i + "']")
                        .val();
                if (parentCode != "" && parentCode != null) {
                    var options = $.data(document, parentCode);
                    $subSelect.append(options.join(''));
                }
            }
            var code = $("select[render-id='" + renderId + "'][region-level='" + maxLevel + "']")
                    .val();
            this.element.val(code);
            this.options.onChange(code);
        }
    };

    var methods = {
        init : function(option) {
            var args = arguments, result = null;
            $(this).each(function(index, item) {
                var options = (typeof option !== 'object') ? null : option;
                var domOptions = $(item).attr("options");
                if (domOptions) { // 如果页面控件设置了options参数，合并参数值
                    options = $.extend(options, $.parseJSON(domOptions.replace(/'/g, "\"")));
                }
                var data = new RegionSelector(item, options);
                result = $.extend({
                    "element" : data.element
                }, methods);
                return false;

                if (typeof option === 'string') {
                    if (data[option]) {
                        result = data[option].apply(data, Array.prototype.slice.call(args, 1));
                    } else {
                        throw "Method " + option + " does not exist";
                    }
                } else {
                    result = data.setOptions(option);
                }
            });
            return result;
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