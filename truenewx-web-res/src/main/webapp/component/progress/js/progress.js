/**
 * progress.js v1.0.0
 *
 * Depends on: jquery.js, raphael-min.js
 *
 * 图床显示插件
 */

(function($) {
    var progress = function(element, options) {
        this.init(element, options);
    };

    progress.prototype = {
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
            this.options = $.extend({}, $.fn.progress.defaults, options);
            this.render();
        },

        /**
         * 渲染控件
         */
        render : function() {
            var paper = null,
                $ele = $(this.element),
                oOptions = this.options
                insideRadius = oOptions.insideRadius,
                outsideRadius = oOptions.outsideRadius,
                image = oOptions.image,
                imageSize = oOptions.imageSize,
                percent = oOptions.percent,
                color = oOptions.color,
                pertxtClass = oOptions.pertxtClass;
            $ele.html("<div class='bg' id='bg'></div><div class='" + pertxtClass + "'></div>");
            // 初始化Raphael画布
            paper = Raphael($ele.find(".bg")[0], imageSize, imageSize);
            // 把底图先画上去
            paper.image(image, 0, 0, imageSize, imageSize);
            // 算法不支持画100%，要按99.99%来画
            var drawPercent = percent >= 1 ? 0.9999 : percent; 
            
            //开始计算各点的位置
            //r1是内圆半径，r2是外圆半径
            var r1 = insideRadius, r2 = outsideRadius, PI = Math.PI,
                p1 = { 
                    x : imageSize / 2,  
                    y : 0 
                }, 
                p4 = { 
                    x : p1.x, 
                    y : r2 - r1 
                }, 
                p2 = {  
                    x : p1.x - r2 * Math.sin(2 * PI * (1 - drawPercent)), 
                    y : p1.y + r2 - r2 * Math.cos(2 * PI * (1 - drawPercent)) 
                }, 
                p3 = {
                    x : p4.x - r1 * Math.sin(2 * PI * (1 - drawPercent)), 
                    y : p4.y + r1 - r1 * Math.cos(2 * PI * (1 - drawPercent)) 
                },
                path = [
                    'M', p1.x, ' ', p1.y, 
                    'A', r2, ' ', r2, ' 0 ', percent > 0.5 ? 1 : 0, ' 1 ', p2.x, ' ', p2.y, 
                    'L', p3.x, ' ', p3.y, 
                    'A', r1, ' ', r1, ' 0 ', percent > 0.5 ? 1 : 0, ' 0 ', p4.x, ' ', p4.y, 
                    'Z' 
                ].join('');
            
            //用path方法画图形，由两段圆弧和两条直线组成，画弧线的算法见后
            paper.path(path).attr({"stroke-width" : 0.5, "stroke" : color, "fill" : "90-" + color}); //填充渐变色，从#3f0b3f到#ff66ff
            //显示进度文字 
            $ele.find("." + pertxtClass).text(Math.round(percent * 100) + "%"); 
        }
    };

    var methods = {
            
    };
    $.fn.progress = function(method) {
        if (method && methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (!method || typeof method === 'object') {
            return new progress(this, method);
        } else {
            return $.error("Method " + method + " does not exist on plug-in: progress");
        }
    };

    $.fn.progress.defaults = {
        insideRadius : 32,
        outsideRadius : 40,
        image : "/res/assets/image/progressBg.png",
        imageSize : 80,
        percent : 0,
        color : "#33ff00",
        pertxtClass : "pertxt"
    };

})(jQuery);
