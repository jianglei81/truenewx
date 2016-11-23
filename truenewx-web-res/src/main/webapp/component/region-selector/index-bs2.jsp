<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>地区选择器</title>
<script type="text/javascript" src="js/region-selector.js" language="javascript"></script>
<script type="text/javascript">
var region;
$(function(){
    selectMenu(6);

    region = $("#region").regionSelector({
        regionName : "region",
        nullLevel: 1
    });
    $("#region2").regionSelector({
        regionName : "region2",
        startLevel : 2
    });
});

function change(){
    region.change("");
}

function change1(){
    $("#region2").regionSelector("change", "CN110000");
}
</script>
</head>
<body>
    <form action="#" class="form-horizontal" method="post">
        <div class="control-group">
            <label class="control-label" for="regionDiv">所在地</label>
            <div class="controls">
                <input type="hidden" id="region" name="region" value="CN" />
                <span class="red">*</span>
                <button type="button" onclick="change()">更改地区</button>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="regionDiv">所在地2</label>
            <div class="controls">
                <input type="hidden" id="region2" name="region2" />
                <button type="button" onclick="change1()">更改地区</button>
            </div>
        </div>
    </form>
</body>
</html>
