/**
 * RPC API v2.0.0
 *
 * Depends on: jquery.js, truenewx.js
 */
$(function() {
    $.tnx.rpc.api = $.tnx.rpc.api || {};
    $.extend($.tnx.rpc.api, {
        onLoad : function() {
            this.showModulesPanel();
        },
        buildPanel : function(title, anchor) {
            var td = $("#template").clone();
            td.removeAttr("id").removeClass("hidden");
            $("#template").parent().append(td);
            $(".panel-title", td).text(title);
            if (anchor) {
                var top = anchor.offset().top;
                top -= $(".panel-heading", td).outerHeight();
                td.css("padding-top", top + "px");
            }
            return $(".panel", td);
        },
        showModulesPanel : function() {
            var _this = this;
            var url = $.tnx.siteContext + "/rpc/api/modules";
            $.tnx.ajax(url, function(result) {
                var panel = _this.buildPanel("模块");
                var ul = $("<ul></ul>").addClass("list-group");
                panel.append(ul);

                var modules = $.parseJSON(result);
                $.each(modules, function(i, module) {
                    var li = $("<li></li>").addClass("list-group-item clickable");
                    if (module.startsWith(" ")) {
                        li.text("<" + module.trim() + ">");
                    } else {
                        li.text(module);
                    }
                    ul.append(li);
                    li.click(function() {
                        if (li.is(".active")) { // 本就是激活状态，则点击可以取消激活
                            li.removeClass("active");
                            _this.removePanels(1);
                        } else {
                            $("li.active", li.parent()).removeClass("active");
                            li.addClass("active");
                            _this.showBeansPanel(module, li);
                        }
                    });
                });
            });
        },
        removePanels : function(minLevel) {
            var tr = $("#template").parent();
            $(".channel:gt(" + minLevel + ")", tr).remove();
        },
        showBeansPanel : function(module, anchor) {
            var _this = this;
            _this.removePanels(1);
            var url = $.tnx.siteContext + "/rpc/api/beans";
            $.tnx.ajax(url, {
                module : module
            }, function(result) {
                var panel = _this.buildPanel("beanId", anchor);
                var ul = $("<ul></ul>").addClass("list-group");
                panel.append(ul);

                var beans = $.parseJSON(result);
                $.each(beans, function(i, bean) {
                    var li = $("<li></li>").addClass("list-group-item clickable");
                    var html;
                    if (bean.deprecated) {
                        html = "<span class='bean-id deprecated'>" + bean.beanId + "</span>";
                    } else {
                        html = "<span class='bean-id'>" + bean.beanId + "</span>";
                    }
                    if (bean.caption) {
                        html += "<span class='caption'>" + bean.caption + "</span>";
                    }
                    li.html(html);
                    ul.append(li);
                    li.click(function() {
                        if (li.is(".active")) { // 本就是激活状态，则点击可以取消激活
                            li.removeClass("active");
                            _this.removePanels(2);
                        } else {
                            $("li.active", li.parent()).removeClass("active");
                            li.addClass("active");
                            _this.showMethodsPanel(bean.beanId, li);
                        }
                    });
                });
            });
        },
        showMethodsPanel : function(beanId, anchor) {
            var _this = this;
            _this.removePanels(2);
            var url = $.tnx.siteContext + "/rpc/api/" + beanId + "/methods";
            $.tnx.ajax(url, function(result) {
                var panel = _this.buildPanel("方法", anchor);
                var ul = $("<ul></ul>").addClass("list-group");
                panel.append(ul);

                var methods = $.parseJSON(result);
                $.each(methods, function(i, method) {
                    var li = $("<li></li>").addClass("list-group-item clickable");
                    var html = "<span class='method-name'>" + method.name + "</span>"
                            + "(<span class='arg-count'>" + method.argCount + "</span>)";
                    if (method.deprecated) {
                        html = "<span class='deprecated'>" + html + "</span>";
                    }
                    if (method.caption) {
                        html += "<span class='caption'>" + method.caption + "</span>";
                    }
                    li.html(html);
                    ul.append(li);
                    li.click(function() {
                        if (li.is(".active")) { // 本就是激活状态，则点击可以取消激活
                            li.removeClass("active");
                            _this.removePanels(3);
                        } else {
                            $("li.active", li.parent()).removeClass("active");
                            li.addClass("active");
                            _this.showMethodPanel(beanId, method.name, method.argCount, li);
                        }
                    });
                });
            });
        },
        showMethodPanel : function(beanId, methodName, argCount, anchor) {
            var _this = this;
            _this.removePanels(3);
            var url = $.tnx.siteContext + "/rpc/api/" + beanId + "/" + methodName + "/" + argCount;
            $.tnx.ajax(url, function(result) {
                var panel = _this.buildPanel("方法详情", anchor);
                var icon = _this.buildIcon("test", "测试");
                $(".panel-title", panel).append(icon);
                var body = $("<div></div>").addClass("panel-body");
                panel.append(body);

                var method = $.parseJSON(result);
                body.append(method.name);
                if (method.deprecated) {
                    body.append("<span class='label label-default' title='不推荐再使用'>过期</span>");
                }
                if (method.anonymous) {
                    body.append("<span class='label label-danger' title='可匿名访问'>匿名</span>");
                }
                if (method.lan) {
                    body.append("<span class='label label-warning' title='仅局域网内部可访问'>LAN</span>");
                }

                var table = $("<table></table>").addClass("table table-bordered");
                table.append("<tr class='arg'><td nowrap='nowrap'>参数</td></tr>");
                table.append("<tr class='result'><td nowrap='nowrap'>结果</td></tr>");
                panel.append(table);
                var tr = $("tr:first", table);
                var argCount = method.argMetas.length;
                if (argCount == 0) {
                    tr.append("<td class='text-muted text-center' colspan='2'>无</td>");
                } else {
                    $("td:first", tr).attr("rowspan", argCount);
                    $.each(method.argMetas, function(i, arg) {
                        if (i > 0) {
                            tr.after("<tr class='arg'></tr>");
                            tr = tr.next();
                        }
                        tr.append("<td class='argType' nowrap='nowrap'></td>");
                        tr.append("<td class='caption' nowrap='nowrap'></td>");
                        $(".argType", tr).append(_this.buildTypeLink(tr, arg.type, 4, i));
                        $(".caption", tr).text(arg.caption);
                    });
                }
                tr = $("tr:last", table);
                var resultType = _this.buildTypeLink(tr, method.resultType, 4); // 结果类型不指定参数索引下标
                if (resultType) {
                    tr.append("<td class='resultType' nowrap='nowrap'></td>");
                    tr.append("<td class='caption' nowrap='nowrap'></td>");
                    $(".resultType", tr).append(resultType);
                    $(".caption", tr).text(method.resultType.caption);
                } else {
                    tr.append("<td class='text-muted text-center' colspan='2'>无</td>");
                }
            });
        },
        buildIcon : function(type, title) {
            var icon = $("<span class='clickable pull-right'></span>");
            var iconClass = $("[icon-" + type + "]").attr("icon-" + type);
            if (iconClass) {
                icon.addClass(iconClass).attr("title", title);
            } else {
                icon.text(title);
            }
            return icon;
        },
        buildTypeLink : function(tr, type, level, argIndex) {
            if (type && type.simpleName) {
                var html = type.simpleName;
                if (type.complex) { // 复合类型
                    var clickable;
                    if (type.array) { // 复合类型数组
                        html = $("<span><span class='type clickable'>"
                                + html.substr(0, html.length - 2) + "</span>[]</span>");
                        clickable = $(".clickable", html);
                    } else {
                        html = $("<span class='type clickable'>" + html + "</span>");
                        clickable = html;
                    }
                    var _this = this;
                    clickable.click(function() {
                        var obj = $(this);
                        if (obj.is(".clickable")) {
                            $(".label-primary", tr.parent()).removeClass("label label-primary")
                                    .addClass("clickable");
                            obj.removeClass("clickable").addClass("label label-primary");
                            _this.showTypePanel(type, level, tr, argIndex);
                        } else {
                            obj.removeClass("label label-primary").addClass("clickable");
                            _this.removePanels(level);
                        }
                    });
                }
                if (type.iterable) { // 集合类型
                    html = $("<span></span>").append(html);
                    html.append("&lt;");
                    html.append(this.buildTypeLink(tr, type.componentType, level, argIndex));
                    html.append("&gt;");
                } else if (type.map) { // Map类型
                    html = $("<span></span>").append(html);
                    html.append("&lt;String, ");
                    html.append(this.buildTypeLink(tr, type.componentType, level, argIndex));
                    html.append("&gt;");
                }
                return html;
            }
            return null;
        },
        showTypePanel : function(type, level, anchor, argIndex) {
            var _this = this;
            _this.removePanels(level);

            var anchorTable = anchor.parents("table");
            var beanId = $(".channel:eq(2) .active .bean-id").text();
            var methodName = $(".channel:eq(3) .active .method-name").text();
            var argCount = $(".channel:eq(3) .active .arg-count").text();
            var url;
            if (argIndex != undefined) { // 参数类型
                var argType = argIndex;
                if (!anchor.is(".arg")) { // 锚点不是参数行，说明当前类型为参数类型的下级类型
                    argType = type.type;
                }
                url = $.tnx.siteContext + "/rpc/api/" + beanId + "/" + methodName + "/" + argCount
                        + "/arg/" + argType;
            } else { // 结果类型
                var className = type.type;
                url = $.tnx.siteContext + "/rpc/api/" + beanId + "/" + methodName + "/" + argCount
                        + "/result/" + className;
            }
            $.tnx.ajax(url + "/properties", function(result) {
                var panel = _this.buildPanel(type.simpleName, anchor);
                var icon = _this.buildIcon("code", "代码");
                $(".panel-title", panel).append(icon);
                icon.click(function() {
                    var url = $.tnx.context + "/rpc/code.win";
                    $.tnx.open(url, [ {
                        "class" : "btn btn-default",
                        text : "关闭",
                        click : function() {
                            this.close();
                        }
                    } ], {
                        title : "模型代码",
                        events : {
                            shown : function() {
                                var language = $("[language]").attr("language");
                                if (language) {
                                    $("li:not(.active) [data-toggle][href='#" + language + "']",
                                            this).trigger("click");
                                }
                                _this.generateCodes(this, url + "/codes");
                            }
                        }
                    });
                });

                result = $.parseJSON(result);

                var body = $("<div></div>").addClass("panel-body");
                body.append(result.caption);
                if (type["enum"]) {
                    body.append("<span class='label label-success' title='枚举类型'>枚举</span>");
                }
                if (body.html()) {
                    panel.append(body);
                }
                var table = $("<table></table>").addClass("table table-bordered");
                panel.append(table);

                $.each(result.properties, function(i, property) {
                    var tr = $("<tr></tr>");
                    table.append(tr);
                    var td = $("<td></td>");
                    if (!type["enum"]) {
                        td.append(_this.buildTypeLink(tr, property.type, level + 1, argIndex));
                        tr.append(td);
                        td = $("<td></td>");
                    }
                    td.append(property.name);
                    tr.append(td);
                    td = $("<td nowrap='nowrap'></td>");
                    td.append(property.caption);
                    tr.append(td);
                });
                _this.scrollRightest(panel);
            });
        },
        scrollRightest : function(panel) {
            var left = panel.offset().left;
            left += panel.outerWidth();
            $(document).scrollLeft(left);
        },
        generateCodes : function(dialog, url) {

        }
    });
    $.tnx.rpc.api.onLoad();
});
