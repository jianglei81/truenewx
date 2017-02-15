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
});
</script>
</head>

<body>
<tnx:region-view value="CN110101" delimiter=" "/>
</body>
</html>