<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="${context}/vendor/jquery/ui/1.12.0/css/jquery-ui.css" rel="stylesheet" type="text/css" />
<c:if test="${param.bsVersion == 2}">
<link href="${context}/vendor/bootstrap/2.3.2/css/bootstrap.css" rel="stylesheet" type="text/css" />
<link href="${context}/vendor/bootstrap/2.3.2/css/bootstrap-responsive.css" rel="stylesheet" type="text/css" />
</c:if><c:if test="${param.bsVersion == 3}">
<link href="${context}/vendor/bootstrap/3.3.5/css/bootstrap.css" rel="stylesheet" type="text/css" />
<link href="${context}/vendor/bootstrap/3.3.5/css/bootstrap-theme.css" rel="stylesheet" type="text/css" />
</c:if>
<link href="${context}/assets/css/site.css" rel="stylesheet" type="text/css" />
