<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<html>
<head>
    <title>登录并授权</title>
    <style>.error{color:red;}</style>
</head>
<body>

<div>使用你的Shiro示例Server帐号访问 [${clientName}] ，并同时登录Shiro示例Server</div>
<div class="error">${error}</div>

<form action="${pageContext.request.contextPath}/oauth2Login?asRedirectUrl=${asRedirectUrl}&clientName=${clientName}" method="post">
    用户名：<input type="text" name="username" value="<shiro:principal/>"><br/>
    密码：<input type="password" name="password"><br/>
    <!--TODO 新增写cookei记住密码功能 -->

    <div class="field">
        <label for="captcha" class="field">验证码:</label> <input type="text"
                                                               id="captcha" name="captcha" size="4" maxlength="4"
                                                               class="required" >
    </div>
    <div class="field">
        <!--<label for="codeImg" class="field"></label>--> <img title="点击更换" id="img_captcha"
                                                                onclick="javascript:refreshCaptcha();"
                                                                src="${pageContext.request.contextPath}/captcha?random=1">(看不清<a href="javascript:void(0)" onclick="javascript:refreshCaptcha()">换一张</a>)
    </div>
    自动登录：<input type="checkbox" name="rememberMe" value="true"><br/>
    <input type="submit" value="登录并授权">
</form>
</body>

<script type="text/javascript">
    function refreshCaptcha() {
        document.getElementById("img_captcha").src="${pageContext.request.contextPath}/captcha?random="+(Math.random()*(200-10)+10);
    }
</script>

</html>