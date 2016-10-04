/**
 * tree-selector.js v1.0.0
 *
 * Depends on: jquery.hcolumns.js, jquery.ztree.all-3.5.js, multiselect-view.js
 *
 * 树选择器插件
 */

(function($) {

    var TreeSelector = function(element, options) {
        this.init(element, options);
    };

    TreeSelector.prototype = {
        init : function(element, options) {
            this.element = element;
            this.setOptions(options);
        },
        setOptions : function(options) {
            this.options = $.extend({
                type : "default", // 插件类型(default:调用zTree树插件,column:调用hCloumns插件)
                id : "id", // ID值对应字段名
                pid : "pid", // PID值对应字段名
                caption : "caption", // 显示值对应字段名
                title : "Tree-selector", // 窗口标题
                multiSelect : false, // 是否多选,默认单选
                onlyLeafNode : true, // 是否必须选择叶子节点,默认必须选择叶子节点
                required : false, // 是否必填,默认非必填
                data : null, // 数据源
                dataArr : null, // 转换后的数组
                model : true, // 打开方式，true：模态窗口展示，false：当前页面展示
                selfId : "", // 初始化控件ID
                container : function(type) {
                    switch (type) {
                    case "column":
                        return "<div class='columns'></div>";
                        break;
                    default:
                        return "<ul id='ztree' class='ztree'></ul>";
                        break;
                    }
                },
                onSure : null, // 自定义确定按钮回调函数
                onClear : null, // 自定义清除按钮回调函数
                entryClick : null
            // 自定义hColumn点击回调函数
            }, (options || $.fn.treeSelector.defaults));

            this.options.rpc === undefined ? this.options.type === "default" ? "" : this.format()
                    : this.rpc();

            this.render();
        },
        render : function() { // 控件初始化渲染
            var _this = this, el = $(this.element), $id = el.attr("id");
            this.options.selfId = $id;
            if (!this.options.model) {
                if (this.options.type === "default") {
                    this.loadZtree(this);
                } else if (this.options.type === "column") {
                    this.loadColumn(this);
                }
                return;
            }
            if (this.options.multiSelect) { // 多选
                var settings = {
                    id : this.options.id,
                    caption : this.options.caption,
                    title : this.options.title,
                    btns : []
                };
                if ($.isArray(this.options.btns)) {
                    this.options.btns.each(function(btn) {
                        if (!btn.click) {
                            btn.click = _this.openModal;
                        }
                        settings.btns.push(btn);
                    });
                }
                this.multi = $(this.element).multiselectView(settings);
                $(this.multi.element).data("treeSelector", this);
            } else { // 单选
                var $name = el.attr("name"), $value = el.attr("value"), $class = el.attr("class"), $style = el
                        .attr("style"), initElm = $("<div></div>").addClass(
                        "input-append treeSelector").attr("id", $id), hiddentElm = $("<input />")
                        .attr("type", "hidden").attr("name", $name).attr("value", $value), textElm = $(
                        "<input />").attr("type", "text").attr("value", this.getCaption($value)), addElm = $("<a href='javascript:;'><span class='add-on'><i class='icon-th-list'></i></span></a>");
                if (!$class) {
                    textElm.attr("class", $class);
                }
                if (!$style) {
                    textElm.attr("style", $style);
                }
                initElm[0].appendChild(hiddentElm[0]);
                initElm[0].appendChild(textElm[0]);
                initElm[0].appendChild(addElm[0]);
                el = initElm.replaceAll(el);
                el.on("click", "input[type='text']", _this.openModal).on("click", "a",
                        _this.openModal);
                el.data("treeSelector", this);
            }
        },
        showModal : function(treeModel) {
            var el = treeModel, o = el.options;
            $.tnx.dialog(o.title, o.container(o.type), [ {
                text : "确定",
                "class" : "btn-primary",
                click : function() {
                    if (o.onSure) {
                        if (typeof o.onSure == "function") {
                            var val = $("#column_current_id").val();
                            if (val === "") {
                                $.tnx.alert("请选择" + o.title + "！");
                                return;
                            } else {
                                o.onSure.call(o.onSure, val);
                            }
                        }
                    }
                    this.close();
                }
            }, {
                text : "取消"
            } ], {
                events : {
                    show : function() {
                        if (o.type == "column") {
                            el.loadColumn(el);
                        } else {
                            el.loadZtree(el);
                        }
                        this.center();
                    }
                }
            });
        },
        openModal : function() { // 打开模态窗口
            var el = $(this).parents(".treeSelector").data("treeSelector"), elop = el.options;
            $.tnx.dialog(elop.title, elop.container(elop.type), [
                    {
                        text : "确定",
                        "class" : "btn-primary",
                        click : function() {
                            var selectIds = "";
                            switch (elop.type) {
                            case "column":
                                if (elop.onlyLeafNode) {
                                    if ($("#column_current_leaf").val() === "true") {
                                        $.tnx.alert("必须选择叶子节点！");
                                        return;
                                    }
                                }
                                selectIds = $("#column_current_id").val();
                                $("#" + elop.selfId).find("input[type='hidden']").val(selectIds);
                                $("#" + elop.selfId).find("input[type='text']").val(
                                        $("#column_current_caption").val());
                                break;
                            case "default":
                                var treeObj = $.fn.zTree.getZTreeObj("ztree");
                                if (elop.multiSelect) {
                                    var nodes = treeObj.getCheckedNodes(true);
                                    if (nodes.length > 0) {
                                        var arry = [];
                                        nodes.each(function(node) {
                                            if (node) {
                                                arry.push({
                                                    "id" : node.id,
                                                    "name" : node.name
                                                });
                                            }
                                        });
                                        el.multi.data(arry);
                                    }
                                } else {
                                    var nodes = treeObj.getSelectedNodes();
                                    if (nodes.length > 0) {
                                        selectIds = nodes[0]["id"];
                                        $("#" + elop.selfId).find("input[type='hidden']").val(
                                                selectIds);
                                        $("#" + elop.selfId).find("input[type='text']").val(
                                                nodes[0]["name"]);
                                    }
                                }
                                break;
                            default:
                                break;
                            }
                            if (elop.onSure) {
                                if (typeof elop.onSure == "function") {
                                    if (selectIds === "") {
                                        $.tnx.alert("请选择" + elop.title + "！");
                                        return;
                                    } else {
                                        elop.onSure.call(elop.onSure, selectIds);
                                    }
                                }
                            }
                            this.close();
                        }
                    }, !elop.required ? {
                        text : "清除",
                        click : function() {
                            $("#" + elop.selfId).find("input[type='hidden']").val("");
                            $("#" + elop.selfId).find("input[type='text']").val("");
                            if (elop.onClear) {
                                if (typeof elop.onClear == "function") {
                                    elop.onClear.call(elop.onClear);
                                }
                            }
                            this.close();
                        }
                    } : undefined, {
                        text : "取消"
                    } ], {
                events : {
                    show : function() {
                        switch (elop.type) {
                        case "column":
                            el.loadColumn(el);
                            break;
                        default:
                            el.loadZtree(el);
                            break;
                        }
                        this.center();
                    }
                }
            });
        },
        loadColumn : function(el) { // 加载hColumns插件
            var elop = el.options, $value = elop.model ? $("#" + elop.selfId).find(
                    "input[type='hidden']").val() : $(el.element).attr("data-id"), $caption = elop.model ? $(
                    "#" + elop.selfId).find("input[type='text']").val()
                    : $(el.element).attr("data-value");
            $(".columns").hColumns({
                defaultId : $value,
                defaultCaption : $caption,
                depths : $value === "" ? null : el.recursiveParent($value),
                isLeafNode : $value === "" ? "" : el.isChild($value),
                entryClick : elop.entryClick,
                nodeSource : function(nodeId, callback) {
                    if (nodeId === null) {
                        nodeId = 0;
                    }
                    if (elop.dataArr === null) {
                        return callback("没有数据...");
                    }
                    if (!(nodeId in elop.dataArr)) {
                        return callback("Node not exists");
                    }
                    return callback(null, elop.dataArr[nodeId]);
                }
            });
        },
        loadZtree : function(el) { // 加载zTree插件
            $.fn.zTree.init($(".ztree"), {
                data : {
                    key : {
                        name : el.options.caption
                    // 节点数据保存节点名称的属性名称
                    },
                    simpleData : {
                        enable : true, // 默认使用简单数据模式
                        idkey : el.options.id, // ID值对应字段
                        pIdKey : el.options.pid, // PID值对应字段名
                        rootPId : null
                    }
                },
                check : {
                    enable : el.options.multiSelect,
                    chkboxType : {
                        "Y" : "",
                        "N" : ""
                    }
                },
                view : {
                    selectedMulti : el.options.multiSelect
                // 设置是否允许同时选中多个节点,true / false 分别表示 支持 / 不支持 同时选中多个节点
                }
            }, el.options.data);
            var zTreeObj = $.fn.zTree.getZTreeObj("ztree");
            if (el.options.multiSelect) {
                var value = el.multi.values();
                if (value.length > 1) {
                    value = value.split(",");
                    value.each(function(val) {
                        zTreeObj.checkNode(zTreeObj.getNodeByParam(el.options.id, val, null), true,
                                true);
                    });
                } else if (value.length == 1) {
                    zTreeObj.checkNode(zTreeObj.getNodeByParam(el.options.id, value, null), true,
                            true);
                }
            } else {
                var value = $("#" + el.options.selfId).find("input[type='hidden']").val();
                if (value !== null && value !== "") {
                    zTreeObj.selectNode(zTreeObj.getNodeByParam(el.options.id, value, null));
                }
            }
        },
        format : function() { // 数据格式化
            var el = this, elop = this.options;
            if (elop.data !== null) {
                var arry = {}, tmpArry = [], len = elop.data.length;
                arry[0] = new Array();
                for (var i = 0; i < len; i++) {
                    (function() {
                        var jsonArray = arguments[0];
                        tmpArry = {
                            "id" : jsonArray[elop.id],
                            "pid" : jsonArray[elop.pid],
                            "caption" : jsonArray[elop.caption],
                            "type" : "folder"
                        };
                        if (jsonArray[elop.pid] === undefined || jsonArray[elop.pid] === null
                                || !el.isParent(jsonArray[elop.pid])) {
                            arry[0].push(tmpArry);
                        } else {
                            if (arry[jsonArray[elop.pid]] === undefined) {
                                arry[jsonArray[elop.pid]] = new Array();
                            }
                            arry[jsonArray[elop.pid]].push(tmpArry);
                        }
                    })(elop.data[i]);
                }
                this.options.dataArr = arry;
            }
        },
        rpc : function() { // rpc 请求数据
            var rpcData = $.tnx.rpc.invoke(this.options.rpc.className, this.options.rpc.methodName,
                    this.options.rpc.args);
            if (this.options.data !== null) {
                this.options.data.concat(rpcData);
            } else {
                this.options.data = rpcData;
            }
            this.options.type === "default" ? "" : this.format();
        },
        isParent : function(pid) { // 查询当前项父节点是否存在
            for ( var json in this.options.data) {
                for ( var key in this.options.data[json]) {
                    if (pid === this.options.data[json][this.options.id]) {
                        return true;
                    }
                }
            }
            return false;
        },
        isChild : function(id) { // 根据ID查询是否是叶子节点
            if (id === 0 || id === null) {
                return "";
            }
            return this.options.dataArr[id] === null ? false : true;
        },
        getCaption : function(id) { // 根据默认ID查询对应显示的值
            var caption = "", elop = this.options;
            if (elop.data === null) {
                return caption;
            }
            var len = elop.data.length;
            for (var i = 0; i < len; i++) {
                (function() {
                    var jsonArray = arguments[0];
                    if (id == jsonArray[elop.id]) {
                        caption = jsonArray[elop.caption];
                    }
                })(elop.data[i]);
            }
            return caption;
        },
        recursiveParent : function(id) { // 根据当前项ID递归父节点集合
            var elop = this.options, len = elop.data.length;
            var node = function(id, arry) {
                for (var i = 0; i < len; i++) {
                    (function() {
                        var jsonArray = arguments[0];
                        if (id == jsonArray[elop.id]) {
                            if (jsonArray[elop.pid] != undefined) {
                                arry.push(jsonArray[elop.pid]);
                                return node(jsonArray[elop.pid], arry);
                            }
                        }
                    })(elop.data[i]);
                }
                return arry;
            };
            return node(id, []);
        },
        destroy : function() {
            $.removeData(this.element, "treeSelector");
            this.element.empty();
        }
    };

    // $.fn.treeSelector = function(option) {
    // var args = arguments, result = null;
    // $(this).each(function(index, item) {
    // var $this = $(item),
    // data = $this.data("treeSelector"),
    // options = (typeof option !== 'object') ? null : option;
    // if (!data) {
    // var domOptions = $this.attr("options");
    // if (domOptions) { // 如果页面控件设置了options参数，合并参数值
    // options = $.extend(options, $.parseJSON(domOptions.replace(/'/g, "\"")));
    // }
    // $this.data("treeSelector", (data = new TreeSelector(this, options)));
    // return;
    // }
    // if (typeof option === 'string') {
    // if (data[option]) {
    // result = data[option].apply(data, Array.prototype.slice.call(args, 1));
    // } else {
    // throw "Method " + option + " does not exist";
    // }
    // } else {
    // result = data.setOptions(option);
    // }
    // });
    // return result;
    // };

    var methods = {
        init : function(option) {
            var args = arguments, result = null;
            $(this)
                    .each(
                            function(index, item) {
                                var $this = $(item), data = $this.data("treeSelector"), options = (typeof option !== 'object') ? null
                                        : option;
                                if (!data) {
                                    var domOptions = $this.attr("options");
                                    if (domOptions) { // 如果页面控件设置了options参数，合并参数值
                                        options = $.extend(options, $.parseJSON(domOptions.replace(
                                                /'/g, "\"")));
                                    }
                                    $this.data("treeSelector", (data = new TreeSelector(this,
                                            options)));
                                    result = $.extend({
                                        "element" : data.element
                                    }, methods);
                                    return;
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
        loadColumn : function(val, caption) {
            var treeSel = $(this.element).data("treeSelector");
            $(this.element).find(".column-view-composition").remove();
            $(this.element).attr("data-id", val).attr("data-value", caption);
            treeSel.loadColumn(treeSel);
        }
    };

    $.fn.treeSelector = function(method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            return $.error("Method " + method + " does not exist on plug-in: treeSelector");
        }
    };

    $.fn.treeSelector.defaults = {
        rpc : { // 设置rpc请求参数
            className : "", // 类名
            methodName : "", // 方法名
            args : []
        // 参数组
        },
        btns : [ {
            icon : "icon-hand-up",
            tooltipTitles : null,
            click : null
        } ]
    };

    $.tnx.treeSelector = function(options) {
        var treeModel = new TreeSelector(null, options);
        treeModel.showModal(treeModel);
    };

    $.tnx.treeSelector = function(el, options) {
        var treeModel = new TreeSelector(el, options);
        treeModel.showModal(treeModel);
    };

})(jQuery);
