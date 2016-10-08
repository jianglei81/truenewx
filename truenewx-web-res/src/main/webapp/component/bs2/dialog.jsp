<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>对话框</title>
<script type="text/javascript" language="javascript">
function processing(formObj) {
    var timeout = $("#timeout", formObj).val();
    if (timeout != "") {
        timeout = Number(timeout);
    } else {
        timeout = undefined;
    }
    var processingObj = $.tnx.processing(timeout, $("#text", formObj).val(), $("#imageUrl", formObj).val());
    if (!timeout) {
        setTimeout(function() {
            processingObj.close();
        }, 30000);
    }
}
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
    selectMenu(1);
});
</script>
</head>
<body>
    <form class="form-inline">
        <label>$.tnx.</label>
        <button type="button" class="btn" onclick="processing(this.form)">processing</button> (
        <input type="text" id="timeout" class="input-mini" placeholder="timeout"> ,
        <input type="text" id="text" class="input-medium" placeholder="text"> ,
        <input type="text" id="imageUrl" class="input-xlarge" placeholder="imageUrl" value="${context}/assets/image/processing.gif"> );
    </form>
    <hr/>
    <form class="form-inline">
        <label>$.tnx.</label>
        <button type="button" class="btn" onclick="dialog(this.form)">dialog</button> (
        <input type="text" id="title" class="input" placeholder="title" value="标题"> ,
        <textarea id="content" placeholder="content" rows="3" class="span3"><button type="button" class="btn" onclick="$.tnx.processing()">processing</button>
        </textarea> ,
        <textarea id="buttons" placeholder="buttons" rows="3">{}</textarea> ,
        <textarea id="options" placeholder="options" rows="3">{}</textarea> );
    </form>
    <hr/>
    <form class="form-inline">
        <label>$.tnx.</label>
        <button type="button" class="btn" onclick="formAlert(this.form)">alert</button> (
        <textarea id="content" placeholder="content" rows="3" class="span3">内容</textarea> ,
        <input type="text" id="title" class="input" placeholder="title" value="标题"> );
    </form>
    <hr/>
    <form class="form-inline">
        <label>$.tnx.</label>
        <button type="button" class="btn" onclick="formConfirm(this.form)">confirm</button> (
        <textarea id="content" placeholder="content" rows="3" class="span3">内容</textarea> ,
        <input type="text" id="title" class="input" placeholder="title" value="标题"> );
    </form>
    <hr/>
    <form class="form-inline">
        <label>$.tnx.</label>
        <button type="button" class="btn" onclick="formFlash(this.form)">flash</button> (
        <textarea id="content" placeholder="content" rows="3" class="span3">内容</textarea> ,
        <input type="text" id="timeout" class="input" placeholder="timeout" value="2000"> );
        <button type="button" class="btn btn-primary hide">$.fn.flash</button>
    </form>
</body>
</html>
