<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/truenewx-tags" prefix="tnx"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>RPC API</title>
<link href="${context}/rpc/css/api.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" language="javascript">
$(function() {
    selectMenu(0, 2);
});
</script>
<script src="${context}/rpc/js/api.js" type="text/javascript" language="javascript"></script>
</head>
<body>
<c:if test="${controllerMap.size() > 0}">
    <ul class="nav nav-tabs" style="margin-bottom: 0px;">
    <c:forEach var="entry" items="${controllerMap}" varStatus="moduleStatus">
        <li<c:if test="${moduleStatus.index==0}"> class="active"</c:if>>
            <a href="#${entry.key.replaceAll(' ','_')}" data-toggle="tab">
                <c:if test="${entry.key.startsWith(' ')}">&lt;</c:if>${entry.key.trim()}<c:if test="${entry.key.startsWith(' ')}">&gt;</c:if>
            </a>
        </li>
    </c:forEach>
    </ul>
    <div class="tab-content">
    <c:forEach var="entry" items="${controllerMap}" varStatus="moduleStatus">
        <div class="tab-pane<c:if test="${moduleStatus.index==0}"> active</c:if>" id="${entry.key.replaceAll(' ','_')}">
            <table class="table table-bordered table-hover" style="margin-bottom: 0px;">
                <thead>
                    <tr>
                        <th>beanId</th>
                        <th>方法</th>
                        <th>参数</th>
                        <th>返回结果</th>
                        <th>限制</th>
                    </tr>
                </thead>
                <tbody>
            <c:forEach var="controller" items="${entry.value}" varStatus="controllerStatus">
                <c:forEach var="method" items="${controller.methodMetas}" varStatus="methodStatus">
                    <tr>
                    <c:if test="${methodStatus.index==0}">
                        <td rowspan="${controller.methodMetas.size()}">
                            <p<c:if test="${controller.deprecated}"> class="deprecated"</c:if>>${controller.beanId}</p>
                        <c:if test="${not empty controller.caption}">
                            <p class="muted">(${controller.caption})</p>
                        </c:if>
                        </td>
                    </c:if>
                        <td>
                            <span<c:if test="${controller.deprecated || method.deprecated}"> class="deprecated"</c:if>>${method.name}</span>
                        <c:if test="${not empty method.caption}">
                            <span class="muted">(${method.caption})</span>
                        </c:if>
                        </td>
                        <td class="arg-type">
                        <c:forEach var="arg" items="${method.argMetas}" varStatus="argStatus">
                            <div class="row-fluid arg-type" index="${argStatus.index}">
                                <div class="span3">
                                    <tnx:rpc-type value="${arg.type}" onclick="showProperties(this)"/>
                                </div>
                                <div class="span5">${arg.name}</div>
                                <div class="span4 muted">${arg.caption}</div>
                            </div>
                        </c:forEach>
                        <c:if test="${method.argMetas.size() == 0}">
                            <span class="muted">&lt;无&gt;</span>
                        </c:if>
                        </td>
                        <td class="result-type">
                            <tnx:rpc-type value="${method.returnType}" onclick="showProperties(this)"
                                emptyHtml="<span class='muted'>&lt;无&gt;</span>"/>
                        <c:if test="${not empty method.returnType.caption}">
                            <span class="muted">(${method.returnType.caption})</span>
                        </c:if>
                        </td>
                        <td>
                        <c:if test="${method.logined}">
                            <span class="label label-important" title="登录后才可访问" render="tooltip">登录</span>
                        </c:if>
                        <c:if test="${method.lan}">
                            <span class="label" title="仅局域网内部可访问" render="tooltip">LAN</span>
                        </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </c:forEach>
                </tbody>
            </table>
        </div>
    </c:forEach>
    </div>
</c:if>
</body>
</html>
