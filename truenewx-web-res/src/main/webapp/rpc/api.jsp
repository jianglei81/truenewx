<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tnx" uri="/truenewx-tags"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<title>RPC API</title>
<link href="${context}/rpc/css/api.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" language="javascript">
$(function() {
    selectMenu(4);
    $.tnx.setContext("${context}", "${context}");
});
</script>
<script src="${context}/rpc/js/api.js" type="text/javascript" language="javascript"></script>
</head>
<body>
<table>
    <tr>
        <td class="hidden channel" id="template">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title text-center"></h3>
                </div>
            </div>
        </td>
    </tr>
</table>
</body>
</html>
