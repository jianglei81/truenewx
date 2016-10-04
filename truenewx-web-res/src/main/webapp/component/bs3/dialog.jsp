<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="utf-8">
<title>对话框</title>
<script type="text/javascript" language="javascript">
function dialog(formObj) {
    var buttons = $.parseJSON($("#buttons", formObj).val());
    var options = $.parseJSON($("#options", formObj).val());
    $.tnx.dialog($("#title", formObj).val(), $("#content", formObj).val(), buttons, options);
}
function formAlert(formObj) {
    $.tnx.alert($("#content", formObj).val(), $("#title", formObj).val(), function(){
        $.console.info("alert callback");
    });
}
function formConfirm(formObj) {
    $.tnx.confirm($("#content", formObj).val(), function(yes) {
        $.console.info("confirm callback: " + yes);
    }, {
        title : $("#title", formObj).val()
    });
}
function formFlash(formObj) {
    var value = $("#timeout", formObj).val();
    var timeout = undefined;
    if (value) {
        timeout = Number(value);
        if (isNaN(timeout)) {
            timeout = undefined;
        }
    }
    $.tnx.flash($("#content", formObj).val(), timeout, function(){
        $(":hidden", formObj).flash(undefined, function(){
            $.console.info("$.fn.flash callback");
        });
    });
}
$(function() {
    selectMenu(0, 0);
});
</script>
</head>
<body>
    <form class="form-inline" role="form">
        <div class="form-group">
            <label>$.tnx.</label>
            <button type="button" class="btn btn-default" onclick="dialog(this.form)">dialog</button> (
            <input class="form-control" type="text" id="title" placeholder="title" value="标题"> ,
            <textarea class="form-control" id="content" placeholder="content" rows="3" class="span3"><button type="button" class="btn btn-default" onclick="$.tnx.processing()">processing</button>
            </textarea> ,
            <textarea class="form-control" id="buttons" placeholder="buttons" rows="3">{}</textarea> ,
            <textarea class="form-control" id="options" placeholder="options" rows="3">{}</textarea> );
        </div>
    </form>
    <hr/>
    <form class="form-inline">
        <div class="form-group">
        <label>$.tnx.</label>
            <button type="button" class="btn btn-default" onclick="formAlert(this.form)">alert</button> (
            <textarea class="form-control" id="content" placeholder="content" rows="3" class="span3">内容</textarea> ,
            <input type="text" id="title" class="input" placeholder="title" value="标题"> );
        </div>
    </form>
    <hr/>
    <form class="form-inline">
        <label>$.tnx.</label>
        <button type="button" class="btn btn-default" onclick="formConfirm(this.form)">confirm</button> (
        <textarea class="form-control" id="content" placeholder="content" rows="3" class="span3">内容</textarea> ,
        <input type="text" id="title" class="input" placeholder="title" value="标题"> );
    </form>
    <hr/>
    <form class="form-inline">
        <label>$.tnx.</label>
        <button type="button" class="btn btn-default" onclick="formFlash(this.form)">flash</button> (
        <textarea class="form-control" id="content" placeholder="content" rows="3" class="span3">内容</textarea> ,
        <input type="text" id="timeout" class="input" placeholder="timeout" value="2000"> );
        <button type="button" class="btn btn-primary hide">$.fn.flash</button>
    </form>
</body>
</html>
