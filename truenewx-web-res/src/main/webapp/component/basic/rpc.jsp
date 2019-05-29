<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>truenewx</title>
<script type="text/javascript" language="javascript">
function rpc(formObj) {
    var beanId = formObj.beanId.value;
    var rpc = $.tnx.rpc.imports(beanId);
    var methodName = formObj.methodName.value;
    var args = formObj.args.value.split(",");
    var result = rpc[methodName].apply(rpc, args);
    if (result != undefined) {
        $.tnx.alert(result);
    }
}
$(function() {
    selectMenu(1);
    $.tnx.setContext("${context}", "${context}");
});
</script>
</head>
<body>
<section id="rpc">
    <form class="form-inline">
        <p>
            <label>var rpc = $.tnx.rpc.imports (</label>
            <input type="text" name="beanId" class="input-large" placeholder="beanId" value="testController">
            <label>);</label>
        </p>
        <p>
            <label>rpc.</label>
            <input type="text" name="methodName" class="input-small" placeholder="methodName" value="append"> (
            <input type="text" name="args" class="input-large" placeholder="args" value='你好,123'> );
            <button type="button" class="btn" onclick="rpc(this.form);">invoke</button>
        </p>
    </form>
</section>
</body>
</html>
