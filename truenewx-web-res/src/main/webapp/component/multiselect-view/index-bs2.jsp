<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>multiselect-view：多选展示插件</title>
<script src="${context}/component/multiselect-view/js/multiselect-view.js" type="text/javascript" language="javascript"></script>
<script type="text/javascript" language="javascript">
var multi;
var arry = [
	{"id":"1", "caption":"超级管理员超级管理员超级管理员超级管理员超级管理员超级管理员超级管理员超级管理员超级管理员"},
	{"id":"2", "caption":"网站管理员"},
	{"id":"3", "caption":"角色管理员角色管理员角色管理员角色管理员角色管理员角色管理员角色管理员角色管理员角色管理员"},
	{"id":"4", "caption":"商城管理员"},
	{"id":"5", "caption":"商家用户"},
	{"id":"6", "caption":"普通用户"}
];
var arry1 = [
	{"caption":"超级管理员"},
	{"caption":"网站管理员"},
	{"caption":"角色管理员"}
];
$(function(){
    selectMenu(2);
    //$.tnx.domain.site.init();
    
    multi = $("#users").multiselectView({
        title: "标题",
        vertical : true,
        sortable : true,
        minColumn: 2,
        maxColumn: 5,
        data: arry
    });
});
function multiSelect(){
	$("#users").multiselectView("data", arry);
	//multi.data(arry);
}
function addItem(){
	var arry = [{"id":"7", "caption":"VIP用户"}];
	$("#users").multiselectView("addData", arry);
}
function getData(){
	console.info(multi.getData());
}
function subClick(){
	//var value = multi.values();
	//var value2 = $("#users").multiselectView("values");
    //alert(value);
    alert(multi.getCaption());
    //alert(value2);
}
function btnClick(){
    //$("#users").multiselectView("empty");	//清空当前所有选择的值
    multi.empty();
}
function add(){
    multi.add($("#caption").val());
}
</script>
</head>
<body>
	<div class="control-group">
        <div class="controls" align="left">
        	<input type="text" id="caption"/>
            <button type="button" class="btn btn-primary" onclick="add();">新增</button>
        </div>
    </div>
	<input type="hidden" id="users" name="users" value=""/>
	<div class="btn-toolbar">
		<div class="btn-group">
			<button onclick="getData()" class="btn btn-primary">获取值(key-value)</button>
			<button onclick="multiSelect()" class="btn btn-primary">二次调用multiSelect</button>
			<button onclick="subClick()" class="btn btn-primary">提交</button>
			<button onclick="btnClick()" class="btn">清空</button>
		</div>
	</div>
</body>
</html>
