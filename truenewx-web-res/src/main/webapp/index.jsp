<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge">
<title>truenewx - 首页</title>
<jsp:include page="/link-css.jsp">
    <jsp:param name="bsVersion" value="3"/>
</jsp:include>
</head>

<body style="padding-top: 65px; position: relative;">
<jsp:include page="/header">
    <jsp:param name="bsVersion" value="3"/>
    <jsp:param name="navIndex" value="0"/>
</jsp:include>
<div class="container">
    <div class="jumbotron">
        <h1>truenewx</h1>
        <p>客户端组件示范站点，查看框架源代码请访问：
            <a href="https://github.com/jiangnanyuzi/truenewx" target="_blank">https://github.com/jiangnanyuzi/truenewx</a>
        </p>
    </div>
</div>
<jsp:include page="/footer.jsp"></jsp:include>
</body>
</html>
