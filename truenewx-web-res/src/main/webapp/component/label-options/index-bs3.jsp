<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>标签型选择器</title>
<link href="css/label-options.css" rel="stylesheet">
<script src="js/label-options.js" type="text/javascript"></script>
<script type="text/javascript" language="javascript">
    $(function() {

        selectMenu(4);

        var options = {
            textProperty : "caption",
            data : [ {
                value : 4,
                caption : "选项4"
            }, {
                value : 5,
                caption : "选项5"
            }, {
                caption : "无值选项"
            } ]
        };
        $("#spanContainer").labelOptions(options);
        options.theme = "red";
        $("#divContainer").labelOptions(options);

    });

    function showSpanSelected() {
        var data = $("#spanContainer").labelOptions("getSelectedData");
        var message = "";
        for (var i = 0; i < data.length; i++) {
            message += data[i].caption + ",";
        }
        if (message.length) {
            $.tnx.alert(message.substr(0, message.length - 1));
        }
    }

    function showDivSelected() {
        var data = $("#divContainer").labelOptions("getSelectedData");
        var message = "";
        for (var i = 0; i < data.length; i++) {
            message += data[i].caption + ",";
        }
        if (message.length) {
            $.tnx.alert(message.substr(0, message.length - 1));
        }
    }
</script>
</head>
<body>
<div class="row">
    <div class="col-md-6" id="spanContainer">
        <span data-value="1">选项1</span>
        <span data-value="2">选项2</span>
        <span data-value="3">选项3</span>
    </div>
</div>
<div class="row">
    <button type="button" class="btn btn-default" onclick="showSpanSelected()">选中的值</button>
</div>
<hr>
<div class="row">
    <div class="col-md-6" id="divContainer">
        <div data-value="1">选项1</div>
        <div data-value="2">选项2</div>
        <div data-value="3">选项3</div>
    </div>
</div>
<div class="row">
    <button type="button" class="btn btn-default" onclick="showDivSelected()">选中的值</button>
</div>
</body>
</html>