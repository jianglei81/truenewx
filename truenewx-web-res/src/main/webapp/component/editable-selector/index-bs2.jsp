<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="utf-8">
<title>可编辑的下拉菜单选择器</title>
<script src="js/editable-selector-bs2.js" type="text/javascript" language="javascript"></script>
<script type="text/javascript" language="javascript">
$(function() {
    selectMenu(0, 6);
    $("#a").editableSelector();
    $("#b").editableSelector();
});


</script>
<style type="text/css">
.dropdown-menu-right {
    left: auto;
    right: 0;
}
</style>
</head>
<body>
<div class="row" style="margin: 20px 0px;">
    <div class="col-md-2">
        <select id="a" class="form-control" id="selectorId" name="selectorName" style="color: blue;">
            <option value="1">选项1</option>
            <option value="2" selected="true">选项2</option>
            <option value="3">选项3</option>
            <option value="4">选项4</option>
            <option value="5">选项5</option>
            <option value="6">选项6</option>
            <option value="7">选项7</option>
            <option value="8">选项8</option>
            <option value="9">选项9</option>
        </select>
        
        <select id="b" class="form-control" id="selectorId" name="selectorName" style="color: blue;">
            <option value="1">选项1</option>
            <option value="2" selected="true">选项2</option>
            <option value="3">选项3</option>
            <option value="4">选项4</option>
            <option value="5">选项5</option>
            <option value="6">选项6</option>
            <option value="7">选项7</option>
            <option value="8">选项8</option>
            <option value="9">选项9</option>
        </select>
    </div>
</div>
</body>
</html>