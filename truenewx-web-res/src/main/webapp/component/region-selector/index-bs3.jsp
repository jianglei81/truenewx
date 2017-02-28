<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/truenewx-tags" prefix="tnx"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="utf-8">
<title>行政区划选择器</title>
<script src="js/region-selector.js" type="text/javascript" language="javascript"></script>
<script type="text/javascript" language="javascript">
$(function() {
    selectMenu(3);
    
    $("#region").regionSelector({
        selectClass:"form-control m-bot15",
        emptyText:"请选择",
        onChange:function(code){
        }
    });
});
</script>
</head>

<body>
<div class="alert alert-info">
    <tnx:region-view value="CN110101" delimiter=" "/>
</div>
<form class="form-horizontal" role="form" action="${context}/org/staff/add" method="post" validate="true">
    <div class="form-group">
        <label class="col-md-1 control-label" for="region">所在地</label>
        <div class="col-md-5">
            <input type="text" class="form-control" id="region" name="region" />
        </div>
    </div>
</form>
</body>
</html>