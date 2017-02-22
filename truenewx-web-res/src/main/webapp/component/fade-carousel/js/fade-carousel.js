/**
 * fade-carousel.js v1.0.0
 *
 * Depends on: jquery.js
 *
 * 淡入淡出轮播
 */

(function($) {

    var FadeCarousel = function(element, options) {
        this.init(element, options);
    };

    FadeCarousel.prototype = {
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
            this.options = $.extend({}, $.fn.fadeCarousel.defaults, options);
            this.render();
        },

        /**
         * 渲染控件
         */
        render : function() {
            var _this = this,
                $ele = $(_this.element),
                oOptions = _this.options;
            $ele.addClass("banner has-dots");
            $ele.append("<ol class='dots'></ol>");
            var index = 0;
            $ele.find("div").each(function () {
                $ele.find("ol.dots").append("<li class='dot'>" + index +"</li>");
                index++;
            });
            $ele.find("div").css("opacity", 0);
            $ele.find("div").css("z-index", 1);
            $ele.find("div").eq(0).css("opacity", 1);
            $ele.find("div").eq(0).css("z-index", 2);
            var index = 0;
            $ele.find("div").each(function () {
                var $div = $(this);
                $div.attr("index", index);
                index++;
            });
            $ele.find("ol.dots").find("li").eq(0).addClass("active");
            var interval = setInterval(function () {
                _this.autoshow();
            }, oOptions.delay);
            $ele.mouseover(function() {
                clearInterval(interval);
            });
            $ele.mouseout(function() {
                interval = setInterval(function () {
                    _this.autoshow();
                }, oOptions.delay);
            });
            
            $ele.find("li").click(function(){
                var index = parseInt($(this).text());
                _this.change(index);
            });
            $ele.data("fadeCarousel", this);
        },
        change : function (index) {
            var _this = this,
                $ele = $(_this.element),
                oOptions = _this.options,
                current = $ele.find("div[index='" + index + "']");
            // 隐藏其他图像
            if (current.parent().is('a')) {
                current.parent().siblings("a").find("div").css("z-index", 1);
                current.parent().siblings("a").find("div").animate({
                    opacity : 0
                }, oOptions.speed);
            } else {
                current.siblings("div").css("z-index", 1);
                current.siblings("div").animate({
                    opacity : 0
                }, oOptions.speed);
            }
            
            //显示当前图像
            current.css("z-index", 2);
            current.animate({
                opacity:1
            }, oOptions.speed);
            $ele.find("ol.dots").find("li").removeClass("active");
            $ele.find("ol.dots").find("li").each(function () {
                var $li = $(this);
                if ($li.text() == index) {
                    $li.addClass("active");
                }
            });
        },
        autoshow : function() {
            var _this = this,
                $ele = $(_this.element),
                size = $ele.find("div").length,
                index = parseInt($ele.find("ol.dots").find(".active").text()) + 1;
            if(index <= size - 1){
                _this.change(index);
            }else{
                index = 0;
                _this.change(index);
            }
        },
        reload : function (_this) {
            var $ele = $(_this.element),
                $dots = $ele.find("ol.dots");
            $dots.html("");
            $ele.find("div[index]").each(function () {
                var $div = $(this);
                $dots.append("<li class='dot'>" + $div.attr("index") +"</li>");
            });
            $dots.find("li").click(function(){   
                var index = parseInt($(this).text());
                _this.change(index);
            });
        }
    };

    var methods = {
        reload : function () {
            $(this).data("fadeCarousel").reload($(this).data("fadeCarousel"));
        }
    };

    $.fn.fadeCarousel = function(method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return new FadeCarousel(this, method);
        } else {
            return $.error("Method " + method + " does not exist on plug-in: FadeCarousel");
        }
    };

    $.fn.fadeCarousel.defaults = {
        delay : 4500,
        speed : 1500
    };
})(jQuery);