<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tnx" uri="/truenewx-tags"%>
<c:if test="${param.bsVersion == 2}">
<div class="navbar navbar-default navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container">
            <a data-target=".navbar-responsive-collapse" data-toggle="collapse" class="btn btn-navbar">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>
            <a href="${context}/" class="brand">truenewx</a>
            <div class="nav-collapse collapse navbar-responsive-collapse">
                <ul class="nav">
                    <li<c:if test="${param.navIndex == 0}"> class="active"</c:if>>
                        <a href="${context}/">首页</a></li>
                    <li<c:if test="${param.navIndex == 1}"> class="active"</c:if>>
                        <a href="${context}/component/basic">基础组件</a></li>
                    <li<c:if test="${param.navIndex == 2}"> class="active"</c:if>>
                        <a href="${context}/component/bs2">Bootstrap2组件</a></li>
                    <li<c:if test="${param.navIndex == 3}"> class="active"</c:if>>
                        <a href="${context}/component/bs3">Bootstrap3组件</a></li>
                    <li<c:if test="${param.navIndex == 4}"> class="active"</c:if>>
                        <a href="${context}/tool">工具</a></li>
                </ul>
            </div>
        </div>
    </div>
</div>
</c:if><c:if test="${param.bsVersion == 3}">
<nav class="navbar navbar-default navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse-1">
                <span class="sr-only">切换导航</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="${context}/">truenewx</a>
        </div>
        <div class="collapse navbar-collapse" id="navbar-collapse-1">
            <ul class="nav navbar-nav">
                <li<c:if test="${param.navIndex == 0}"> class="active"</c:if>>
                    <a href="${context}/">首页</a></li>
                <li<c:if test="${param.navIndex == 1}"> class="active"</c:if>>
                    <a href="${context}/component/basic">基础组件</a></li>
                <li<c:if test="${param.navIndex == 2}"> class="active"</c:if>>
                    <a href="${context}/component/bs2">Bootstrap2组件</a></li>
                <li<c:if test="${param.navIndex == 3}"> class="active"</c:if>>
                    <a href="${context}/component/bs3">Bootstrap3组件</a></li>
                <li<c:if test="${param.navIndex == 4}"> class="active"</c:if>>
                    <a href="${context}/tool">工具</a></li>
            </ul>
        </div>
    </div>
</nav>
</c:if>
<script type="text/javascript" language="javascript">
function selectNav(index) {
    var navs = $(".navbar .nav li");
    navs.removeClass("active");
    navs.eq(index).addClass("active");
}
</script>
