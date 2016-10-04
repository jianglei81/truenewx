function showProperties(spanObj) {
    var spanObj = $(spanObj);
    if (spanObj.attr("expandable") == "true") {
        var url = $.tnx.domain.site.path.context + "/rpc/properties";
        var className = spanObj.attr("title");
        var ifEnum = spanObj.attr("enum") == "true";
        var row = spanObj.parents(".arg-type .row-fluid");
        if (row.length) { // 参数
            if (spanObj.parent().is("td")) { // 参数类型的下级属性
                var nextTable = spanObj.next();
                if (nextTable.is("table")) { // 已有属性表格，则切换显示
                    nextTable.toggleClass("hidden");
                    return;
                }
            } else {
                var nextRow = row.next();
                if (nextRow.hasClass("row-fluid") && !nextRow.hasClass("arg-type")) { // 已有属性表格，则切换显示
                    nextRow.toggleClass("hidden");
                    return;
                }
            }
            // 没有构建属性表格则构建
            var td = row.parent();
            var tr = td.parent();
            var firstTd = $("td:first", tr);
            while (!firstTd.is("[rowspan]")) {
                var prev = firstTd.parent().prev();
                if (prev.length == 0) {
                    return;
                }
                firstTd = $("td:first", prev);
            }
            var beanId = $("p:first", firstTd).text();
            var methodTd = $("td:first", tr);
            if (methodTd.is("[rowspan]")) {
                methodTd = methodTd.next();
            }
            var methodName = $("span:first", methodTd).text();
            var argCount = $("td.arg-type .arg-type", tr).length;
            var argIndex = row.attr("index");
            url += "/arg/" + beanId + "/" + methodName + "/" + argCount + "/" + argIndex + ".json";
            buildPropertiesTable(url, ifEnum, function(table) {
                if (spanObj.parent().is("td")) { // 参数类型的下级属性
                    table.css({
                        "margin-top" : "8px",
                        "margin-bottom" : "0px"
                    });
                    spanObj.after(table);
                } else {
                    table.css({
                        "margin-bottom" : "8px"
                    });
                    var newRow = $("<div class=\"row-fluid\"></div>");
                    newRow.append(table);
                    row.after(newRow);
                }
            });
        } else { // 结果
            var td = spanObj.parent();
            var tableObj = $("table", td);
            if (tableObj.length) {
                tableObj.toggleClass("hidden");
            } else {
                var tr = spanObj.parents("td.result-type").parent();
                var firstTd = $("td:first", tr);
                while (!firstTd.is("[rowspan]")) {
                    var prev = firstTd.parent().prev();
                    if (prev.length == 0) {
                        return;
                    }
                    firstTd = $("td:first", prev);
                }
                var beanId = $("p:first", firstTd).text();
                var methodTd = $("td:first", tr);
                if (methodTd.is("[rowspan]")) {
                    methodTd = methodTd.next();
                }
                var methodName = $("span:first", methodTd).text();
                var argCount = $("td.arg-type .arg-type", tr).length;
                url += "/result/" + beanId + "/" + methodName + "/" + argCount + "/" + className
                        + ".json";
                buildPropertiesTable(url, ifEnum, function(table) {
                    table.css({
                        "margin-top" : "10px",
                        "margin-bottom" : "0px"
                    });
                    td.append(table);
                });
            }
        }
    }
}
function buildPropertiesTable(url, ifEnum, callback) {
    $.tnx.ajax(url, function(result) {
        if (result) {
            var metas = $.parseJSON(result);
            var table = $("<table class=\"table table-bordered table-condensed\"></table>");
            var tbody = $("<tbody></tbody>");
            $.each(metas, function(index, meta) {
                var tr = $("<tr><td></td><td></td><td></td></tr>");
                var typeTd = $("td:first", tr);
                if (ifEnum) {
                    var spanObj = $("<span></span>");
                    spanObj.addClass("muted");
                    spanObj.text("枚举常量");
                    typeTd.append(spanObj);
                } else {
                    appendTypeMeta(typeTd, meta.type);
                }
                var nameTd = $("td:eq(1)", tr);
                nameTd.text(meta.name);
                var captionTd = $("td:eq(2)", tr);
                captionTd.text(meta.caption);
                if (meta.deprecated) {
                    nameTd.addClass("deprecated");
                    captionTd.addClass("deprecated");
                }
                tbody.append(tr);
            });
            table.append(tbody);

            callback(table);
        }
    });
}
function appendTypeMeta(container, typeMeta) {
    var type = typeMeta.fullName;
    if (typeMeta.primitive) {
        container.text(type);
    } else {
        if (typeMeta.array) {
            type = type.substr(0, type.length - 2);
        }
        var spanObj = $("<span></span>");
        var index = type.lastIndexOf(".");
        if (index > 0) {
            if (!type.startsWith("java.")) {
                spanObj.attr("expandable", "true");
                spanObj.click(function() {
                    showProperties(spanObj);
                });
            }
            spanObj.attr("title", type);
            type = type.substr(index + 1);
        }
        if (typeMeta.enum) {
            spanObj.attr("enum", "true");
        }
        spanObj.text(type);
        container.append(spanObj);
        if (typeMeta.array) { // 数组
            container.append("[]");
        } else if (typeMeta.componentType) {
            var componentContainer = appendTypeMeta($("<span></span>"), typeMeta.componentType);
            var componentSpanObj = componentContainer.find("span[expandable='true']");
            if (componentSpanObj.length) { // 有可展开的span，则加入该span对象，因为该span上绑定了事件
                if (typeMeta.map) {
                    container.append("&lt;<span title=\"java.lang.String\">String</span>, ")
                            .append(componentSpanObj).append("&gt;");
                } else if (typeMeta.iterable) {
                    container.append("&lt;").append(componentSpanObj).append("&gt;");
                }
            } else { // 否则简单加入html字符串
                if (typeMeta.map) {
                    container.append("&lt;<span title=\"java.lang.String\">String</span>, "
                            + componentContainer.html() + "&gt;");
                } else if (typeMeta.iterable) {
                    container.append("&lt;" + componentContainer.html() + "&gt;");
                }
            }
        }
    }
    return container;
}
