<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>progress：圆形进度条插件</title>
<style>
    .percentBox{ width:80px; height:80px; position:relative}
    .pertxt{width:80px;height:80px;line-height:80px;position:absolute;margin-top:-82px;text-align:center;color:#343a41;font-size:22px;font-family:Arial;}
</style>
<script type="text/javascript" src="js/raphael-min.js" language="javascript"></script>
<script type="text/javascript" src="js/progress.js" language="javascript"></script>
<script type="text/javascript" language="javascript">
$(function(){
    selectMenu(0, 5);
    
    $("#divProgress").progress({
        percent : 0.2,
        pertxtClass : "pertxt"
    });
});
function changePercent() {
    $("#divProgress").progress({
        percent : $("#txtPercent").val() || 0,
        color : $("#txtColor").val() || "#33ff00",
        pertxtClass : "pertxt"
    });
}
</script>
</head>
<body>
    <div id="divProgress" class="percentBox"></div>
	<input type="text" id="txtPercent" value="0.2" />
    <button onclick="changePercent()">改变进度</button>
    <br />
    <input type="text" id="txtColor" value="#33ff00" />
    <button onclick="changePercent()">改变颜色</button>
</body>
</html>