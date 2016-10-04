<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>简单组件</title>
<script type="text/javascript" src="${context}/component/input-limiter/js/input-limiter.js" language="javascript"></script>
<script type="text/javascript" language="javascript">
$(function() {
    selectMenu(0, 0);
});

function applyPopover(form) {
    var title = $("[name='title']", form).val();
    var content = $("[name='content']", form).val();
    var closeable = $("[name='closeable']", form).is(":checked");
    var buttons = $("button[options]", form);
    buttons.each(function(index, button) {
        button = $(button);
        var options = $.parseJSON(button.attr("options"), true);
        $.extend(options, {
            title : title,
            content : content,
            closeable : closeable
        });
        button.popover("destroy");
        var $popover = button.popover(options).data('popover');
        var $tip = $popover.tip();
        $(".popover-title", $tip).css("backgroundColor", "#0088cc");
    });
}
</script>
</head>
<body>
<form class="form-inline">
    <label>输入限制</label>
    <input type="text" class="input-small" placeholder="限制整数" render="limitInput" options="int">
    <input type="text" class="input-small" placeholder="限制数字" render="limitInput" options="number">
    <input type="text" class="input-small" placeholder="限制字母" render="limitInput" options="[^a-zA-Z]">
    <input type="text" class="input-small" placeholder="限制手机号码" render="limitInput" validation="{'mobilePhone':true}">
</form>
<hr/>
<form class="form-inline">
    <div class="pagination">
        <label>共 100 条</lable>
        <label>每页</label>
        <select name="pageSize" style="width:60px;" onchange="$.tnx.pager.changePageSize(this.form, this.value);">
            <option value="10">10</option>
            <option value="20">20</option>
            <option value="40">40</option>
        </select>
        <input type="hidden" name="pageSize" style="width:20px;" maxlength="2" value="10"/>
        <ul>
            <li><a href="#" onclick="$.tnx.pager.toPage(this.form, 1)">&lt;&lt;</a></li>
            <li class="disabled"><a href="#">&lt;</a></li>
            <li class="active" onclick="$.tnx.pager.toPage(this.form, 2)"><a href="#">1</a></li>
            <li><a href="#">2</a></li>
            <li><a href="#">3</a></li>
            <li><a href="#">4</a></li>
            <li><a href="#">5</a></li>
            <li><a href="#">&gt;</a></li>
            <li><a href="#">&gt;&gt;</a></li>
        </ul>
        <input type="hidden" name="pageNo" style="width:20px;" maxlength="2" value="1"/>
        <button type="button" class="btn">GO</button>
    </div>
</form>
<hr/>
<form class="form-horizontal">
    <div class="control-group">
        <input type="text" class="span4" name="title" placeholder="标题">
    </div>
    <div class="control-group">
        <textarea rows="4" class="span4" name="content" placeholder="内容"></textarea>
    </div>
    <div class="control-group">
        <label class="checkbox">
            <input type="checkbox" name="closeable"> 可关闭
        </label>
    </div>
    <div class="control-group">
        <button type="button" class="btn btn-primary" onclick="applyPopover(this.form)">Popover</button>
    </div>
    <div class="control-group">
        <table>
            <tr>
                <td align="left">
                    <button type="button" class="btn" options="{'placement':'left', 'arrow':'bottom'}">{'placement':'left', 'arrow':'bottom'}</button>
                </td>
                <td align="left">
                    <button type="button" class="btn" options="{'placement':'top', 'arrow':'right'}">{'placement':'top', 'arrow':'right'}</button>
                </td>
                <td align="center">
                    <button type="button" class="btn" options="{'placement':'top'}">{'placement':'top'}</button>
                </td>
                <td align="right">
                    <button type="button" class="btn" options="{'placement':'top', 'arrow':'left'}">{'placement':'top', 'arrow':'left'}</button>
                </td>
                <td align="right">
                    <button type="button" class="btn" options="{'placement':'right', 'arrow':'bottom'}">{'placement':'right', 'arrow':'bottom'}</button>
                </td>
            </tr>
            <tr>
                <td align="left"></td>
                <td align="left">
                    <button type="button" class="btn" options="{'placement':'left'}">{'placement':'left'}</button>
                </td>
                <td align="center"></td>
                <td align="right">
                    <button type="button" class="btn" options="{'placement':'right'}">{'placement':'right'}</button>
                </td>
                <td align="right"></td>
            </tr>
            <tr>
                <td align="left">
                    <button type="button" class="btn" options="{'placement':'left', 'arrow':'top'}">{'placement':'left', 'arrow':'top'}</button>
                </td>
                <td align="left">
                    <button type="button" class="btn" options="{'placement':'bottom', 'arrow':'right'}">{'placement':'bottom', 'arrow':'right'}</button>
                </td>
                <td align="center">
                    <button type="button" class="btn" options="{'placement':'bottom'}">{'placement':'bottom'}</button>
                </td>
                <td align="right">
                    <button type="button" class="btn" options="{'placement':'bottom', 'arrow':'left'}">{'placement':'bottom', 'arrow':'left'}</button>
                </td>
                <td align="right">
                    <button type="button" class="btn" options="{'placement':'right', 'arrow':'top'}">{'placement':'right', 'arrow':'top'}</button>
                </td>
            </tr>
        </table>
    </div>
</form>
</body>
</html>
