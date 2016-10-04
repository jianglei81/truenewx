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
    function selectMenu(parentIndex, subIndex){
        var panels = $(".panel-collapse");
        panels.removeClass("in");
        var panel = panels.eq(parentIndex);
        panel.addClass("in");
        var as = $("a", panel);
        as.removeClass("active");
        as.eq(subIndex).addClass("active");
    }
</script>
<decorator:head/>
</head>

<body style="padding-top: 60px; position: relative;"<decorator:getProperty property="body.required-class" writeEntireProperty="true"/><decorator:getProperty property="body.component" writeEntireProperty="true"/><decorator:getProperty property="body.script" writeEntireProperty="true"/>>
<jsp:include page="/header">
    <jsp:param name="bsVersion" value="3"/>
</jsp:include>
<div class="container-fluid">
    <div class="col-md-2">
        <div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h4 class="panel-title">
                        <a data-toggle="collapse" data-parent="#accordion" href="#collapseOne" 
                            aria-expanded="true" aria-controls="collapseOne">技术类</a>
                    </h4>
                </div>
                <div id="collapseOne" class="panel-collapse collapse in" role="tabpanel">
                    <div class="list-group" style="margin-bottom: 0px;">
                        <a class="list-group-item" href="${context}/component/bs3/dialog">对话框类</a>
                        <a class="list-group-item" href="${context}/component/editable-selector/index-bs3">可编辑选择器</a>
                        <a class="list-group-item" href="${context}/component/level-menu/index-bs3">多级菜单</a>
                    </div>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h4 class="panel-title">
                        <a data-toggle="collapse" data-parent="#accordion" href="#collapseTwo" 
                            aria-expanded="false" aria-controls="collapseTwo">业务类</a>
                    </h4>
                </div>
                <div id="collapseTwo" class="panel-collapse collapse" role="tabpanel">
                    <div class="list-group" style="margin-bottom: 0px;">&nbsp;
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-10">
        <decorator:body />
    </div>
</div>
<jsp:include page="/footer.jsp"></jsp:include>
<script type="text/javascript">
    $(function() {
        selectNav(3);
    });
</script>
</body>
</html>
