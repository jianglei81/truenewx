<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>truenewx - <decorator:title /></title>
<jsp:include page="/link-css.jsp">
    <jsp:param name="bsVersion" value="2"/>
</jsp:include>
<jsp:include page="/link-script.jsp">
    <jsp:param name="bsVersion" value="2"/>
</jsp:include>
<script type="text/javascript">
function selectMenu(index){
    $(".nav-list li:eq(" + index + ")").addClass("active");
}
</script>
<decorator:head/>
</head>

<body style="padding-top: 55px; position: relative;"<decorator:getProperty property="body.required-class" writeEntireProperty="true"/><decorator:getProperty property="body.component" writeEntireProperty="true"/><decorator:getProperty property="body.script" writeEntireProperty="true"/>>
<jsp:include page="/header">
    <jsp:param name="bsVersion" value="2"/>
</jsp:include>
<div class="container">
    <div class="row">
        <div class="span2">
            <ul class="nav nav-list">
                <li><a href="${context}/component/bs2/simple">简单组件</a></li>
                <li><a href="${context}/component/bs2/dialog">对话框类</a></li>
                <li><a href="${context}/component/multiselect-view/index-bs2">多选显示组件</a></li>
                <li><a href="${context}/component/progress/index-bs2">圆形进度条</a></li>
                <li><a href="${context}/component/editable-selector/index-bs2">可编辑选择器</a></li>
                <li><a href="${context}/component/level-menu/index-bs2">多级菜单</a></li>
                <li><a href="${context}/component/region-selector/index-bs2">地区选择器</a></li>
                <!-- <li><a href="${context}/component/tree-selector/index-bs2">树形选择组件</a></li> -->
                <!-- <li><a href="${context}/component/menu-style-selector/index-bs2">二级菜单选择</a></li> -->
            </ul>
        </div>
        <div class="span10">
            <decorator:body />
        </div>
    </div>
</div>
<jsp:include page="/footer.jsp"></jsp:include>
<script type="text/javascript">
    $(function() {
        selectNav(2);
    });
</script>
</body>
</html>
