<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tnx" uri="/truenewx-tags"%>
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
    function selectMenu(parentIndex, subIndex){
        var accordions = $(".accordion-body");
        accordions.removeClass("in");
        var accordion = accordions.eq(parentIndex);
        accordion.addClass("in");
        var lis = $("li", accordion);
        lis.removeClass("active");
        lis.eq(subIndex).addClass("active");
    }
</script>
<decorator:head/>
</head>

<body style="padding-top: 50px; position: relative;"<decorator:getProperty property="body.required-class" writeEntireProperty="true"/><decorator:getProperty property="body.component" writeEntireProperty="true"/><decorator:getProperty property="body.script" writeEntireProperty="true"/>>
<jsp:include page="/header">
    <jsp:param name="bsVersion" value="2"/>
</jsp:include>
<div class="container-fluid">
    <div class="row-fluid">
    <c:if test="${showMenu != false}">
        <div class="span2">
            <div class="accordion" id="accordion1">
                <div class="accordion-group">
                    <div class="accordion-heading">
                        <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion1" href="#collapseOne">技术类</a>
                    </div>
                    <div id="collapseOne" class="accordion-body collapse in">
                        <div class="accordion-inner">
                            <ul class="nav nav-list">
                                <li><a href="${context}/component/index">功能组件</a></li>
                                <li><a href="${context}/component/validate">字段校验</a></li>
                                <li><a href="${context}/rpc/api">RPC API</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
                <div class="accordion-group">
                    <div class="accordion-heading">
                        <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion1" href="#collapseTwo">业务类</a>
                    </div>
                    <div id="collapseTwo" class="accordion-body collapse">
                        <div class="accordion-inner">
                            <ul class="nav nav-list">
                                <li><a href="${context}/sms">短信</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="span10">
            <decorator:body />
        </div>
    </c:if><c:if test="${showMenu == false}">
        <div class="offset1 span10">
            <decorator:body />
        </div>
    </c:if>
    </div>
</div>
<jsp:include page="/footer.jsp"></jsp:include>
<script type="text/javascript">
    $(function() {
        selectNav(${showMenu == false ? 0 : 1});
    });
</script>
</body>
</html>
