/**
 * 非结构化存储上传组件
 *
 * Depends on: jquery.js, webuploader.js, truenewx.js
 */
(function($) {

    var UnstructuredUpload = function(element, options) {
        this.init(element, options);
    };

    var defaultOptions = {
        // 【必填】授权类型
        authorizeType : undefined,
        // 【建议填写】回调函数的上下文，即this
        callbackContext : undefined,
        // 【一般需要重填】服务端路径
        serverPath : "/unstructured/upload",
        // RPC控制器beanId
        controllerId : "unstructuredController",
        // flash文件路径，允许flash实现时需提供
        swf : undefined,
        // 是否压缩图片后再上传，对图片才有效
        resize : false,
        // 是否自动提交上传
        auto : false,
        events : {
            // 当有文件被添加进队列时的回调函数
            fileQueued : function(file) {
            },
            // 文件上传过程中的回调函数
            uploadPprogress : function(file, percentage) {
            },
            // 上传成功的回调函数
            uploadSuccess : function(file) {
            },
            // 上传失败的回调函数
            uploadError : function(file, error) {
            },
            // 文件上传完成后，服务器返回结果回调函数
            uploadAccept : function(object, result) {
            },
            // 通用错误处理函数
            error : function(error) {
                $.tnx.alert(getLocaleMessage("error"), error.message);
            }
        },
        messages : { // 扩展定义错误消息
            "default" : {},
            "zh_TW" : {}
        }
    };

    var messages = {
        "default" : {
            "error" : "错误",
            "error.unstructured.upload.beyond_max_number" : "上传文件数不能超过 {0} 个",
            "error.unstructured.upload.beyond_max_capacity" : "文件最大不能超过 {0}，“{1}”的大小为 {2}",
            "error.unstructured.upload.unsupported_extension" : "仅支持 {0} 文件，不能上传“{1}”"
        },
        "zh_TW" : {
            "error" : "錯誤",
            "error.unstructured.upload.beyond_max_number" : "上載文件數不能超過 {0} 個",
            "error.unstructured.upload.beyond_max_capacity" : "文件最大不能超過 {0}，“{1}”的大小為 {2}",
            "error.unstructured.upload.unsupported_extension" : "僅支持 {0} 文件，不能上載“{1}”"
        }
    }

    var getLocaleMessage = function(code, args) {
        var locale = $.tnx.locale;
        var localeMessages = messages[locale] || messages["default"];
        var message = localeMessages[code];
        if (message) {
            args.each(function(arg, index) {
                var regexp = new RegExp("\\{" + index + "\\}", "g");
                message = message.replace(regexp, args[index]);
            });
        }
        return message;
    };

    UnstructuredUpload.prototype = {
        init : function(element, options) {
            this.options = $.extend(defaultOptions, options);
            if (!this.options.authorizeType) { // 授权类型必须有
                throw "Please set the authorizeType";
            }
            // 覆盖默认错误消息
            $.each(this.options.messages, function(locale, localeMessages) {
                messages[locale] = $.extend(messages[locale], localeMessages);
            });
            delete this.options.messages;

            this.element = element;
            var _this = this;
            $.tnx.rpc.imports(this.options.controllerId, function(rpc) {
                _this.rpc = rpc;

                rpc.getUploadLimit(_this.options.authorizeType, function(uploadLimit) {
                    var webuploaderOptions = {
                        swf : _this.options.swf,
                        server : _this.options.serverPath + "/" + _this.options.authorizeType,
                        pick : element,
                        resize : _this.options.resize,
                        threads : 5,
                        prepareNextFile : true,
                        fileNumLimit : uploadLimit.number,
                        fileSingleSizeLimit : uploadLimit.capacity,
                        duplicate : false,
                        accept : {
                            extensions : uploadLimit.extensions.join(","),
                            mimeTypes : uploadLimit.mimeTypes.join(",")
                        }
                    };
                    _this.webuploader = WebUploader.create(webuploaderOptions);

                    _this.webuploader.on("beforeFileQueued", function(file) {
                        // 文件容量大小校验
                        var capacity = uploadLimit.capacity;
                        if (capacity > 0 && file.size > capacity) {
                            var error = _this.buildError(
                                    "error.unstructured.upload.beyond_max_capacity", _this
                                            .getCapacityCaption(capacity), file.name, _this
                                            .getCapacityCaption(file.size));
                            _this.options.events.error(error);
                            return false;
                        }
                        // 扩展名校验
                        var extensions = uploadLimit.extensions;
                        if (extensions.length) {
                            var extension = _this.getFileExtension(file.name);
                            if (extensions.indexOf(extension) < 0) {
                                var error = _this.buildError(
                                        "error.unstructured.upload.unsupported_extension",
                                        extensions.join("、"), file.name);
                                _this.options.events.error(error);
                                return false;
                            }
                        }
                        return true;
                    });

                    _this.webuploader.on("error", function(type) {
                        switch (type) {
                        case "Q_EXCEED_NUM_LIMIT":
                            var error = _this.buildError(
                                    "error.unstructured.upload.beyond_max_number",
                                    uploadLimit.number);
                            _this.options.events.error(error);
                            break;
                        }
                    });

                    $.each(_this.options.events, function(name, handler) {
                        if (name != "error") {
                            _this.webuploader.on(name, handler);
                        }
                    });
                });
            });
        },
        getCapacityCaption : function(capacity) {
            if (capacity === 0) {
                return '0 B';
            }
            var units = [ 'B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB' ];
            var step = 1024;
            var level = Math.floor(Math.log(capacity) / Math.log(step));
            return (capacity / Math.pow(step, level)).toPrecision(3) + ' ' + units[level];
        },
        getFileExtension : function(filename, withDot) {
            var index = filename.lastIndexOf(".");
            if (!withDot) {
                index++;
            }
            return filename.substr(index);
        },
        buildError : function(code) {
            var args = Array.prototype.slice.call(arguments, 1);
            return {
                code : code,
                message : getLocaleMessage(code, args)
            };
        }
    };

    var methods = {
        init : function(options) {
            var element = $(this);
            return new UnstructuredUpload(element, options);
        }
    };

    $.fn.unstructuredUpload = function() {
        return methods.init.apply(this, arguments);
    };

})(jQuery);
