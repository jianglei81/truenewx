<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/truenewx-tags" prefix="tnx"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>基础组件</title>
</head>
<body>
<div class="hero-unit">
    <h1>基础组件</h1>
    <p>当前Locale：<tnx:json value="${pageContext.request.locale}" /></p>
</div>
</body>
</html>
