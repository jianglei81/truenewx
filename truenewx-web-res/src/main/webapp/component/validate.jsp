<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/truenewx-tags" prefix="tnx"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>字段校验</title>
<script type="text/javascript" language="javascript">
$(function() {
    selectMenu(1);
});
</script>
</head>
<body required-class="required">
    <form id="validateForm" class="form-inline" method="post" validate="true">
        <div id="validateFormError" class="alert alert-error hide" style="margin-bottom: 5px;">
            <button type="button" class="close" data-dismiss="alert" tabindex="-1">&times;</button>&nbsp;
        </div>
        <input type="text" name="name" class="input" caption="名称" placeholder="名称" validation="${validation['name']}">
        <input type="text" name="md5" class="input-large" caption="MD5" placeholder="MD5" validation="${validation['md5']}">
        <input type="text" name="path" class="input-xlarge" caption="路径" placeholder="路径" validation="${validation['path']}">
        <input type="text" name="extension" class="input-small" caption="扩展名" placeholder="扩展名" validation="${validation['extension']}">
        <input type="text" name="capacity" class="input-small" caption="容量" placeholder="容量" validation="${validation['capacity']}">
        <button type="submit" class="btn">提交</button>
        <div id="nameError" class="alert alert-error hide" style="width:169px; margin-top: 5px;">
            <button type="button" class="close" data-dismiss="alert" tabindex="-1">&times;</button>&nbsp;
        </div>
    </form>
</body>
</html>
