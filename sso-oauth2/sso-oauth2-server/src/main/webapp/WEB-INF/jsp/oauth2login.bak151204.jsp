<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<!doctype html>
<html>
<head>
    <title>用户登录</title>
    <meta content="text/html; charset=utf-8" http-equiv="Content-Type">
    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" name="viewport" />
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache,must-revalidate">
    <meta http-equiv="expires" content="0">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/extends/default/css/login.css">
    <link rel="stylesheet" href="${cdnUrl}/css/login.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/extends/default/js/jquery-1.11.2.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/placeholder.min.js"></script>
</head>
<body>

<div class="content">
    <div class="center">
        <form id="loginForm" action="" method="post" onsubmit="return validityForm();">
            <div class="loginarea">
                <div class="headImg">
                    <img src="${cdnUrl}/imgs/logo.png" onerror="this.src='${pageContext.request.contextPath}/static/extends/default/imgs/logo.png'">
                </div>
                <div class="inputarea">
                    <div class="input">
                        <div class="icon"><img src="${cdnUrl}/imgs/icon01.png" onerror="this.src='${pageContext.request.contextPath}/static/extends/default/imgs/icon01.png'"></div>
                        <div class="username"><input type="text" id="username" name="username" placeholder="用户名" autocomplete="off" disableautocomplete autofocus required></div>
                    </div>
                    <div class="input">
                        <div class="icon"><img src="${cdnUrl}/imgs/icon02.png" onerror="this.src='${pageContext.request.contextPath}/static/extends/default/imgs/icon02.png'"></div>
                        <div class="password"><input type="password" id="password" name="password" placeholder="密码" autocomplete="off" disableautcomplete autofocus required></div>
                    </div>
                    <div class="input">
                        <div class="icon"><img src="${cdnUrl}/imgs/icon03.png" onerror="this.src='${pageContext.request.contextPath}/static/extends/default/imgs/icon03.png'"></div>
                        <div class="authcode"><input type="text" id="captcha" name="captcha" placeholder="验证码" autocomplete="off" disableautcomplete autofocus required></div>
                        <div class="authimg">
                            <img id="codeUrl" src="${pageContext.request.contextPath}/captcha?random=1">
                        </div>
                    </div>

                    <div id="error-info"  <c:if test="${error!=null}">class="show-info"</c:if> <c:if test="${error==null}">class="hidden-info"</c:if>>
                        登录失败<br /> <br /> 原因: ${error}
                    </div>

                </div>
                <div class="loginBtn">授权并登录</div>
            </div>
        </form>
        <div class="bot">2015 © PDMI.Co.Ltd.Brought to you by PDMI.cn</div>
    </div>
</div>
</body>
<script>
    $("body").height($(window).height());
</script>
<script type="text/javascript">
if(window.top.location != window.self.location){
	window.top.location = window.self.location;
}

    $(document).ready(function(){


        $(".loginBtn").bind("click",function(){
            $("#loginForm").submit();
        });

        $("input[name=username],input[name=password],input[name=captcha]").keyup(function(){
            if(event.keyCode == 13){
                $("#loginForm").submit();
            }
        });

        $("#codeUrl").click(function(){
            $(this).attr("src","${pageContext.request.contextPath}/captcha?random="+(Math.random()*(200-10)+10));
        });

    });

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
        //else if(captcha==null||captcha==''){
        //    addErrorInfo(true,"请输入验证码","captcha");
        //    return false;
        //}
        else{
            addErrorInfo(false,"","");
            return true;
        }
    }

    function addErrorInfo(flag,info,target){
        if(flag){
            $("#error-info").html(info);
            $("#error-info").attr("class", "show-info");
            $("#"+target).focus();
        }else{
            $("#error-info").html("");
            $("#error-info").attr("class", "error-info");
        }
    }
</script>
</html>