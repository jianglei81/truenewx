<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<script src="js/level-menu.js" type="text/javascript" language="javascript"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>多级菜单</title>
<script type="text/javascript" language="javascript">
$(function() {
    selectMenu(5);
    
    $("#levelMenuContainer").levelMenu({
        hideOnClickOutside: false,
        parentNodeIcon : "icon-chevron-right",
        data : getData()
    });
});
function getData() {
    return [ {
        text : "/",
        selectable : false,
        id: 0,
        nodes : [ {
            text : "根目录1",
            id: 1,
            state : {
                expanded : true
            },
            nodes : [ {
                text : "目录11",
                id: 2,
            }, {
                text : "目录12",
                id: 3
            } ]
        }, {
            text : "根目录2",
            id: 4,
            nodes : [ {
                text : "目录21",
                id: 5
            }, {
                text : "目录22，这是个很长名字的节点",
                id: 6
            } ]
        }, {
            text : "重置菜单",
            id : 7,
            click : function(event) {
                var container = $("#levelMenuContainer");
                container.levelMenu("reset");
                container.levelMenu("showMenu", null);
            }
        } ]
    } ];
}
</script>
</head>
<body>
<!-- 要求菜单容器或父级包含有.row-fluid样式，否则菜单项宽度会超宽 -->
<div id="levelMenuContainer" class="row-fluid"></div>
</body>
</html>