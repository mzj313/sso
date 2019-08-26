<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Signin Template for Bootstrap</title>

    <!-- Bootstrap core CSS -->
    <link href="${pageContext.request.contextPath}/static/js/bootstrap-3.2.0/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <style type="text/css">

        body {
            padding-top: 40px;
            padding-bottom: 40px;
            background-color: #eee;
        }

        .form-signin {
            max-width: 330px;
            padding: 15px;
            margin: 0 auto;
        }
        .form-signin .form-signin-heading,
        .form-signin .checkbox {
            color: #ffffff;
            margin-bottom: 20px;
        }
        .form-signin .checkbox {
            font-weight: normal;
        }
        .form-signin .form-control {
            position: relative;
            height: auto;
            -webkit-box-sizing: border-box;
            -moz-box-sizing: border-box;
            box-sizing: border-box;
            padding: 10px;
            font-size: 16px;
        }
        .form-signin .form-control:focus {
            z-index: 2;
        }
        .form-signin input[type="email"] {
            margin-bottom: -1px;
            border-bottom-right-radius: 0;
            border-bottom-left-radius: 0;
        }
        .form-signin input[type="password"] {
            margin-bottom: 10px;
            border-top-left-radius: 0;
            border-top-right-radius: 0;
        }

        .hidden-info{
            display: none;
        }

        .show-info{
            display: block;
        }

    </style>

    <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
    <!--[if lt IE 9]><script src="${pageContext.request.contextPath}/static/js/ie8-responsive-file-warning.js"></script><![endif]-->
    <script src="${pageContext.request.contextPath}/static/js/ie-emulation-modes-warning.js"></script>

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="${pageContext.request.contextPath}/static/js/html5shiv.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/respond.min.js"></script>
    <![endif]-->
</head>

<body style="background:url(${pageContext.request.contextPath}/static/images/login/bj.jpg) top center no-repeat;">

<div class="container">

    <form class="form-signin" id="loginForm" action="" method="post" onsubmit="return validityForm()">
        <h4 class="form-signin-heading" style="text-align: center;">^PDMI|身份管理和单点登录平台</h4>
        <label for="username" class="sr-only">用户名</label>
        <input type="text" id="username" name="username" class="form-control" placeholder="请输入用户名" required autofocus>
        <label for="password" class="sr-only">密码</label>
        <input type="password" id="password" name="password" class="form-control" placeholder="请输入密码" required>
        <label for="captcha" class="sr-only">验证码</label>
        <input type="text" id="captcha" name="captcha" class="form-control" placeholder="请输入验证码" required>
        <a href="javascript:changeVeryfy();" class="showcode">
            <img id="codeUrl" style="margin: 0 0 0 3px; vertical-align: middle; height: 36px;" src="${pageContext.request.contextPath}/captcha?random=1">
        </a>
        <div class="checkbox">
            <label>
                <input type="checkbox" name="rememberMe" value="true"> 自动登录
            </label>
        </div>
        <div id="error-info"  <c:if test="${error!=null}">class="show-info"</c:if> <c:if test="${error==null}">class="hidden-info"</c:if>>
            <font color="red"> 登录失败<br /> <br /> 原因: ${error}</font>
        </div>
        <button id="doLogin" class="btn btn-lg btn-primary btn-block" type="submit">登录</button>
    </form>

</div> <!-- /container -->


<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<script src="${pageContext.request.contextPath}/static/js/ie10-viewport-bug-workaround.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery-1.11.0.min.js"></script>
<script type="text/javascript">

    function changeVeryfy() {
        document.getElementById("codeUrl").src="${pageContext.request.contextPath}/captcha?random="+(Math.random()*(200-10)+10);
    }

    function validityForm(){
        var username = $("#username").val();
        var password = $("#password").val();
        var captcha = $("#captcha").val();

        if(username==null||username==''){
            addErrorInfo(true,"请输入用户名","username");
            return false;
        }else if(password==null||password==''){
            addErrorInfo(true,"请输入密码","password");
            return false;
        }
        else if(captcha==null||captcha==''){
            addErrorInfo(true,"请输入验证吗","captcha");
            return false;
        }
        else{
            addErrorInfo(false,"","");
            return true;
        }
    }

    function addErrorInfo(flag,info,target){
        if(flag){
            $("#error-info").html("<font color='red'>"+info+"</font>");
            $("#error-info").attr("class", "show-info");
            $("#"+target).focus();
        }else{
            $("#error-info").html("");
            $("#error-info").attr("class", "error-info");
        }
    }
</script>
</body>
</html>
