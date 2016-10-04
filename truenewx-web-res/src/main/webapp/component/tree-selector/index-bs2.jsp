<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Tree-selector：树选择器插件</title>
<link href="${context}/vendor/hcolumns/0.1.2/css/hcolumns.css" rel="stylesheet" type="text/css"/>
<link href="${context}/vendor/ztree/3.5.14/css/zTreeStyle.css" rel="stylesheet" type="text/css"/>

<script type="text/javascript" src="${context}/vendor/hcolumns/0.1.2/js/jquery.hcolumns.js" language="javascript"></script>
<script type="text/javascript" src="${context}/vendor/ztree/3.5.14/js/jquery.ztree.all.min.js" language="javascript"></script>
<script type="text/javascript" src="../multiselect-view/js/multiselect-view.js" language="javascript"></script>
<script type="text/javascript" src="js/tree-selector.js" language="javascript"></script>
<script type="text/javascript" language="javascript">
var nodes = [
  	{"id": '1', "name": "My Favorite Sites"},
  	{"id": '2', "name": "Empty Folder"},
  	{"id": '3', "name": "Direct link to Google"},
  	{"id": '11', "parentId" : "1", "name": "Tech1"},
  	{"id": '12', "parentId" : "1", "name": "Food2"},
  	{"id": '13', "parentId" : "1", "name": "Food3"},
  	{"id": '14', "parentId" : "1", "name": "Food4"},
  	{"id": '15', "parentId" : "1", "name": "Food5"},
  	{"id": '16', "parentId" : "1", "name": "Food6"},
  	{"id": '17', "parentId" : "1", "name": "Food7"},
  	{"id": '18', "parentId" : "1", "name": "Food8"},
  	{"id": '19', "parentId" : "1", "name": "Food9"},
  	{"id": '20', "parentId" : "1", "name": "Food10"},
  	{"id": '21', "parentId" : "1", "name": "Food11"},
  	{"id": '111', "parentId" : "11", "name": "PHP"},
  	{"id": '112', "parentId" : "11", "name": "Javascript"},
  	{"id": '113', "parentId" : "11", "name": "Hacker News"},
  	{"id": '1111', "parentId" : "111", "name": "PHP Engine"},
  	{"id": '1112', "parentId" : "111", "name": "PHP Extension"},
  	{"id": '1121', "parentId" : "112", "name": "node.js"}
];
var hcolumn;
$(function(){
    selectMenu(0, 3);
    /*$("#title").treeSelector({	//初始化树选择器控件
		type : "default",
	    id : "id",
	    pid : "parentId",
	    caption : "name",
	    title:"zTree Demo",
	    data : nodes
	});*/
  	//$.tnx.domain.site.init();
	/*$("#title2").treeSelector({
		data: nodes,
		onSure : function(id){
			alert(id);
		}
	});*/
	hcolumn = $("#hColum").treeSelector({
	    type : "column",
	    pid: "parentId",
        caption: "name",
	    data: nodes,
	    model: false,
	    entryClick : function(){
	        $.console.info($("#hColum .column .active"));
	    }
	});
	/*$("#title3").treeSelector({
		type : "default",
		pid: "parentId",
		caption: "name",
		multiSelect : true,
		data : nodes,
		btns : [{
		    tooltipTitles : "选择用户"
		}]
	});*/
});
function entryClick(){
    $.tnx.treeSelector({
		type: "column",
		pid: "parentId",
		caption: "name",
		data : nodes,
		onSure : function(id){
			alert(id);
		}
	});
}
function hcolumnClick(){
	hcolumn.loadColumn(13, "Food3");
}
</script>
</head>
<body>
	<div class="control-group form-horizontal">
		<label class="control-label" for="inputStation">测试树(zTree)</label>
		<div class="controls">
			<input type="hidden" id="title" name="title" value="" />
		</div>
	</div>
	<div class="control-group form-horizontal">
		<label class="control-label">测试树(hColunm)</label>
		<div class="controls">
            <input type="hidden" id="title2" name="title2" value=""
                options="{'type':'column','id':'id','pid':'parentId','caption':'name','title':'hColunm Demo'}"/>
        </div>
        <div class="controls">
        	<button onclick="hcolumnClick();">改变默认选中项</button>
        </div>
	</div>
    <div class="control-group form-horizontal">
        <label class="control-label" for="inputStation">测试非模态窗口展示</label>
        <div id="hColum" class="controls columns" data-id="14" data-value="Food4">
        </div>
    </div>
	<div class="control-group form-horizontal">
		<label class="control-label">测试多选树(zTree)</label>
		<div class="controls">
            <input type="text" id="title3" name="title2" value=""/>
        </div>
	</div>
	<div class="control-group form-horizontal">
		<label class="control-label">测试多选树(hColunm)</label>
		<div class="controls">
            <a href="javascript:;" onclick="entryClick()">点击弹出树选择器插件</a>
        </div>
	</div>
</body>
</html>