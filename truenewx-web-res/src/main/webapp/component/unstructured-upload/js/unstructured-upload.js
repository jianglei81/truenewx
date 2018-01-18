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
            // 当组件初始化完成的回调函数
            ready : function() {
            },
            // 当有文件被添加进队列时的回调函数
            fileQueued : function(file) {
            },
            // 当有一批文件被添加进队列时的回调函数
            filesQueued : function(files) {
            },
            // 文件上传过程中的回调函数
            uploadPprogress : function(file, percentage) {
            },
            // 上传成功的回调函数
            uploadSuccess : function(file) {
            },
            // 上传失败的回调函数
            uploadError : function(file) {
            },
            // 文件上传完成后，服务器返回结果回调函数
            uploadAccept : function(block, result) {
            },
            // 文件上传完成后(不管成功与否），服务器返回结果回调函数
            uploadComplete : function(file) {
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
            this.options = $.extend(true, {}, defaultOptions, options);
            if (!this.options.authorizeType) { // 授权类型必须有
                throw "Please set the authorizeType";
            }
            this.fileStats = {
                updateFileId : null, // 待更新id
                acceptFileId : null, // 待接收id
                // deleteFileId : null, // 待删除id
                removeFileId : null, // 移除文件标识
                stopCause : null
            // 阻止原因， 'number'-表示因数量限制阻止，'duplicate'-表示因文件重复阻止
            }
            // 覆盖默认错误消息
            $.each(this.options.messages, function(locale, localeMessages) {
                messages[locale] = $.extend({}, messages[locale], localeMessages);
            });
            delete this.options.messages;

            this.element = element;
            var _this = this;
            // 绑定清空更新id在点击事件上
            element.on("click", function() {
                _this.fileStats.updateFileId = null;
            })
            $.tnx.rpc.imports(this.options.controllerId, function(rpc) {
                _this.rpc = rpc;

                rpc.getUploadLimit(_this.options.authorizeType, function(uploadLimit) {
                    _this.uploadLimit = uploadLimit;
                    _this.buildWebUploader(uploadLimit);
                });
            });
            element.data("unstructuredUpload", this);
        },
        buildWebUploader : function(uploadLimit) {
            var webuploaderOptions = {
                swf : this.options.swf,
                server : this.options.serverPath + "/" + this.options.authorizeType,
                pick : this.element,
                auto : this.options.auto,
                resize : this.options.resize,
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
            this.webuploader = WebUploader.create(webuploaderOptions);
            var _wu = this.webuploader;
            var _fs = this.fileStats;

            $.each(this.options.events, function(name, handler) {
                var eventArray = [ "ready", "beforeFileQueued", "filesQueued", "uploadAccept",
                        "error", "fileDequeued", "uploadComplete" ];
                if (!eventArray.includes(name)) {
                    _wu.on(name, handler);
                }
            });

            var _this = this;

            _wu.on("beforeFileQueued", function(file) {
                _fs.stopCause = null;
                // 文件容量大小校验
                var capacity = uploadLimit.capacity;
                if (capacity > 0 && file.size > capacity) {
                    var error = _this.buildError("error.unstructured.upload.beyond_max_capacity",
                            _this.getCapacityCaption(capacity), file.name, _this
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
                                "error.unstructured.upload.unsupported_extension", extensions
                                        .join("、"), file.name);
                        _this.options.events.error(error);
                        return false;
                    }
                }
                // 文件数限制验证
                var filesLimitNum = uploadLimit.number;
                if (_fs.updateFileId) { // 存在待更新id
                    // 给待加入队列的文件设置hash值
                    file.valiHash = _this.hashString(file.name + file.size + file.lastModifiedDate)
                    // 当文件验证重复时，设置阻止上传标识为2
                    $.each(_wu.getFiles(), function() {
                        if (this.__hash == file.valiHash) {
                            _fs.stopCause = "duplicate";
                        }
                    })
                    // 已有文件数大于限制文件数，阻止上传；
                    if (_wu.getFiles().length > filesLimitNum) {
                        _fs.stopCause = "number";
                    }
                    _fs.removeFileId = _fs.updateFileId;
                    _fs.updateFileId = null
                } else {
                    // 不存在更新文件id，已有文件数等于限制文件数时，阻止上传；
                    if (_wu.getFiles().length >= filesLimitNum) {
                        _fs.stopCause = "number";
                    }
                }
                // 阻止上传标识为空，且移除文件标识存在时，删除待更新文件；
                if (_fs.stopCause == null && _fs.removeFileId) {
                    _wu.removeFile(_fs.removeFileId, true);
                    // 设置给accept事件接受的参数
                    _fs.acceptFileId = _fs.removeFileId
                    _fs.updateFileId = null;
                }
                _fs.removeFileId = null;
                return true;
            });

            _wu.on("filesQueued", function(files) {
                // 存在阻止上传原因，清空队列；
                if (_fs.stopCause) {
                    $.each(files, function() {
                        _wu.removeFile(this.id, true)
                    });
                    // 超过数量限制
                    if (_fs.stopCause == "number") {
                        var error = _this.buildError("error.unstructured.upload.beyond_max_number",
                                uploadLimit.number);
                        _this.options.events.error(error);
                    }
                }
                _fs.stopCause = null;
                return _this.options.events.filesQueued(files);
            });

            _wu.on("uploadAccept", function(block, result) {
                if (result.errors) {
                    var error = result.errors;
                    if (error.length == 1) { // 只有一个错误，则转换为单错误对象
                        error = error[0];
                    }
                    _this.options.events.error(error);
                    return false;
                }
                // 根据是否存在更新id，执行回调函数
                if (_fs.acceptFileId) {
                    var updateId = _fs.acceptFileId;
                    _fs.acceptFileId = null;
                    return _this.options.events.uploadAccept(block, result, updateId);
                } else {
                    return _this.options.events.uploadAccept(block, result);
                }
            });

            _wu.on("uploadComplete", function(file) {
                // 上传结束后判断是否隐藏上传按钮
                if (_wu.getFiles().length >= _wu.options.fileNumLimit) {
                    _this.element.hide();
                }
                return _this.options.events.uploadComplete(file);
            });
            // 加载完执行ready事件
            this.options.events.ready();
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
        },
        hashString : function(str) {
            var hash = 0, i = 0, len = str.length, _char;
            for (; i < len; i++) {
                _char = str.charCodeAt(i);
                hash = _char + (hash << 6) + (hash << 16) - hash;
            }
            return hash;
        },
        addFiles : function(storageUrls, callback) {
            var _this = this;
            this.rpc.getReadMetadatas(storageUrls, function(metadatas) {
                var wuFiles = [];
                $.each(metadatas, function(i, metadata) {
                    if (metadata) {
                        var blobFile = new File([ "files" ], metadata.filename, {
                            type : metadata.mimeType,
                            lastModified : metadata.lastModifiedTime
                        });
                        var runtimeForRuid = new WebUploader.Runtime.Runtime();
                        var wuFile = new WebUploader.File(new WebUploader.Lib.File(WebUploader
                                .guid("rt_"), blobFile));
                        // 由于服务端接收到的文件可能比原始文件更大，此时加载的文件可能超出容量限制，此时通过设置虚假文件大小，避免容量超限
                        if (metadata.size > _this.uploadLimit.capacity) {
                            wuFile.size = _this.uploadLimit.capacity;
                        } else {
                            wuFile.size = metadata.size;
                        }
                        wuFile.lastModified = metadata.lastModifiedTime;
                        wuFile.url = metadata.readUrl;
                        wuFile.__hash = _this.hashString(wuFile.name + wuFile.size
                                + wuFile.lastModified);
                        wuFile.setStatus("complete"); // 回显文件设置为已完成状态
                        wuFiles.push(wuFile);
                    }
                });
                if (wuFiles.length) {
                    var _webuploader = _this.webuploader;
                    _webuploader.addFile(wuFiles);
                    if (callback) {
                        callback(_webuploader.getFiles());
                    }
                }
            });
        },
        updateFile : function(fileId) {
            this.element.find("input[type='file']").trigger("click");
            this.fileStats.updateFileId = fileId;
        },
        deleteFile : function(fieldId) {
            // 如果待删除文件和待更新文件ID一致，则清空待更新文件id
            if (this.fileStats.updateFileId == fieldId) {
                this.fileStats.updateFileId = null;
            }
            this.webuploader.removeFile(fieldId, true);
            // 移除页面DOM元素
            $("#" + fieldId).parents(".webuploader-item").remove();
            // 执行删除后，如果队列里的文件小于限制文件，则显示上传按钮
            var filesLength = this.webuploader.getFiles().length;
            var fileNumLimit = this.webuploader.options.fileNumLimit;
            if (filesLength < fileNumLimit) {
                this.element.show();
            }
        }
    };

    var methods = {
        init : function(options) {
            var element = $(this);
            return new UnstructuredUpload(element, options);
        },
        addFiles : function(storageUrls, callback) {
            var element = $(this);
            var _unstructuredUpload = element.data("unstructuredUpload");
            _unstructuredUpload.addFiles(storageUrls, callback);
        },
        updateFile : function(fileId) {
            var element = $(this);
            var _unstructuredUpload = element.data("unstructuredUpload");
            _unstructuredUpload.updateFile(fileId);
        },
        deleteFile : function(fileId) {
            var element = $(this);
            var _unstructuredUpload = element.data("unstructuredUpload");
            _unstructuredUpload.deleteFile(fileId);
        }
    };

    $.fn.unstructuredUpload = function(method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1))
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments)
        } else {
            return $.error("Method " + method + " does not exist on plug-in: UnstructuredUpload");
        }
    };

})(jQuery);
