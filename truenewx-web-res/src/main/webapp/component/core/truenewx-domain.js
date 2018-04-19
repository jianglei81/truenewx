/**
 * truenewx-domain.js v1.1.0
 * 
 * Depends on: truenewx.js
 */
$.tnx.domain = {
    Controller : Class.extend({
        className : "$.tnx.domain.Controller",
        rpc : undefined,
        getRpcClassNames : function() {
            return undefined;
        },
        init : function() {
            var classNames = this.getRpcClassNames();
            if (classNames) {
                var _this = this;
                $.each(classNames, function(i, className) {
                    var rpcObject = $.tnx.rpc.imports(className);
                    var names = className.split(".");
                    var name = names[names.length - 1];
                    _this.rpc[name] = rpcObject;
                });
            }
        },
        focusEmpty : function(selector, trim) {
            var fieldObj = $(selector);
            var fieldValue = trim ? fieldObj.val().trim() : fieldObj.val();
            if (fieldValue.length == 0) {
                if (trim) {
                    fieldObj.val(fieldValue);
                }
                fieldObj.focus();
                return true;
            }
            return false;
        },
        getDataModel : function(container, types) {
            var model = {};
            var fieldObjs = $(
                    "input[name],textarea[name],select[name],[name][contenteditable='true']",
                    container);
            fieldObjs.each(function() {
                var $this = $(this);
                var tagName = this.tagName.toLowerCase();
                var requiresArray = false;
                var value = undefined;
                if (tagName == "select") {
                    value = $this.find("option:selected").val();
                } else {
                    value = $this.val() || $this.text();
                    if (tagName == "input") {
                        var type = $this.attr("type");
                        if (type) {
                            type = type.toLowerCase();
                            if (type == "checkbox" || type == "radio") {
                                // 忽略未选中的复选框和单选框
                                if (!$this.is(":checked")) {
                                    value = undefined;
                                }
                                if (type == "checkbox") { // 如果存在多个相同名称的复选框，则限定字段类型为数组
                                    var name = $this.attr("name");
                                    var nameObjs = $("input[type='checkbox']" + "[name='" + name
                                            + "']");
                                    if (nameObjs.length > 1) {
                                        requiresArray = true;
                                    }
                                }
                            }
                        }
                    }
                }
                if (value != undefined) {
                    var name = $this.attr("name");
                    if (types && typeof value == "string") { // 类型转换
                        var type = types[name];
                        if (type) {
                            if (type.endsWith("[]")) {
                                requiresArray = true;
                                type = type.substring(0, type.length - 2);
                            }
                            switch (type.toLowerCase()) {
                            case "int":
                                value = parseInt(value);
                                break;
                            case "float":
                                value = parseFloat(value);
                                break;
                            case "number":
                                value = Number(value);
                                break;
                            case "boolean":
                                value = Boolean(value);
                                break;
                            }
                        }
                    }
                    if (model[name] == undefined) { // 没有相同名称字段，则直接赋值
                        model[name] = value;
                    } else { // 有相同名称字段，则转换为数组
                        if (!$.isArray(model[name])) { // 原来不是数组才转换
                            model[name] = [ model[name] ];
                        }
                        model[name].push(value);
                    }
                    // 要求为数组却不是数组，则转换为数组
                    if (requiresArray && !$.isArray(model[name])) {
                        model[name] = [ model[name] ];
                    }
                }
            });
            return model;
        },
        validateFieldBusiness : function(fieldObj, controllerId) {
            var fieldName = fieldObj.attr("name");
            if (fieldName) {
                var fieldValue = fieldObj.val();
                if (fieldValue) {
                    $.tnx.rpc.imports(controllerId, function(rpc) {
                        if (rpc.validateBusiness) {
                            var formObj = $(fieldObj[0].form);
                            var id = formObj.attr("data-id");
                            if (id) {
                                id = parseInt(id);
                            }
                            var model = {};
                            model[fieldName] = fieldValue;
                            rpc.validateBusiness(id, model, function() {
                            }, function(error) {
                                $.tnx.validator.showFieldErrors(fieldObj, error.message);
                                fieldObj.attr("business", "false");
                                fieldObj.focus();
                            });
                        }
                    });
                }
            }
        },
        bindBusinessValidate : function(controllerId) {
            var _this = this;
            $("form input[name][business]").blur(function() {
                _this.validateFieldBusiness($(this), controllerId);
            });
        },
        validateFormBusiness : function(formObj, controllerId) {
            var model = {};
            $("input[name][business]", formObj).each(function() {
                var fieldObj = $(this);
                var fieldValue = fieldObj.val();
                if (fieldValue) {
                    var fieldName = fieldObj.attr("name");
                    model[fieldName] = fieldValue;
                }
            });
            if (!$.isEmptyObject(model)) {
                var _this = this;
                $.tnx.rpc.imports(controllerId, function(rpc) {
                    if (rpc.validateBusiness) {
                        var id = formObj.attr("data-id");
                        if (id) {
                            id = parseInt(id);
                        }
                        rpc.validateBusiness(id, model, function() {
                        }, function(error) {
                            $.tnx.validator.showFieldErrors(fieldObj, error.message);
                            fieldObj.attr("business", "false");
                            fieldObj.focus();
                        });
                    }
                });
            }
        }
    }),
    /**
     * 加载容器插件
     */
    loadContainerAddon : function(container, win) {
        if (!container) {
            container = $("body");
        }
        // 先加载依赖组件
        var components = container.attr("component");
        if (components) {
            components = components.split(",");
            var urls = [];
            var callbacks = [];
            components.each(function(component) {
                component = component.trim();
                if (component) {
                    var componentUrls = $.tnx.domain.site.components[component];
                    if (componentUrls) {
                        if ($.isArray(componentUrls)) {
                            componentUrls.each(function(url) {
                                if (typeof url == "function") {
                                    callbacks.push(url);
                                } else {
                                    urls.push(url);
                                }
                            });
                        } else {
                            if (typeof componentUrls == "function") {
                                callbacks.push(componentUrls);
                            } else {
                                urls.push(componentUrls);
                            }
                        }
                    }
                }
            });
            var _this = this;
            $.tnx.imports(urls, function() {
                callbacks.each(function(callback) {
                    callback.call();
                });
                _this.onBeforeContainerLoad.call(_this, container, win);
            }, true); // 组件加载完毕后回调加载脚本
        } else { // 没有依赖组件，直接加载脚本
            this.onBeforeContainerLoad(container, win);
        }
    },
    onBeforeContainerLoad : function(container, win) {
        // 加载功能脚本前，先初始化组件
        $("[render]").each(function() {
            var obj = $(this);
            var name = obj.attr("render");
            var options = obj.attr("options");
            if (options && options.startsWith("{") && options.endsWith("}")) {
                options = $.parseJSON(options.replace(/'/g, "\""));
            }
            if (typeof obj[name] == "function") {
                try {
                    obj[name].call(obj, options);
                    // render属性替换成rendered，表示已经渲染，并避免再次被渲染
                    obj.attr("rendered", obj.attr("render")).removeAttr("render");
                } catch (e) {
                    $.console.error(e.message);
                }
            }
        });
        if ($.tnx.validator) {
            $.tnx.validator.render();
        }
        var _this = this;
        // 加载功能脚本
        var scripts = container.attr("script");
        if (scripts) {
            scripts = scripts.split(",");
            scripts.each(function(script, i) {
                script = script.trim();
                if (script && script.toLowerCase().endsWith(".js")) {
                    if (!script.startsWith("/")) {
                        scripts[i] = $.tnx.domain.site.util.absUrl(site.namespace + "/" + script);
                    } else {
                        scripts[i] = script;
                    }
                    if ($.tnx.domain.site.version) { // 脚本路径附加版本信息，以更新客户端缓存
                        scripts[i] += "?v=" + $.tnx.domain.site.version;
                    }
                } else { // 无效的脚本文件置空
                    scripts[i] = undefined;
                }
            });
            $.tnx.imports(scripts, function() {
                // 加载完所有js之后，依次执行其中的Controller.onLoad()
                controllers = []; // 使用全局的controlls保存所有Controller实例
                scripts.each(function(script) {
                    var namespace = $.tnx.domain.site.util.jsUrl2Namespace(script);
                    var className = namespace + ".Controller";
                    try {
                        eval(namespace + ".controller = new " + className + "()");
                        // 使用全局的controller保存最后一个Controller实例
                        eval("controller = " + namespace + ".controller");
                        controllers.push(controller);
                    } catch (e) {
                        $.console.error(e);
                        return;
                    }
                    controller.className = className;
                    if (typeof controller.onLoad == "function") {
                        if (!win) {
                            win = window;
                        }
                        controller.onLoad(win);
                    }
                });
                setTimeout(function() {
                    // 初始化后添加输入控件自动执行trim动作的事件
                    _this.bindInputTrim();
                    // 执行完所有Controller.onLoad()后初始化图片延时加载
                    _this.initImageLazyLoad();
                }, 500);
            }, true);
        }
        if (!scripts || !scripts.length) {
            _this.bindInputTrim();
            _this.initImageLazyLoad();
        }
    },
    bindInputTrim : function() {
        $("input,textarea").focusout(function() {
            var fieldObj = $(this);
            // 未设置trim且不是file类型，默认执行trim动作
            if (fieldObj.attr("trim") !== "false" && fieldObj.attr("type") != "file") {
                fieldObj.val(fieldObj.val().trim());
            }
        });
    },
    initImageLazyLoad : function(container, placeholderImageUrl, loadingImageUrl) {
        if (typeof (container) == "string") {
            loadingImageUrl = placeholderImageUrl;
            placeholderImageUrl = container;
            container = undefined;
        }
        if ($("img:first").lazyload) { // 已加载lazyload
            if ($.tnx.context) {
                if (!placeholderImageUrl) {
                    placeholderImageUrl = $.tnx.context + "/assets/image/placeholder.png"; // 默认的占位图片路径
                }
                if (!loadingImageUrl) {
                    loadingImageUrl = $.tnx.context + "/assets/image/loading.gif"; // 默认的加载中图片路径
                }
            }
            $("img[data-src]:not([src][data-original])", container).each(function(index, image) {
                image = $(image);
                if (loadingImageUrl) {
                    image.css("background", "url(" + loadingImageUrl + ") no-repeat center");
                }
                var src = image.attr("data-src");
                if (placeholderImageUrl) {
                    image.attr("src", placeholderImageUrl);
                } else {
                    image.removeAttr("src");
                }
                image.attr("data-original", src);
                image.removeAttr("data-src");
            });
            var options = {
                skip_invisible : false,
                placeholder : placeholderImageUrl
            };
            $("img[data-original]:not([data-container])").lazyload(options);
            $("img[data-original][data-container]").each(function(index, image) {
                image = $(image);
                var containerSelector = image.attr("data-container");
                image.lazyload($.extend({}, options, {
                    container : $(containerSelector)
                }));
            });
        } else { // 未加载lazyload
            $("img[data-src]:not([src][data-original])", container).each(function(index, image) {
                image = $(image);
                var src = image.attr("data-src");
                // 图片标签上存在data-src但不存在src，因未加载lazyload，故需要将data-src更改为src
                if (src && !image.attr("src")) {
                    image.attr("src", src);
                    image.removeAttr("data-src");
                }
            });
        }
    }
};

/**
 * 站点设置，由具体站点负责初始化
 */
$.tnx.domain.site = {
    namespace : "site", // 站点默认命名空间
    path : {
        context : $.tnx.siteContext, // 默认站点根路径
        assets : $.tnx.siteContext + "/assets", // 默认站点资源文件夹相对域名的路径
        // 默认站点js文件夹相对域名的路径
        js : $.tnx.siteContext + "/assets/js"
    },
    components : {}, // 组件名称-相关js文件相对域名路径清单的映射集
    Controller : $.tnx.domain.Controller.extend({
        init : function() {
            this._super(); // 调用父类构造函数
            this.className = $.tnx.domain.site.namespace + ".Controller";
        },
        onLoad : function() {
        }
    }),
    init : function(container, win) {
        $.tnx.domain.loadContainerAddon(container, win);
    },
    util : { // 站点工具对象
        namespace2JsUrl : function(namespace) {
            var array = namespace.split(".");
            var url = $.tnx.domain.site.path.js;
            for (var i = 1; i < array.length; i++) {
                url += "/" + array[i].replace("_", "-");
            }
            return url + ".js";
        },
        absUrl : function(url) {
            if (url.startsWith("/")) {
                return url;
            }
            var index = url.lastIndexOf(".");
            var extension = url.substring(index + 1);
            return $.tnx.domain.site.path.assets + "/" + extension + "/" + url;
        },
        jsUrl2Namespace : function(url) {
            var index = url.indexOf("?");
            if (index >= 0) { // 去掉可能的参数部分
                url = url.substr(0, index);
            }
            url = this.absUrl(url);
            var prefix = $.tnx.domain.site.path.js + "/";
            var extension = ".js";
            if (!url.startsWith(prefix) || !url.endsWith(extension)) {
                return;
            }
            var array = url.substring(prefix.length, url.length - extension.length).split("/");
            var namespace = "";
            $.each(array, function() {
                namespace += "." + this.replace(/\-/g, "_");
            });
            if (namespace.length > 0) {
                namespace = namespace.substring(1);
            }
            return namespace;
        },
        importJsByNamesapce : function(namespace) {
            var url = this.namespace2JsUrl(namespace);
            $.tnx.imports(url);
        }
    },
    /**
     * 用模态窗体打开指定URL
     * 
     * @param url
     *            URL
     * @param params
     *            请求参数
     * @param buttons
     *            按钮集，详见$.tnx.dialog()方法中关于按钮设置的说明
     * @param backdrop
     *            boolean或字符串'static'，boolean表示是否显示遮罩层，字符串'static'表示显示遮罩层且点击窗体外部不会关闭窗体
     * @param extendToWin
     *            附加到弹出窗体对象上的参数对象
     * @param unLogined
     *            未登录异常处理函数，在指定URL要求用户登录却没登录时调用
     */
    open : function(url, params, buttons, backdrop, extendToWin, unLogined) {
        if (typeof backdrop == "object") {
            extendToWin = backdrop;
            backdrop = undefined;
        }
        var _this = this;
        var _controller;
        if (typeof (controller) == "object") {
            _controller = controller; // 缓存当前controller变量
        }
        var options = {
            backdrop : backdrop,
            callback : function(container) {
                if (!(container instanceof jQuery)) {
                    container = $(container);
                }
                var win = this;
                if (extendToWin) {
                    $.extend(win, extendToWin);
                }
                $.tnx.domain.site.init(container, win); // controller变量在此处会被更改
            },
            events : {
                hidden : function() {
                    controller = _controller; // 弹出框关闭后，恢复controller变量
                }
            }
        };
        if (typeof unLogined != "function") {
            unLogined = site.open.unLogined; // 默认的未登录异常处理方法
        }
        if (typeof unLogined == "function") {
            options.error = function(response, textStatus, errorThrown) {
                if (response.called != true && (response.status == 401 || response.status == 511)) {
                    unLogined.call();
                    response.called = true; // 为避免错误回调函数被调用两次
                }
            }
        }
        $.tnx.open(url, params, buttons, options);
    },
    ajax : function(target, url, params, type, callback) {
        if ($.isFunction(type)) {
            callback = type;
            type = undefined;
        }
        $.tnx.ajax(url, params, function(result) {
            result = result.trim();
            if (result.toLowerCase().startsWith("<body")) {
                result = result.replace(/<body /i, "<div ").replace(/<body>/i, "<div>").replace(
                        /<\/body *>/i, "</div>");
                var container = $(result);
                target.html(container);
                $.tnx.domain.loadContainerAddon(container);
            } else {
                target.html(result);
            }
            if ($.isFunction(callback)) {
                callback.call();
            }
        }, type);
    }
};
