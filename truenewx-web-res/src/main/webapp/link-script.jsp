<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!-- 考虑开发便捷和便于调试，未做性能优化 -->
<script src="${context}/vendor/core/LAB-2.0.3.min.js" type="text/javascript" language="javascript"></script>
<script src="${context}/vendor/core/extend-1.0.0.js" type="text/javascript" language="javascript"></script>
<script src="${context}/vendor/core/sugar-1.4.1.min.js" type="text/javascript" language="javascript"></script>
<script src="${context}/vendor/jquery/3.1.0/jquery.js" type="text/javascript" language="javascript"></script>
<script src="${context}/vendor/jquery/plugins/jquery.json-2.4.0.min.js" type="text/javascript" language="javascript"></script>
<script src="${context}/vendor/jquery/ui/1.12.0/js/jquery-ui.js" type="text/javascript" language="javascript"></script>
<c:if test="${param.bsVersion == 2}">
<script src="${context}/vendor/bootstrap/2.3.2/js/bootstrap.js" type="text/javascript" language="javascript"></script>
</c:if><c:if test="${param.bsVersion == 3}">
<script src="${context}/vendor/bootstrap/3.3.5/js/bootstrap.js" type="text/javascript" language="javascript"></script>
</c:if>
<script src="${context}/vendor/echarts/3.2.2/echarts.min.js" type="text/javascript" language="javascript"></script>
<script src="${context}/component/core/prototype.js" type="text/javascript" language="javascript"></script>
<script src="${context}/component/core/truenewx.js" type="text/javascript" language="javascript"></script>
<script src="${context}/component/core/truenewx-bs${param.bsVersion}.js" type="text/javascript" language="javascript"></script>
<script src="${context}/component/core/truenewx-domain.js" type="text/javascript" language="javascript"></script>
<script src="${context}/component/core/truenewx-validate.js" type="text/javascript" language="javascript"></script>
<script type="text/javascript" language="javascript">
$(function(){
    $.tnx.pager.contextPath = "${context}";
    $.tnx.domain.site.path.context = "${context}";
    $.tnx.domain.site.init();
});
</script>
