<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge">
<title>truenewx - <decorator:title /></title>
<jsp:include page="/link-css.jsp">
    <jsp:param name="bsVersion" value="3"/>
</jsp:include>
<jsp:include page="/link-script.jsp">
    <jsp:param name="bsVersion" value="3"/>
</jsp:include>
<script type="text/javascript">
function selectMenu(index){
    $(".list-group-item:eq(" + index + ")").addClass("active");
}
</script>
<decorator:head/>
</head>

<body style="padding-top: 65px; position: relative;"<decorator:getProperty property="body.required-class" writeEntireProperty="true"/><decorator:getProperty property="body.component" writeEntireProperty="true"/><decorator:getProperty property="body.script" writeEntireProperty="true"/>>
<jsp:include page="/header">
    <jsp:param name="bsVersion" value="3"/>
    <jsp:param name="navIndex" value="4"/>
</jsp:include>
<div class="container">
    <div class="col-md-2">
        <div class="panel panel-default">
            <div class="list-group" style="margin-bottom: 0px;">
                <a class="list-group-item" href="${context}/tool/region">行政区划数据调整</a>
            </div>
        </div>
    </div>
    <div class="col-md-10">
        <decorator:body />
    </div>
</div>
<jsp:include page="/footer.jsp"></jsp:include>
</body>
</html>
