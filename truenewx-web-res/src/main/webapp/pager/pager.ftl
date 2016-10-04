<div class="pagination ${align}">
	<ul style="box-shadow:none; margin-top:5px; margin-left:10px;">
  	<#if isCountable >
		<span style="float:left; display:block; padding: 5px;">共 <a>${total?c}</a> 条</span>
	</#if>
	<#if (pageSizeOptions?size !=1 && pageSizeOptions[0]!="") >
	    <span style="float:left; display:block; padding: 5px;">共 <a>${pageCount?c}</a> 页</span> 
		<span style="float:left; display:block; padding: 5px;">每页</span>
	    <select name="pageSize" id="pageSize" style="width:${40+pageSizeOptions[pageSizeOptions?size -1]?length*10}px; float:left; margin-right:5px;" onchange="$.tnx.pager.changePageSize(this.form, this.value);">
			<#list pageSizeOptions as option>
				<option value="${option}"<#if (pageSize?string = option)> selected="selected"</#if> >${option}</option>
			</#list>
	    </select>
	<#else>
		<input type="hidden" name="pageSize" id="pageSize"  value="${pageSize?c}"/>
	</#if>
      	<li<#if pageNo = 1> class="disabled"</#if>><a style="border:1px solid #dddddd; border-top-left-radius: 4px; border-bottom-left-radius: 4px;" href="javascript:" onclick="$.tnx.pager.toPage(this,${pageCount?c}, ${previousPage?c})" >
			<i class="icon-backward" style="opacity: .6; background-position: -241px -72px; width:7px;"></i></a></li>
    <#if (startPage > 1)>
	   	<li><a href="javascript:" onclick="$.tnx.pager.toPage(this,${pageCount?c}, 1)">1</a></li>
	</#if>
	<#if (startPage > 2)>
		<li><a onclick="$.tnx.pager.toPage(this,${pageCount?c}, ${(startPage-1)?c})" href="javascript:" style="font-weight:bold; cursor:default;">&hellip;</a></li>
	</#if>
	<#list startPage..endPage as i>
    	<li<#if pageNo = i> class="active"</#if>><a onclick="$.tnx.pager.toPage(this,${pageCount?c}, ${i?c})" href="javascript:">${i}</a></li>
    </#list>
    <#if (endPage  < pageCount-1 || !isCountable)>
    	<li><a onclick="$.tnx.pager.toPage(this,${pageCount?c}, ${(endPage+1)?c})" href="javascript:" style="font-weight:bold; cursor:default;">&hellip;</a></li>
	</#if>
	<#if (isCountable  && endPage < pageCount)>
        <li><a onclick="$.tnx.pager.toPage(this,${pageCount?c}, ${pageCount?c})" href="javascript:">${pageCount}</a></li>
    </#if>
    	<li <#if !isMorePage || !isCountable > class="disabled"</#if>><a onclick="$.tnx.pager.toPage(this,${pageCount?c}, ${nextPage?c})" href="javascript:"><i class="icon-forward" style="opacity: .6;background-position: -342px -72px; width:7px;"></i></a></li>
    <#if pageNoInputtable>
    	<li><input id="pageNo" value="${pageNo?c}" onkeydown="$.tnx.pager.pageNoKeydown(event,this,${pageCount?c});" onkeyup="$.tnx.pager.pageNoKeyup(this);" onfocus="this.select()"<#if (pageCount > 0)> maxlength="${pageCount?length}"</#if> 
    			type="text" class="input" style="border-left:0;-webkit-border-radius:0;-moz-border-radius: 0px;border-radius:0px; width:${15+pageCount?length}px; float:left;"></li>
    	<#if goText !="">
    		<span style="border:1px solid #dddddd; border-top-right-radius: 4px; border-bottom-right-radius: 4px; background-color: #fff; border-left:0; width:46px; height:28px; float:left;">
    			<a href="javascript:" onclick="$.tnx.pager.toPage(this,${pageCount?c})" style=" display:block; padding:4px; text-decoration:none; text-align:center;">${goText}</a></span>
    	</#if>
    <#else>
    	<input id="pageNo" 	type="hidden" />
    </#if>
    </ul>
</div>
