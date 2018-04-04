/**
 * 非结构化存储上传组件
 *
 * Depends on: jquery.js, webuploader-0.1.7.js, truenewx.js
 */
(function($) {

    var UnstructuredUpload = function(element, options) {
        this.init(element, options);
    };

    var defaultOptions = {
        // 【必填】授权类型
        authorizeType : undefined,
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
        messages : { // 扩展定义错误消息
        },
        // 获取初始数据的函数，也可以直接赋值为一个数组
        data : function() {
            return undefined;
        },
        events : {
            // 当有文件被添加进队列时的回调函数
            fileQueued : function(file) {
            },
            // 当有一批文件被添加进队列时的回调函数
            filesQueued : function(files) {
            },
            // 文件上传过程中的回调函数
            uploadProgress : function(file, percentage) {
            },
            // 上传成功的回调函数
            uploadSuccess : function(file) {
            },
            // 上传失败的回调函数
            uploadError : function(file, reason) {
            },
            // 文件上传完成后，服务器返回结果回调函数
            uploadAccept : function(file, uploadResult) {
            },
            // 文件上传完成后(不管成功与否），服务器返回结果回调函数
            uploadComplete : function(file) {
            },
            // 通用错误处理函数
            error : function(error) {
                $.tnx.alert(message, $.tnx.message($.tnx, "error.title"));
            }
        }
    };

    UnstructuredUpload.prototype = {
        messages : {
            "error.unstructured.upload.beyond_max_number" : "上传文件数不能超过 {0} 个",
            "error.unstructured.upload.beyond_max_capacity" : "文件最大不能超过 {0}，“{1}”的大小为 {2}",
            "error.unstructured.upload.only_supported_extension" : "仅支持 {0} 文件，不能上传“{1}”",
            "error.unstructured.upload.unsupported_extension" : "不支持 {0} 文件，不能上传“{1}”",
            "error.unstructured.upload.duplicated_file" : "文件 {0} 重复，将被忽略"
        },
        init : function(element, options) {
            this.options = $.extend(true, {}, defaultOptions, options);
            if (!this.options.authorizeType) { // 授权类型必须有
                throw "Please set the authorizeType";
            }
            // 覆盖默认错误消息
            this.messages = $.extend(this.messages, this.options.messages);
            delete this.options.messages;

            this.element = element;
            $.tnx.initMessages(this, this.options.locale, undefined,
                    "/component/unstructured-upload/js/unstructured-upload");
            var _this = this;
            $.tnx.rpc.imports(this.options.controllerId, function(rpc) {
                _this.rpc = rpc;

                rpc.getUploadLimit(_this.options.authorizeType, function(uploadLimit) {
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
                duplicate : false
            };
            // 接受扩展名的模式，则添加扩展名限定
            if (!uploadLimit.rejectedExtension) {
                webuploaderOptions.accept = {
                    extensions : uploadLimit.extensions.join(","),
                    mimeTypes : uploadLimit.mimeTypes.join(",")
                }
            }
            this.webuploader = WebUploader.create(webuploaderOptions);
            var _wu = this.webuploader;

            $.each(this.options.events, function(name, handler) {
                var specialEvents = [ "ready", "beforeFileQueued", "fileQueued", "filesQueued",
                        "fileDequeued", "uploadAccept", "error" ];
                if (!specialEvents.includes(name)) {
                    _wu.on(name, handler);
                }
            });

            var _this = this;

            _wu.on("beforeFileQueued", function(file) {
                // 如果存在待更新文件，且当前加入文件不是重新加入队列的待更新文件，则先从队列中移除待更新文件
                if (_this.updatingFile && _this.updatingFile.id != file.id) {
                    _wu.removeFile(_this.updatingFile.id);
                }
                // 扩展名拒绝模式，需要校验扩展名
                if (uploadLimit.rejectedExtension) {
                    var extensions = uploadLimit.extensions;
                    if (extensions.length) {
                        var extension = _this.getFileExtension(file.name);
                        if (extensions.indexOf(extension) >= 0) {
                            var error = _this.buildError(
                                    "error.unstructured.upload.unsupported_extension", extensions
                                            .join("、"), file.name);
                            _this.options.events.error(error);
                            return false;
                        }
                    }
                }
                return true;
            });

            _wu.on("fileQueued", function(file) {
                // 忽略待更新文件重新加入队列的事件
                if (_this.updatingFile && file.id == _this.updatingFile.id) {
                    return;
                }

                if (_this.options.events.fileQueued) {
                    _this.options.events.fileQueued(file);
                }
            });

            _wu.on("filesQueued",
                    function(files) {
                        // 忽略待更新文件重新加入队列的事件
                        if (files.length == 1 && _this.updatingFile
                                && files[0].id == _this.updatingFile.id) {
                            return;
                        }
                        // 文件数量达到上限，则需要隐藏上传按钮
                        if (_this.getFileNum() >= _wu.options.fileNumLimit) {
                            _this.element.hide();
                        }

                        if (files.length && _this.options.events.filesQueued) {
                            _this.options.events.filesQueued(files,
                                    _this.updatingFile ? _this.updatingFile.id : undefined);
                        }
                    });

            _wu.on("fileDequeued", function(file) {
                // 忽略待更新文件被移出队列的事件
                if (_this.updatingFile && file.id == _this.updatingFile.id) {
                    return;
                }
                // 文件数量低于限制，则需要显示上传按钮
                if (_this.getFileNum(file) < _wu.options.fileNumLimit) {
                    _this.element.show();
                }
                if (_this.options.events.fileDequeued) {
                    _this.options.events.fileDequeued(file);
                }
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
                // webuploader每次只上传一个文件，故此处的result必然为单个结果
                var uploadResult = result[0];
                if (_this.updatingFile) {
                    // 清除待更新文件
                    var fileId = _this.updatingFile.id;
                    _this.updatingFile = null;
                    return _this.options.events.uploadAccept(block.file, uploadResult, fileId);
                } else {
                    return _this.options.events.uploadAccept(block.file, uploadResult);
                }
            });

            _wu.on("error", function(type) {
                // 如果有待更新文件，则将该文件重新加入队列，并清除待更新文件记录
                if (_this.updatingFile) {
                    _wu.addFile(_this.updatingFile);
                    _this.updatingFile = null;
                }
                var error;
                switch (type) {
                case "Q_EXCEED_NUM_LIMIT":
                    error = _this.buildError("error.unstructured.upload.beyond_max_number",
                            uploadLimit.number);
                    break;
                case "F_EXCEED_SIZE":
                    var file = arguments[2];
                    error = _this.buildError("error.unstructured.upload.beyond_max_capacity", _this
                            .getCapacityCaption(uploadLimit.capacity), file.name, _this
                            .getCapacityCaption(file.size));
                    break;
                case "Q_TYPE_DENIED":
                    var file = arguments[1];
                    error = _this.buildError("error.unstructured.upload.only_supported_extension",
                            uploadLimit.extensions.join("、"), file.name);
                    break;
                case "F_DUPLICATE":
                    var file = arguments[1];
                    error = _this
                            .buildError("error.unstructured.upload.duplicated_file", file.name);
                    break;
                }
                if (error) {
                    _this.options.events.error(error);
                }
            });

            // 加载初始数据
            var data;
            if (typeof (this.options.data) == "function") {
                data = this.options.data();
            } else {
                data = this.options.data;
            }
            if (data && data.length) {
                this.addFile(data);
            }
        },
        // 获取webuploader队列中的有效文件数
        getFileNum : function(excludedFile) {
            var files = this.webuploader.getFiles();
            var number = files.length;
            for (var i = 0; i < files.length; i++) {
                var status = files[i].getStatus();
                if (!status || status == "cancelled") {
                    number--;
                } else if (excludedFile && files[i].id == excludedFile.id) {
                    number--;
                }
            }
            return number;
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
                message : $.tnx.message(this, code, args)
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
        addFile : function(storageUrls) {
            var _this = this;
            this.rpc.getReadMetadatas(storageUrls, function(metadatas) {
                var _wu = _this.webuploader;
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
                        // 由于服务端接收到的文件可能比原始文件更大，此时加载的文件可能超出容量限制，此时通过设置虚假文件大小，避免容量超限报错
                        if (metadata.size > _wu.options.fileSingleSizeLimit) {
                            wuFile.size = _wu.options.fileSingleSizeLimit;
                        } else {
                            wuFile.size = metadata.size;
                        }
                        wuFile.storageUrl = storageUrls[i];
                        wuFile.name = metadata.filename;
                        wuFile.lastModified = metadata.lastModifiedTime;
                        wuFile.url = metadata.readUrl;
                        wuFile.__hash = _this.hashString(wuFile.name + wuFile.size
                                + wuFile.lastModified);
                        wuFile.setStatus("complete"); // 回显文件设置为已完成状态
                        wuFiles.push(wuFile);
                    }
                });
                if (wuFiles.length) {
                    _wu.addFile(wuFiles);
                }
            });
        },
        updateFile : function(fileId) {
            this.updatingFile = this.webuploader.getFile(fileId);
            $("input[type='file']", this.element).trigger("click");
        },
        removeFile : function(fileId) {
            this.webuploader.removeFile(fileId, true);
        }
    };

    var methods = {
        init : function(options) {
            var element = $(this);
            return new UnstructuredUpload(element, options);
        },
        addFile : function(storageUrls) {
            var element = $(this);
            var _unstructuredUpload = element.data("unstructuredUpload");
            _unstructuredUpload.addFile(storageUrls);
        },
        updateFile : function(fileId) {
            var element = $(this);
            var _unstructuredUpload = element.data("unstructuredUpload");
            _unstructuredUpload.updateFile(fileId);
        },
        removeFile : function(fileId) {
            var element = $(this);
            var _unstructuredUpload = element.data("unstructuredUpload");
            _unstructuredUpload.removeFile(fileId);
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
