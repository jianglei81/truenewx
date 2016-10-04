<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/truenewx-tags" prefix="tnx"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>通用组件</title>
<script type="text/javascript">
$(function() {
    selectMenu(1, 0);
});
function sendSms(btn){
    var formObj = $(btn.form);
    var mobileObj = $(".mobile", formObj);
    if (!mobileObj.val()) {
        mobileObj.select();
        return;
    }
    var contentObj = $(".content", formObj);
    if (!contentObj.val()) {
        contentObj.select();
        return;
    }
    formObj.submit();
}
</script>
</head>
<body>
<section id="smshy">
    <form class="form-inline" action="http://www.duanxin10086.com/sms.aspx" method="POST">
        <input type="hidden" name="action" value="send" />
        <input type="hidden" name="userid" value="10235" />
        <input type="hidden" name="account" value="T2328" />
        <input type="hidden" name="password" value="123456" />
        <p>
            <label class="span1 text-right">海岩短信：</label>
            <input type="text" name="mobile" class="input-large mobile" placeholder="手机号码，多个用,分隔" value="">
            <input type="text" name="content" class="input-large content" placeholder="内容" value="">
            <button type="button" class="btn btn-primary" onclick="sendSms(this)">发送</button>
        </p>
    </form>
</section>
<section id="bjxiaoshizi">
    <form class="form-inline" action="http://114.215.136.186:9002/sms/servlet/UserServiceAPI" method="POST">
        <input type="hidden" name="method" value="sendSMS" />
        <input type="hidden" name="isLongSms" value="0" />
        <input type="hidden" name="username" value="2924747236" />
        <input type="hidden" name="password" value="MTIzNDU2" />
        <input type="hidden" name="smstype" value="1" />
        <p>
            <label class="span1 text-right">小狮子：</label>
            <input type="text" name="mobile" class="input-large mobile" placeholder="手机号码，多个用,分隔" value="">
            <input type="text" name="content" class="input-large content" placeholder="内容" value="">
            <button type="button" class="btn btn-primary" onclick="sendSms(this)">发送</button>
        </p>
    </form>
</section>
<section id="hechina">
    <form class="form-inline" action="http://203.81.21.34/send/gsend.asp" method="POST">
        <input type="hidden" name="name" value="juxzhang88" />
        <input type="hidden" name="pwd" value="jux8527" />
        <p>
            <label class="span1 text-right">巨象科技：</label>
            <input type="text" name="dst" class="input-large mobile" placeholder="手机号码，多个用,分隔" value="">
            <input type="text" name="msg" class="input-large content" placeholder="内容" value="">
            <button type="button" class="btn btn-primary" onclick="sendSms(this)">发送</button>
        </p>
    </form>
</section>
<section id="139000">
    <form class="form-inline" action="http://www.139000.com/send/gsend.asp" method="POST">
        <input type="hidden" name="name" value="juxzhang88" />
        <input type="hidden" name="pwd" value="jux8527" />
        <p>
            <label class="span1 text-right">名商通：</label>
            <input type="text" name="dst" class="input-large mobile" placeholder="手机号码，多个用,分隔" value="">
            <input type="text" name="msg" class="input-large content" placeholder="内容" value="">
            <button type="button" class="btn btn-primary" onclick="sendSms(this)">发送</button>
        </p>
    </form>
</section>
<section id="1xinxi">
    <form class="form-inline" action="http://sms.1xinxi.cn/asmx/smsservice.aspx" method="POST">
        <input type="hidden" name="name" value="13672798520" />
        <input type="hidden" name="pwd" value="930837DFD52F55A22A865F389366" />
        <input type="hidden" name="type" value="pt" />
        <p>
            <label class="span1 text-right">第一信息：</label>
            <input type="text" name="mobile" class="input-large mobile" placeholder="手机号码，多个用,分隔" value="">
            <input type="text" name="content" class="input-large content" placeholder="内容" value="">
            <button type="button" class="btn btn-primary" onclick="sendSms(this)">发送</button>
        </p>
    </form>
</section>
<section id="cr6868">
    <form class="form-inline" action="http://web.cr6868.com/asmx/smsservice.aspx" method="POST">
        <input type="hidden" name="name" value="2924747236@qq.com" />
        <input type="hidden" name="pwd" value="9D76BD589AEEE15964C0FA51A451" />
        <input type="hidden" name="type" value="pt" />
        <input type="hidden" name="sign" value="xx科技" />
        <p>
            <label class="span1 text-right">创瑞通讯：</label>
            <input type="text" name="mobile" class="input-large mobile" placeholder="手机号码，多个用,分隔" value="">
            <input type="text" name="content" class="input-large content" placeholder="内容" value="">
            <button type="button" class="btn btn-primary" onclick="sendSms(this)">发送</button>
        </p>
    </form>
</section>
</body>
</html>
