<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<script src="js/menu-style-selector.js" type="text/javascript" language="javascript"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>二级菜单选择</title>
<script type="text/javascript" language="javascript">
$(function(){
    selectMenu(0, 4);
    $("#dLabel1").menuStyleSelector({
        textField: "text",
        valueField: "value",
        subField: "level2",
        data: '[{"text":"子菜单1","value":"123","level2":[{"text":"二级菜单1","value":"2-1"},{"text":"二级菜单2","value":"2-2"}]},{"text":"子菜单2","value":"22233","level2":[{"text":"二级菜单3","value":"3-1"},{"text":"二级菜单4","value":"3-2"}]},{"text":"子菜单3","value":"22233","level2":[{"text":"二级菜单3","value":"3-1"},{"text":"二级菜单4","value":"3-2"}]}]'
    });
    
    $("#dLabel2").menuStyleSelector({
        textField: "text",
        valueField: "value",
        data: [{"text":"菜单1","value":"no1","pid":null},{"text":"子菜单1","value":"no1","pid":"no1"},{"text":"菜单2","value":"no2","pid":null}],
        clickMenuOptionCallBack:function(obj){
            alert(obj.text());
        }
    });
});
</script>
</head>
<body>
    <div class="dropdown" style="float: left;">
        <input  id="dLabel1" >
        </input>
    </div>
   
    <div class="dropdown" style="float: left;margin-left: 50px;">
        <button  id="dLabel2" > 菜单2<b class="caret"></b>
        </button>
    </div>
</body>
</html>