/**
 * truenewx.js v1.1.0
 *
 * Depends on: sugar.js, jquery.js, jquery.json.js, bootstrap.js
 */

if ($.parseJSON) {
    var fnParseJson = $.parseJSON;
    $.parseJSON = function(json, replace) {
        if (json) {
            if (replace == true) {
                json = json.replace(/'/g, "\"");
            }
            return fnParseJson(json);
        }
        return undefined;
    };
}

var fnShow = $.fn.show; // 缓存原来的show()
$.fn.show = function() {
    this.removeClass("hide");
    this.removeClass("hidden");
    return fnShow.apply(this);
};

/**
 * 控制台工具
 */
$.console = {
    debug : function(message) {
        if (typeof console == "object" && typeof console.debug == "function") {
            console.debug(message);
        }
    },
    error : function(message) {
        if (typeof console == "object" && typeof console.error == "function") {
            console.error(message);
        }
    },
    info : function(message) {
        if (typeof console == "object" && typeof console.info == "function") {
            console.info(message);
        }
    },
    warn : function(message) {
        if (typeof console == "object" && typeof console.warn == "function") {
            console.warn(message);
        }
    }
};

$.bootstrap = {
    getVersion : function() {
        if ($.isFunction($.fn.typeahead)) {
            return 2;
        }
        if ($.isFunction($.fn.emulateTransitionEnd)) {
            return 3;
        }
        return undefined;
    }
};

/**
 * 等待指定断言为真后执行指定函数
 *
 * @param predicate
 *            断言
 * @param func
 *            要执行的函数
 * @param interval
 *            每次断言为否时等待的间隔时间，单位：毫秒
 */
$.wait = function(predicate, func, interval) {
    if (!interval) {
        interval = 100;
    }
    var id = undefined;
    id = setInterval(function() {
        if (predicate.call()) {
            func.call();
            clearInterval(id);
        }
    }, interval);
};

/**
 * 使当前DOM元素在指定容器中水平和垂直居中
 *
 * @param container
 *            容器，不指定时为window
 */
$.fn.center = function(container) {
    if (!container) {
        container = $(window);
        this.css("position", "fixed");
    } else if (!(container instanceof jQuery)) {
        container = $(container);
        this.appendTo(container);
        this.css("position", "relative");
    }

    var locate = function(container, element) {
        if (element["float"] != false) {
            var width = element.outerWidth();
            var height = element.outerHeight();
            var containerWidth = container.width();
            var containerHeight = container.height();
            var dialogLeft = 0;
            if (containerWidth > width) {
                dialogLeft = (containerWidth - width) / 2;
            }
            var dialogTop = 0;
            if (containerHeight > height) {
                dialogTop = (containerHeight - height) / 2;
            }
            element.css("left", dialogLeft + "px");
            element.css("top", dialogTop + "px");
        }
    };
    locate(container, this);
    this.css("margin-left", "0px");
    this.css("margin-top", "0px");

    var _this = this;
    container.resize(function() {
        _this.stop();
        locate(container, _this);
    });

    return this;
};

/**
 * 闪现当前DOM元素
 *
 * @param inDuration
 *            淡入耗时
 * @param stayDuration
 *            显示时间
 * @param outDuration
 *            淡出耗时
 * @param callback
 *            完成淡出后的回调函数
 */
$.fn.flash = function(inDuration, stayDuration, outDuration, callback) {
    if (outDuration == undefined && callback == undefined) {
        if (typeof stayDuration == "function") {
            callback = stayDuration;
            stayDuration = inDuration;
        } else if (stayDuration == undefined) {
            if (typeof inDuration == "function") {
                callback = inDuration;
            } else {
                stayDuration = inDuration;
            }
        }
        inDuration = undefined;
        outDuration = undefined;
    }
    if (typeof stayDuration != "number" || stayDuration < 0) {
        stayDuration = 1500; // 默认显示1.5秒
    }
    var _this = this;
    this.fadeIn(inDuration, function() {
        setTimeout(function() {
            _this.fadeOut(outDuration, callback);
        }, stayDuration);
    });
};

/**
 * 禁用当前DOM元素
 */
$.fn.disable = function(disabled) {
    this.attr("disabled", "disabled");
};

/**
 * 使当前DOM元素可用
 */
$.fn.enable = function() {
    this.removeAttr("disabled");
}

/**
 * 对象工具
 */
$.Object = {
    /**
     * 织入函数
     */
    weave : function(target, before, functionName, after) {
        if (target) {
            if (typeof before == "string") {
                after = functionName;
                functionName = before;
                before = undefined;
            }
            var fn = target[functionName];
            if (typeof fn == "function") {
                target[functionName] = function() {
                    if (typeof before == "function") {
                        try {
                            before.apply(target, arguments);
                        } catch (error) {
                            $.console.error(error.message);
                            return;
                        }
                    }
                    var result = fn.apply(target, arguments);
                    if (typeof after == "function") {
                        try {
                            after.apply(target, arguments);
                        } catch (error) {
                            $.console.error(error.message);
                            return;
                        }
                    }
                    return result;
                };
            }
            return target[functionName];
        }
        return undefined;
    }
};

/**
 * 字符串工具
 */
$.String = {
    /**
     * 获取指定URL的上一级URL
     *
     * @param url
     *            URL
     */
    getUpperUrl : function(url) {
        var index = url.lastIndexOf("/");
        return url.substring(0, index);
    },
    /**
     * 获取相对于指定js文件的相对路径的绝对路径
     *
     * @param jsFileName
     *            js文件名
     * @param relativePath
     *            相对路径
     * @returns 绝对路径
     */
    getUrlToJs : function(jsFileName, relativePath) {
        if (!jsFileName.startsWith("/")) {
            jsFileName = "/" + jsFileName;
        }
        var result = undefined;
        $.each(document.scripts, function(i, script) {
            if (script.src.indexOf(jsFileName) > 0) {
                result = script.src;
                return false;
            }
        });
        if (result && relativePath) {
            var pathes = relativePath.split("/");
            if (pathes.length > 0) {
                result = $.String.getUpperUrl(result);
                pathes.each(function(path) {
                    if (path == "..") {
                        result = $.String.getUpperUrl(result);
                    } else if (path == ".") {
                        // 不变
                    } else if (path.length > 0) {
                        result += "/" + path;
                    }
                });
            }
        }
        return result;
    },
    /**
     * 对文本超长进行处理
     *
     * @param str
     *            需处理的文本
     * @param maxLen
     *            最大长度
     * @param replaceStr
     *            超长替换符
     *
     * @returns 处理后的文本
     */
    cut : function(str, maxLen, replaceStr) {
        var strLen = str.length, cutLen = maxLen || 0;
        replaceStr = replaceStr || "";

        if (strLen <= maxLen) {
            return str;
        }

        // 如果有超长替换符, 则少截取一位显示替换符
        if (replaceStr != "") {
            cutLen--;
        }

        return str.substring(0, cutLen) + replaceStr;
    }
};

$.tnx = {
    name : "truenewx",
    _version : "1.1.0",
    encoding : "UTF-8",
    locale : "zh_CN",
    messages : {
        "zh_CN" : {
            alert : {
                title : "提示",
                ok : "确定"
            },
            confirm : {
                title : "确定",
                yes : "确定",
                no : "取消"
            },
            error : {
                title : "错误"
            }
        },
        "zh_TW" : {
            alert : {
                title : "提示",
                ok : "確定"
            },
            confirm : {
                title : "確定",
                yes : "確定",
                no : "取消"
            },
            error : {
                title : "錯誤"
            }
        },
        "en" : {
            alert : {
                title : "Alert",
                ok : "OK"
            },
            confirm : {
                title : "Confirm",
                yes : "Yes",
                no : "No"
            },
            error : {
                title : "Error"
            }
        }
    },
    message : function(subject, code, args) {
        var messages;
        if ($.tnx.locale.startsWith("en")) {
            messages = subject.messages["en"];
        } else {
            messages = subject.messages[$.tnx.locale];
        }
        if (code) {
            if (messages) {
                var message = messages[code];
                if (message && args) {
                    $.each(args, function(index, arg) {
                        var regexp = new RegExp("\\{" + index + "\\}", "g");
                        message = message.replace(regexp, args[index]);
                    });
                }
                return message;
            } else {
                return undefined;
            }
        } else {
            return messages;
        }
    },
    namespace : function(namespace) {
        var names = namespace.split(".");
        var space = null;
        // 判断第一级对象是否已存在，若不存在则初始化为{}
        eval("if(typeof " + names[0] + " === 'undefined'){" + names[0] + " = {};}");
        // 取得第一级对象的引用
        eval("space = " + names[0] + ";");
        // 创建剩余级次的对象
        for (var i = 1; i < names.length; i++) {
            var name = names[i];
            space[name] = space[name] || {};
            space = space[name];
        }
        return space;
    },
    imports : function(url, callback, serial) {
        var cssUrls = [];
        var jsUrls = [];
        var funcTypeUrl = function(url) {
            var index = url.indexOf("?");
            var params = "";
            if (index >= 0) { // 拆分可能的参数部分
                params = url.substr(index);
                url = url.substr(0, index);
            }
            if (url.toLowerCase().endsWith(".js")) {
                jsUrls.push({
                    src : url + params,
                    type : "text/javascript",
                    charset : $.tnx.encoding
                });
            } else if (url.toLowerCase().endsWith(".css")) {
                cssUrls.push(url + params);
            }
        };
        if (typeof url === "string") {
            funcTypeUrl(url);
        } else if ($.isArray(url)) {
            $.each(url, function() {
                funcTypeUrl(this);
            });
        }
        cssUrls.each(function(cssUrl) {
            var href = 'href="' + cssUrl + '"';
            if ($('link[' + href + ']').length == 0) {
                $("body").append('<link type="text/css" rel="stylesheet" ' + href + '></link>');
            }
        });
        // 回调函数对css无效，如果同时存在css和js，则有极小几率在css未加载完毕之前执行回调函数
        if (jsUrls.length > 0) {
            if (serial == true && jsUrls.length > 1) { // 多个js文件串行加载
                var loadScript = function(i) {
                    $LAB.script(jsUrls[i]).wait(function() {
                        if (i < jsUrls.length - 1) {
                            loadScript(i + 1);
                        } else if (typeof callback == "function") {
                            callback.call();
                        }
                    });
                };
                loadScript(0);
            } else { // 并行加载
                if (callback) {
                    if (typeof callback == "function") {
                        $LAB.script(jsUrls).wait(callback);
                    } else {
                        $LAB.script(jsUrls).wait();
                    }
                } else {
                    $LAB.script(jsUrls);
                }
            }
        }
    },
    maxZIndex : function(objs) {
        var result = -1;
        $.each(objs, function(i, obj) {
            var zIndex = Number($(obj).css("zIndex"));
            if (result < zIndex) {
                result = zIndex;
            }
        });
        return result;
    },
    /**
     * 获取最小的可位于界面顶层的ZIndex
     */
    minTopZIndex : function(step) {
        var maxValue = 2147483584; // 允许的最大值，取各浏览器支持的最大值中的最小一个（Opera）
        var maxZIndex = this.maxZIndex($("body *")); // 已有DOM元素中的最高层级
        if (maxZIndex > maxValue - step) {
            return maxValue;
        } else {
            return maxZIndex + step;
        }
    },
    /**
     * 加载模板
     *
     * @param relativeUrl
     *            模板文件相对URL
     * @param baseFile
     *            相对URL基于的文件名
     * @returns 模板对象
     */
    loadTemplate : function(relativeUrl, baseFile) {
        if (!$.tnx._templates) {
            $.tnx._templates = {};
        }
        if (!baseFile) {
            baseFile = $.tnx.name + ".js";
        }
        if (!$.tnx._templates[baseFile]) {
            $.tnx._templates[baseFile] = {};
        }
        if (!$.tnx._templates[baseFile][relativeUrl]) {
            var url = $.String.getUrlToJs(baseFile, relativeUrl);
            var response = $.ajax(url, {
                async : false
            });
            if (response.status == 200) {
                $.tnx._templates[baseFile][relativeUrl] = response.responseText.trim();
            }
        }
        return $.tnx._templates[baseFile][relativeUrl];
    },
    alert : function(content, title, callback) {
        if (typeof title == "function") {
            callback = title;
            title = undefined;
        }
        if (!title) {
            title = $.tnx.message($.tnx).alert.title;
        }
        this.dialog(title, content, [ {
            text : $.tnx.message($.tnx).alert.ok,
            "class" : "btn-primary",
            focus : true,
            click : function() {
                if (callback) {
                    callback.apply(this);
                }
                this.close();
            }
        } ], {
            events : {
                close : callback
            }
        });
    },
    confirm : function(content, callback, options) {
        // 不限制回调函数和选项对象的顺序
        if (!options) {
            options = {};
        } else if (typeof callback == "object" && typeof options == "function") {
            var temp = callback;
            callback = options;
            options = temp;
        }
        var title = options.title || $.tnx.message($.tnx).confirm.title;
        var yesText = options.yes || $.tnx.message($.tnx).confirm.yes;
        var noText = options.no || $.tnx.message($.tnx).confirm.no;
        var buttonStyle = options.style ? options.style : "";
        if (buttonStyle.indexOf("margin-left") < 0) {
            buttonStyle += "margin-left: 10px;";
        }
        this.dialog(title, content, [ {
            text : yesText,
            "class" : "btn-primary",
            style : buttonStyle,
            focus : true,
            click : function() {
                if (callback) {
                    callback.call(this, true);
                }
                this.close();
            }
        }, {
            text : noText,
            "class" : "btn-default",
            style : buttonStyle,
            click : function() {
                if (callback) {
                    callback.call(this, false);
                }
                this.close();
            }
        } ], {
            maxWidth : options.maxWidth,
            events : {
                close : function() {
                    callback.call(this, false);
                }
            }
        });
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
     * @param options
     *            选项，形如：{ title: "标题", type: "GET", //或'POST'，默认为'GET' callback:
     *            function(){
     *            //窗体显示完全后调用的回调函数，其this为模态对话框窗体jquery对象，有一个参数为内容的容器jquery对象 } }
     */
    open : function(url, params, buttons, options) {
        if ($.isArray(params)) {
            options = options || buttons;
            buttons = params;
            params = undefined;
        }
        options = options || {};
        var resp = $.ajax(url, {
            cache : false,
            data : params,
            type : options.type,
            dataType : "html",
            contentType : "application/x-www-form-urlencoded; charset=" + $.tnx.encoding, // 不能更改
            error : options.error,
            success : function(html) {
                html = html.trim();
                var container;
                if (html.toLowerCase().startsWith("<body")) {
                    html = html.replace(/<[bB][oO][dD][yY] /, "<div ").replace(
                            /<[bB][oO][dD][yY]>/, "<div>").replace(/<\/[bB][oO][dD][yY] *>/,
                            "</div>");
                    container = $(html);
                    html = container.html(); // body内部元素才作为显示内容
                } else {
                    container = $(html);
                    if (container.length != 1) {
                        container.wrap("<div></div>");
                        container = container.parent();
                    }
                }
                var events = {};
                if (options.events) {
                    events = options.events;
                }
                if (typeof options.callback == "function") {
                    events.shown = function() {
                        options.callback.call(this, container);
                    };
                }
                var title = options.title ? options.title : container.attr("title");
                var width = options.width ? options.width : container.attr("width");
                if (width && !width.toLowerCase().endsWith("px")) {
                    width += "px";
                }
                $.tnx.dialog(title, html, buttons, {
                    width : width,
                    backdrop : options.backdrop,
                    events : events
                });
            }
        });
        resp.fail(options.error);
    },
    ajax : function(url, params, callback, type) {
        if (typeof params == "function") {
            type = callback;
            callback = params;
            params = undefined;
        }
        if (typeof callback == "string") {
            type = callback;
            callback = undefined;
        }
        var options = {
            cache : false,
            data : params,
            type : type || "GET",
            dataType : "html",
            contentType : "application/x-www-form-urlencoded; charset=" + $.tnx.encoding // 不能更改
        };
        if (callback) {
            options.success = callback;
            options.async = true; // 有回调函数则异步
        } else {
            options.async = false;
        }
        var resp = $.ajax(url, options);
        if (options.async === false) { // 同步，返回结果
            return resp.responseText;
        }
    },
    /**
     * 闪现对话框
     *
     * @param content
     *            内容
     * @param timeout
     *            显示停留时间
     * @param callback
     *            闪现完成后的回调函数
     */
    flash : function(content, timeout, callback) {
        if (typeof timeout == "function") {
            callback = timeout;
            timeout = undefined;
        }
        var options = {
            backdrop : false,
            events : {
                shown : function() {
                    var dialogObj = this;
                    if (typeof timeout != "number" || timeout < 0) {
                        timeout = 1500; // 默认显示1.5秒
                    }
                    setTimeout(function() {
                        dialogObj.close();
                    }, timeout);
                },
                hidden : callback
            }
        };
        if (typeof content == "string") {
            content += "<button type=\"button\" class=\"close\">&times;</button>";
        } else if (content instanceof jQuery) {
            content.after("<button type=\"button\" class=\"close\">&times;</button>");
        }
        this.dialog(undefined, content, undefined, options);
    },
    cache : function(key, value) {
        return $.data(document, key, value);
    }
};

$.tnx.pager = {
    callback : null,
    contextPath : "",
    /**
     * 获取指定相对URL的绝对URL
     *
     * @param url
     *            相对URL
     * @returns {String} 绝对URL
     */
    getUrl : function(url) {
        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        return this.contextPath + url;
    },
    /**
     * 指定每页显示大小
     *
     * @param pageSize
     *            页大小
     */
    changePageSize : function(pageSize) {
        $("#pageSize").val(pageSize);
        $("#pageNo").val(1);
        $("#pageNo").attr("name", "pageNo");
        if (this.callback) {// 判断是否有回调方法
            if (typeof this.callback == "function") {
                var callbacks = $.Callbacks('once');
                callbacks.add(this.callback);
                callbacks.fire(); // 执行回调方法
            }
        } else {
            var formObj = $("#pageNo")[0].form;
            if (formObj.length > 0) {
                if (formObj.onsubmit != null && formObj.onsubmit() == false) {
                    return;
                }
                formObj.submit();
            }
        }
    },
    /**
     * 跳转至指定页
     *
     * @param pageNo
     *            指定页
     */
    toPage : function(ele, pageNo) {
        var $ele = $(ele), $form = $ele.parents("form")[0];
        if (!pageNo) {
            pageNo = $("#pageNo", $form).val();
        }
        var pageTotal = parseInt($("#pageTotal", $form).val());
        if (!(/^\d+$/.test(pageNo)) || parseInt(pageNo) <= 0 || parseInt(pageNo) > pageTotal) {
            $("#pageNo", $form).focus().select();
            return false;
        }
        $("#pageNo", $form).val(pageNo);
        $("#pageNo", $form).attr("name", "pageNo");
        if (this.callback) {// 判断是否有回调方法
            if (typeof this.callback == "function") {
                var callbacks = $.Callbacks('once');
                callbacks.add(this.callback);
                callbacks.fire(); // 执行回调方法
            }
        } else {
            if ($form && $form.length > 0) {
                if ($form.onsubmit != null && $form.onsubmit() == false) {
                    return;
                }
                $form.submit();
            }
        }
    },
    pageNoKeydown : function(event) {
        var keyCode = event.which;
        if (keyCode == 13) {
            this.toPage();
            return false;
        }
    },
    pageNoKeyup : function(el) {
        var value = $(el).val();
        if (!(/^\d+$/.test(value)) || parseInt(value) <= 0) {
            var re = /[^\d]/g;
            value = value.replace(re, "");
            if (value == "0") {
                value = "";
            }
            $(el).val("").focus().val(value);// 获得焦点后重新把自己复制粘帖一下
        }
    },
    /**
     * rpc调用分页
     *
     * @param paging
     *            分页对象
     * @param args
     *            分页参数（goText=GO&pageSizeOptions=10,20,40&showPageNo=true&pageNoSpan=1）...
     * @param _callback
     *            回调方法
     */
    rpcPager : function(paging, args, _callback) {
        this.callback = _callback;
        var params = "total=" + paging.total + "&pageSize=" + paging.pageSize + "&pageNo="
                + paging.pageNo + "&morePage=" + paging.morePage;
        if (args != null && args != "") {
            params += "&" + args;
        }
        var options = {
            cache : false,
            type : "GET",
            contentType : "application/x-www-form-urlencoded; charset=" + $.tnx.encoding, // 不能更改
            async : false,
            data : params
        };
        var url = this.getUrl("/pager.ajax");
        var resp = $.ajax(url, options);
        if (options.async === false) { // 同步，返回结果
            var response = resp.responseText;
            if (response) {
                $("#pager").html(response);
            }
        }
    }
};

/**
 * RPC
 */
$.tnx.rpc = {
    requestType : "POST",
    _cacheKey : $.tnx.name + ".rpc",
    invoke : function(beanId, methodName, args, success, error, contextUrl) {
        if (typeof args == "function") {
            contextUrl = error;
            error = success;
            success = args;
            args = [];
        }
        var options = {
            cache : false,
            type : this.requestType,
            contentType : "application/x-www-form-urlencoded; charset=" + $.tnx.encoding,// 不能更改
            dataType : "json"
        };
        if ($.isArray(args)) { // 数组型参数
            options.data = {
                args : $.toJSON(args)
            };
        } else { // Map型参数
            options.data = args;
        }
        options.async = false; // 默认同步
        if (success) {
            options.success = success;
            options.async = true; // 有回调函数则异步
        }
        var url = undefined;
        if (contextUrl) {
            url = contextUrl + "/rpc/invoke/" + beanId + "/" + methodName + ".json";
            // 跨域访问必须使用GET请求，数据格式必须JSONP
            options.type = "GET";
            options.dataType = "jsonp";
        } else {
            url = $.tnx.pager.getUrl("/rpc/invoke/" + beanId + "/" + methodName + ".json");
        }
        if (error || options.async) {
            var _this = this;
            options.error = function(resp, textStatus, errorThrown) {
                _this._handleErrors(resp, error, function() {
                    _this._request(url, options, error);
                });
            };
            options.async = true; // 有回调函数则异步
        }
        return this._request(url, options, error);
    },
    _request : function(url, options, error) {
        options.contentType = "application/x-www-form-urlencoded; charset=" + $.tnx.encoding; // 不能更改
        var resp = $.ajax(url, options);
        if (options.async === false) { // 同步，则返回结果
            if (this._handleErrors(resp, error)) {
                return $.parseJSON(resp.responseText);
            }
        }
    },
    /**
     * 处理响应中的错误
     *
     * @param response
     *            响应
     * @param error
     *            错误回调函数
     * @return 如果没有错误返回true，否则返回false
     */
    _handleErrors : function(response, error, loginedCallback) {
        if (response.status == 200) {
            return true;
        }
        // 401错误，未登录
        if ((response.status == 401) && typeof this.toLogin == "function") {
            this.toLogin(loginedCallback);
        }
        // 500错误，服务器内部错误
        if (response.status == 500) {
            throw new Error(response.responseText);
        }
        // 409错误，借用表示业务异常
        if (response.status == 409) {
            var errors = $.parseJSON(response.responseText).errors;
            if (errors.length > 0) {
                if (error) {
                    if (errors.length == 1) {
                        error(errors[0]);
                    } else {
                        error(errors);
                    }
                } else {
                    this.error.apply(this, errors);
                }
            }
        }
        return false;
    },
    /**
     * 默认错误处理函数
     */
    error : function(error) {
        var message = undefined;
        if ($.isArray(error)) {
            message = "";
            $.each(error, function(i) {
                if (i > 0) {
                    message += "<br/>";
                }
                message += this.message || this.code;
            });
        } else {
            message = error.message || error.code;
        }
        this.showErrorMessage(message);
    },
    showErrorMessage : function(message) {
        $.tnx.alert(message, $.tnx.message($.tnx).error.title);
    },
    imports : function(beanId, contextUrl, callback) {
        if (typeof (contextUrl) == "function") {
            callback = contextUrl;
            contextUrl = undefined;
        }

        var rpcObjects = $.tnx.cache(this._cacheKey);
        if (!rpcObjects) {
            rpcObjects = {};
            $.tnx.cache(this._cacheKey, rpcObjects);
        }
        if (rpcObjects[beanId]) {
            if (callback) {
                callback.call(this, rpcObjects[beanId]);
                return;
            } else {
                return rpcObjects[beanId];
            }
        }

        var url = undefined;
        if (contextUrl) {
            url = contextUrl + "/rpc/methods/" + beanId + ".json";
        } else {
            url = $.tnx.pager.getUrl("/rpc/methods/" + beanId + ".json");
        }
        var options = {
            cache : false,
            async : false,
            dataType : "json",
            contentType : "application/x-www-form-urlencoded; charset=" + $.tnx.encoding // 不能更改
        };
        if (callback) {
            var _this = this;
            options.async = true;
            options.success = function(methodNames) {
                var rpcObject = _this._buildRpcObject(beanId, methodNames, contextUrl);
                rpcObjects[beanId] = rpcObject;
                callback.call(_this, rpcObject);
            }
            options.error = function(resp) {
                _this._handleErrors(resp);
            }
            $.ajax(url, options);
        } else {
            var resp = $.ajax(url, options);
            if (this._handleErrors(resp)) {
                var methodNames = $.parseJSON(resp.responseText);
                var rpcObject = this._buildRpcObject(beanId, methodNames, contextUrl);
                rpcObjects[beanId] = rpcObject;
                return rpcObject;
            }
        }
    },
    _buildRpcObject : function(beanId, methodNames, contextUrl) {
        var _this = this;
        var rpcObject = {};
        $.each(methodNames, function(index, methodName) {
            rpcObject[methodName] = function() {
                var success = undefined;
                var error = undefined;
                var length = arguments.length;
                var paramLength = length;
                if (length > 0 && typeof arguments[length - 1] == "function") { // 最后一个参数为函数
                    success = arguments[length - 1];
                    paramLength = length - 1;
                }
                if (length > 1 && typeof arguments[length - 2] == "function") { // 倒数第二个参数为函数
                    error = success;
                    success = arguments[length - 2];
                    paramLength = length - 2;
                }
                var args = new Array();
                for (var i = 0; i < paramLength; i++) {
                    args.push(arguments[i]);
                }
                return _this.invoke(beanId, methodName, args, success, error, contextUrl);
            };
        });
        return rpcObject;
    }
};
