/**
 * image-upload.js v1.0.0
 *
 * Depends on: jquery.js, jquery.Jcrop.js,unstructured-upload.js
 *
 * 图片裁剪上传插件
 */
(function($) {

    var ImageUpload = function(element, options) {
        this.init(element, options);
    };

    ImageUpload.prototype = {
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
            this.options = $.extend({}, $.fn.imageUpload.defaults, options);
            this.render();
        },
        reSizeImage : function(maxWidth, maxHeight, imgObj) {
            var hRatio;
            var wRatio;
            var Ratio = 1;
            var w = imgObj.width();
            var h = imgObj.height();
            var oOptions = this.options;
            wRatio = maxWidth / w;
            hRatio = maxHeight / h;
            if (maxWidth == 0 && maxHeight == 0) {
                Ratio = 1;
            } else if (maxWidth == 0) {//
                if (hRatio < 1) {
                    Ratio = hRatio;
                }
            } else if (maxHeight == 0) {
                if (wRatio < 1) {
                    Ratio = wRatio;
                }
            } else if (wRatio < 1 || hRatio < 1) {
                Ratio = (wRatio <= hRatio ? wRatio : hRatio);
            }
            if (Ratio < 1) {
                w = parseInt(w * Ratio);
                h = parseInt(h * Ratio);
            }
            if(w<oOptions.minWidth){
                w=oOptions.minWidth;
            }
            if(h<oOptions.minHeight){
                w=oOptions.minWidth;
            }
            imgObj.height(h);
            imgObj.width(w);
        },
        showModalCrop : function(imageUrl) {
            var oOptions = this.options;
            var $ele = $(this.element);
            if (oOptions.maxUploadFileNum == 1 && oOptions.crop && !oOptions.crop.disabled) { // 支持裁剪
                var $ele = $(this.element);
                var _this = this;
                var $image = $("<img/>");
                $image.unbind("load");
                $image.bind("load", function() {
                    _this.reSizeImage(oOptions.crop.width, oOptions.crop.height, $image);
                    $image.show();
                    var imageWidth = $image.width();
                    var imageHeight = $image.height();
                    var selectWidth = 0, selectHeight = 0;
                    var maxWidth = Math.min(oOptions.maxWidth, imageWidth);// 最大的宽度
                    var maxHeight = Math.min(oOptions.maxWidth, imageWidth);
                    if (oOptions.minWidth == oOptions.maxWidth) {
                        selectWidth = oOptions.minWidth;
                    } else {
                        selectWidth = maxWidth - Math.max(oOptions.minWidth, 0);
                    }
                    if (oOptions.minHeight == oOptions.maxHeight) {
                        selectHeight = oOptions.minHeight;
                    } else {
                        selectHeight = Math.min(oOptions.maxHeight, imageHeight)
                                - Math.max(oOptions.minHeight, 0);
                    }
                    var minWidth = Math.max(oOptions.minWidth, 0);// 最小的选择框宽度
                    minWidth = Math.min(minWidth, imageWidth);// 为了防止最小选择框宽度比图片宽度还大
                    var minHeight = Math.max(oOptions.minHeight, 0);// 最小的选择框高度
                    minHeight = Math.min(minHeight, imageHeight);
                    // $image.imageView();
                    $image.Jcrop({
                        keySupport : false,
                        aspectRatio : oOptions.crop.aspectRatio,
                        allowResize : oOptions.crop.allowResize,
                        allowMove : oOptions.crop.allowMove,
                        allowSelect : oOptions.crop.allowSelect,
                        minSize : [ minWidth, minHeight ],
                        maxSize : [ maxWidth, maxHeight ],
                        boxWidth : $image.width(),
                        boxHeight : $image.height(),
                        sideHandles : true
                    }, function() {
                        _this.jcrop = this;
                        var bounds = this.getBounds();
                        var x0 = (imageWidth - selectWidth) / 2;
                        var y0 = (imageHeight - selectHeight) / 2;
                        var x1 = x0 + selectWidth;
                        var y1 = y0 + selectHeight;
                        this.setSelect([ x0, y0, x1, y1 ]);
                        $(".modal-body .jcrop-holder").css("margin", "auto");
                    });
                });
                var content = "<div id='concent' style='display: flex; width: "
                        + oOptions.crop.width + "px; height: " + oOptions.crop.height
                        + "px;margin:auto'></div>";
                $.tnx
                        .dialog(
                                "图片裁剪",
                                content,
                                [
                                        {
                                            text : "确定",
                                            "class" : 'btn-primary',
                                            click : function() {
                                                if (oOptions.beginCallback
                                                        && typeof oOptions.beginCallback == "function") {
                                                    oOptions.beginCallback($ele);
                                                }
                                                var winThis = this;
                                                var select = _this.jcrop.tellSelect();
                                                var imageWidth = $image.width();
                                                var imageHeight = $image.height();
                                                var x = select.x, y = select.y, width = select.w, height = select.h;
                                                if (_this.unstructuredUpload) {
                                                    var filename = "";
                                                    if (!oOptions.originalFilename
                                                            && oOptions.newFilename != "") {
                                                        filename = oOptions.newFilename;
                                                    }
                                                    _this.unstructuredUpload
                                                            .upload({
                                                                filename : filename,
                                                                resize : {
                                                                    width : imageWidth,
                                                                    height : imageHeight
                                                                },
                                                                crop : {
                                                                    x : x,
                                                                    y : y,
                                                                    width : width,
                                                                    height : height
                                                                },
                                                                successCallback : function(result) {
                                                                    if (oOptions.successCallback
                                                                            && typeof oOptions.successCallback == 'function') {
                                                                        oOptions
                                                                                .successCallback(result);
                                                                    }
                                                                    if (oOptions.endCallback
                                                                            && typeof oOptions.endCallback == 'function') {
                                                                        oOptions.endCallback($ele);
                                                                    }
                                                                    winThis.close();
                                                                },
                                                                errorCallback : function(err) {
                                                                    oOptions.errorCallback(err);
                                                                    oOptions.endCallback($ele);
                                                                }
                                                            });
                                                }
                                            }
                                        }, {
                                            text : "取消",
                                            "class" : "btn-default",
                                            click : function() {
                                                this.close();
                                            }
                                        } ], {
                                    width : oOptions.crop.width + 50,
                                    height : oOptions.crop.height,
                                    events : {
                                        shown : function() {
                                            $("#concent").append($image);
                                            $image.attr("src", imageUrl);
                                            $image.hide();
                                            $.fn.center();
                                        }
                                    }
                                });
            }
        },
        inbuiltCrop : function(imageUrl) {
            var oOptions = this.options;
            var $ele = $(this.element);
            if (oOptions.maxUploadFileNum == 1 && oOptions.crop && !oOptions.crop.disabled) { // 支持裁剪
                var $target = $(oOptions.crop.target);
                if ($target.length) {
                    var $ele = $(this.element);
                    $target.html("");
                    var $image = $("<img/>");
                    var _this = this;
                    var boundx, boundy;
                    var preview = function(c) { // 显示缩略图回调
                        if (parseInt(c.w) > 0) {
                            var $pimg = $(oOptions.crop.previewTarget);
                            if (!$pimg.is("img")) {
                                $pimg = $pimg.find("img");
                            }
                            var rx = $pimg.parent().width() / c.w;
                            var ry = $pimg.parent().height() / c.h;
                            $pimg.css({
                                'width' : Math.round(rx * boundx) + 'px',
                                'height' : Math.round(ry * boundy) + 'px',
                                'marginLeft' : '-' + Math.round(rx * c.x) + 'px',
                                'marginTop' : '-' + Math.round(ry * c.y) + 'px',
                                'max-width' : 'none'
                            });
                        }
                        // oOptions.crop.onChange(crop);
                    }
                    $image
                            .bind(
                                    "load",
                                    function() {
                                        // 同步显示缩略图
                                        var previewTarget = oOptions.crop.previewTarget;
                                        if (previewTarget) {
                                            var $previewTarget = $(previewTarget);
                                            if ($previewTarget.is("img")) {
                                                $previewTarget.attr("src", $image.attr("src"));
                                            } else {
                                                var $previewImage = $("<img/>");
                                                $previewImage.attr("src", $image.attr("src"));
                                                $previewTarget.html($previewImage);
                                            }
                                        }
                                        _this.reSizeImage(oOptions.crop.width,
                                                oOptions.crop.height, $image);
                                        $image.show();
                                        var imageWidth = $image.width();
                                        var imageHeight = $image.height();
                                        var selectWidth = 0, selectHeight = 0;
                                        var maxWidth = Math.min(oOptions.maxWidth, imageWidth);// 最大的宽度
                                        var maxHeight = Math.min(oOptions.maxWidth, imageWidth);
                                        if (oOptions.minWidth == oOptions.maxWidth) {
                                            selectWidth = oOptions.minWidth;
                                        } else {
                                            selectWidth = maxWidth - Math.max(oOptions.minWidth, 0);
                                        }
                                        if (oOptions.minHeight == oOptions.maxHeight) {
                                            selectHeight = oOptions.minHeight;
                                        } else {
                                            selectHeight = Math
                                                    .min(oOptions.maxHeight, imageHeight)
                                                    - Math.max(oOptions.minHeight, 0);
                                        }
                                        var minWidth = Math.max(oOptions.minWidth, 0);// 最小的选择框宽度
                                        minWidth = Math.min(minWidth, imageWidth);// 为了防止最小选择框宽度比图片宽度还大
                                        var minHeight = Math.max(oOptions.minHeight, 0);// 最小的选择框高度
                                        minHeight = Math.min(minHeight, imageHeight);
                                        // $image.imageView();
                                        $image
                                                .Jcrop(
                                                        {
                                                            aspectRatio : oOptions.crop.aspectRatio,
                                                            allowResize : oOptions.crop.allowResize,
                                                            allowMove : oOptions.crop.allowMove,
                                                            allowSelect : oOptions.crop.allowSelect,
                                                            onChange : preview,
                                                            minSize : [ minWidth, minHeight ],
                                                            maxSize : [ maxWidth, maxHeight ]
                                                        },
                                                        function() {
                                                            _this.jcrop = this;
                                                            var bounds = this.getBounds();
                                                            boundx = bounds[0];
                                                            boundy = bounds[1];

                                                            var x0 = (imageWidth - selectWidth) / 2;
                                                            var y0 = (imageHeight - selectHeight) / 2;
                                                            var x1 = x0 + selectWidth;
                                                            var y1 = y0 + selectHeight;
                                                            this.setSelect([ x0, y0, x1, y1 ]);

                                                            if (oOptions.crop.button) {
                                                                var $btn = $(oOptions.crop.button);
                                                                $btn
                                                                        .click(function() {
                                                                            if (oOptions.callbackBegin) {
                                                                                oOptions
                                                                                        .callbackBegin($ele);
                                                                            }
                                                                            var select = _this.jcrop
                                                                                    .tellSelect();
                                                                            var x = select.x, y = select.y, width = select.w, height = select.h;
                                                                            if (_this.unstructuredUpload) {
                                                                                var filename = "";
                                                                                if (!oOptions.originalFilename
                                                                                        && oOptions.newFilename != "") {
                                                                                    filename = oOptions.newFilename;
                                                                                }
                                                                                _this.unstructuredUpload
                                                                                        .upload({
                                                                                            filename : filename,
                                                                                            resize : {
                                                                                                width : imageWidth,
                                                                                                height : imageHeight
                                                                                            },
                                                                                            crop : {
                                                                                                x : x,
                                                                                                y : y,
                                                                                                width : width,
                                                                                                height : height
                                                                                            },
                                                                                            successCallback : function(
                                                                                                    result) {
                                                                                                oOptions
                                                                                                        .successCallback(result);
                                                                                                if (oOptions.endCallback
                                                                                                        && typeof oOptions.endCallback == "function") {
                                                                                                    oOptions
                                                                                                            .endCallback($ele);
                                                                                                }
                                                                                            },
                                                                                            errorCallback : function(
                                                                                                    err) {
                                                                                                oOptions
                                                                                                        .errorCallback(err);
                                                                                                oOptions
                                                                                                        .endCallback($ele);
                                                                                            }
                                                                                        });
                                                                            }
                                                                        });
                                                                // $target.append($btn);
                                                            }
                                                        });
                                    });
                    $image.attr("src", imageUrl);
                    $image.hide(); // 先将图片隐藏，待加载完成调整大小后再显示出来
                    $target.prepend($image);
                    return true;
                }
            }
            return false;
        },
        /**
         * 渲染控件
         */
        render : function() {
            var $ele = $(this.element), oOptions = this.options, minWidth = oOptions.minWidth, maxWidth = oOptions.maxWidth, minHeight = oOptions.minHeight, maxHeight = oOptions.maxHeight, _this = this;
            var userAgent = navigator.userAgent;
            var isIe7 = userAgent.indexOf("MSIE 7.0") > 0;
            var isIe6 = userAgent.indexOf("MSIE 6.0") > 0;
            var isIe8 = userAgent.indexOf("MSIE 8.0") > 0;
            this.unstructuredUpload = $ele.unstructuredUpload({
                authorizeType : oOptions.authorizeType
            }); // 渲染非结构化上传插件
            $ele.prop("accept", "image/*")
            $ele
                    .bind(
                            'input propertychange',
                            function(e) {
                                var files = this.files;
                                var imageUrl;
                                var reg = /\.jpg$|\.jpeg$|\.gif$|\.png$/i;
                                if (isIe6 || isIe7) {
                                    imageUrl = $ele[0].value;
                                } else {
                                    imageUrl = window.URL.createObjectURL(files[0]);
                                }
                                if (files) {
                                    // 预留为批量上传准备
                                    if (files.length > oOptions.maxUploadFileNum) {
                                        oOptions.alert("最多只允许上传 " + oOptions.maxUploadFileNum
                                                + " 张图片");
                                        return;
                                    }
                                    if (files.length == 1 && oOptions.crop.disabled == false) { // 只有上传单张图片才有可能进行图片裁剪
                                        if (!reg.test(files[0].name)) {
                                            $.tnx.alert("选择的文件不是图片，请重新选择", "提示");
                                            return;
                                        }
                                        if (!oOptions.crop.showModal) {
                                            _this.inbuiltCrop.call(_this, imageUrl);
                                        } else {
                                            _this.showModalCrop.call(_this, imageUrl);
                                        }
                                    } else {
                                        if (oOptions.beginCallback
                                                && typeof oOptions.beginCallback == "function") {
                                            oOptions.beginCallback($ele);
                                        }
                                        var endNumber = 0;
                                        var fail = false;
                                        var results = [];
                                        for (var i = 0; i < files.length; i++) {
                                            if (fail) {
                                                return;
                                            }
                                            var file = files[i];
                                            if (!reg.test(file.name)) {
                                                $.tnx.alert("选择的文件不是图片，请重新选择", "提示");
                                                if (oOptions.endCallback
                                                        && typeof oOptions.endCallback == "function") {
                                                    oOptions.endCallback($ele);
                                                }
                                                return;
                                            }
                                            if (file && oOptions.maxCapacity
                                                    && file.size > oOptions.maxCapacity) {
                                                var maxCapacityStr = oOptions.maxCapacity + "B";
                                                if (oOptions.maxCapacity >= 1024) {
                                                    // KB
                                                    maxCapacityStr = (oOptions.maxCapacity / 1024)
                                                            + "K";
                                                } else if (oOptions.maxCapacity >= (1024 * 1024)) {
                                                    // MB
                                                    maxCapacityStr = (oOptions.maxCapacity / (1024 * 1024))
                                                            + "M";
                                                }
                                                oOptions.alert("最大只能上传 " + maxCapacityStr
                                                        + " 大小的图片");
                                                $ele.val("");
                                                return;
                                            }
                                            if (isIe6 || isIe7) {
                                                imageUrl = $ele[i].value;
                                            } else {
                                                imageUrl = window.URL.createObjectURL(files[i]);
                                            }
                                            var image = new Image();
                                            image.src = imageUrl;
                                            var width = image.width;
                                            var height = image.height;
                                            var message = "";
                                            image.onload = function() {
                                                if (fail) {
                                                    return;
                                                }
                                                if ((minWidth && minWidth > 0 && image.width < minWidth)
                                                        || (maxWidth && maxWidth > 0 && image.width > maxWidth)
                                                        || (minHeight && minHeight > 0 && image.height < minHeight)
                                                        || (maxHeight && maxHeight > 0 && image.height > maxHeight)) {
                                                    fail = false;
                                                    if (minWidth == maxWidth
                                                            && minHeight == maxHeight) {
                                                        message = "只能上传" + minWidth + "*"
                                                                + minHeight + "大小的图片";
                                                    } else {
                                                        message = "只能上传" + minWidth + "*"
                                                                + minHeight + "至" + maxWidth + "*"
                                                                + maxHeight + "大小的图片";
                                                    }
                                                }
                                                if (message != "" && !fail) {
                                                    fail = true;
                                                    oOptions.endCallback($ele);
                                                    oOptions.alert(message);
                                                    $ele.val("");
                                                    return;
                                                }
                                                var filename = oOptions.originalFilename ? file.name
                                                        : null;
                                                _this.unstructuredUpload
                                                        .upload({
                                                            filename : filename,
                                                            successCallback : function(result) {
                                                                endNumber++;
                                                                results.push(result);
                                                                if (endNumber == files.length) {
                                                                    oOptions
                                                                            .successCallback(results);
                                                                    if (oOptions.endCallback
                                                                            && typeof oOptions.endCallback == "function") {
                                                                        oOptions.endCallback($ele);
                                                                    }
                                                                }
                                                            },
                                                            errorCallback : function(error) {
                                                                oOptions.errorCallback(error);
                                                                endNumber++;
                                                                if (endNumber == files.length) {
                                                                    if (oOptions.endCallback
                                                                            && typeof oOptions.endCallback == "function") {
                                                                        oOptions.endCallback($ele);
                                                                    }
                                                                }
                                                            }
                                                        });
                                            }

                                        }
                                    }
                                }
                            });
        }
    };

    var methods = {};

    $.fn.imageUpload = function(method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return new ImageUpload(this, method);
        } else {
            return $.error("Method " + method + " does not exist on plug-in: imageUpload");
        }
    };

    $.fn.imageUpload.defaults = {
        authorizeType : null,
        maxWidth : 0, // 限定最大宽度
        maxHeight : 0, // 限定最大高度
        minWidth : 0, // 限定最小宽度
        minHeight : 0, // 限定最小高度
        maxCapacity : 3145728, // 限定单张图片最大容量
        maxUploadFileNum : 1, // 限定最多可以上传图片数
        originalFilename : false, // 是否使用原文件名
        newFilename : "", // 新的文件名
        crop : { // 裁剪设置
            disabled : false, // 默认不禁用裁剪
            showModal : false, // 是否使用模态窗口的方式裁剪
            target : undefined, // 裁剪区定位selector
            aspectRatio : 0,
            width : 220, // 裁剪区域的宽度
            height : 220, // 裁剪区域的高度
            allowResize : true, // 允许选框缩放
            allowMove : true, // 允许选框移动
            allowSelect : false, // 允许新选框
            previewTarget : {
                // 预览图区域
                target : undefined
            },
            button : undefined
        },
        alert : function(data) {
            $.tnx.alert(data);
        },
        successCallback : function(result) {
        }, // 成功回调
        errorCallback : function(error) {
        },// 失败回调
        callbackBegin : function(ele) { // 开始上传的CallBack
        },
        callbackEnd : function(ele) { // 结束上传的CallBack
        },
    };
})(jQuery);