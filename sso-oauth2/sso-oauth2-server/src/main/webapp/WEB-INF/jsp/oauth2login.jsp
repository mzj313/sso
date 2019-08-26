<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no">
    <title>PDMI统一用户登录平台</title>
    <link href="${pageContext.request.contextPath}/static/login/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/static/login/font-awesome/css/font-awesome.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/static/login/css/animate.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/static/login/css/style.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/static/login/css/plugins/awesome-bootstrap-checkbox/awesome-bootstrap-checkbox.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/static/login/css/login.css" rel="stylesheet">
</head>

<body class="gray-bg">
    <div class="row contact-box">
        <div class="col-md-offset-3">
            <i class="pdmi-logo"></i>
            <span class="top-title">统一用户登录平台</span>
        </div>
    </div>
    <div class="loginscreen bg-white">
        <div class="middle-box">           
            <form id="loginForm" class="m-t" role="form" method="post" onsubmit="if(validateLoginForm())return true;else return false;">               
                <div class="form-group input">
                    <div class="input-icon">
                        <i class="fa fa-envelope"></i>
                        <input id="username" type="text" name="username" class="form-control" placeholder="用户名" maxlength="50" autocomplete="off">
                        <label id="username-error" class="error" for="password">This field is required.</label>                        
                    </div>
                </div>
                <div class="form-group input">
                    <div class="input-icon">
                        <i class="fa fa-lock"></i>
                        <input id="password" type="password" name="password" class="form-control " placeholder="密码" name="password" maxlength="16" autocomplete="off">
                        <label id="password-error" class="error" for="password">This field is required.</label>
                    </div>
                </div>
                <div class="form-group input">
                    <div class="input-icon">
                        <i class="fa fa-key"></i>
                        <input id="captcha" type="text" name="captcha" class="form-control validate-input" placeholder="验证码" name="verify" maxlength="5" autocomplete="off">
                        <a id="changecode" class="change-code">换一张</a>
                        <div class="authimg">
                        <img id="codeUrl" src="${pageContext.request.contextPath}/captcha?random=1" class="validate-img">
                        </div>
                        <label id="captcha-error" class="error" for="password">This field is required.</label>
                        <div id="error-info"  <c:if test="${error!=null}">class="show-info"</c:if> <c:if test="${error==null}">class="hidden-info"</c:if>>
                        登录失败<br /> <br /> 原因: ${error}
                    </div>
                    </div>
                </div>                             
                <button type="submit" class="btn btn-primary red block full-width m-b loginBtn">授权并登录</button>
                
                <%--
                <div class="form-group text-left">
                    <div class="col-xs-8">
                       <div class="checkbox">
                            <input type="checkbox" id="checkForSave">
                            <label for="checkForSave">
                                十天内免登录
                            </label>
                        </div>
                    </div>
                    <div class="col-xs-4 text-right"><a href="#">忘记密码?</a></div>
                </div>   
                 --%>
                <div style="margin-top: 10px;" align="right">
                		
						<!-- <a id="forgetPwd" href="http://localhost:8180/pup/auth/toLogin2" style="text-decoration:none;color:#000000;">修改密码</a> -->
						<!-- <a href="javascript:void(0)" onclick="window.open('http://localhost:8180/pup/auth/toLogin2','_blank')" style="text-decoration:none;color:#000000;">修改密码</a> -->
						<!-- 8180 改成 <%=request.getServerPort()%> -->
						<a href="javascript:void(0)" onclick="window.open('http://<%=request.getServerName()%>:8180/pup/auth/toLogin2','登录','menubar=no')" style="text-decoration:none;color:#000000;">修改密码</a>
				</div>
            </form>            
        </div>
    </div>
    <div class="bottom-row"><p class="m-t m-t-bottom  text-center"> <small>2015 &copy; PDMI.Co.Ltd.Brought to you by PDMI.cn</small> </p></div>
    <!-- Mainly scripts -->
    <script src="${pageContext.request.contextPath}/static/login/js/jquery.min.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/static/login/js/login.js" type="text/javascript"></script>
    <script>
    $("body").height($(window).height());
        document.body.addEventListener('touchmove', function (event) {
        event.preventDefault();
    }, false);
    $("#changecode").click(function(){
        $("#codeUrl").attr("src","${pageContext.request.contextPath}/captcha?random="+(Math.random()*(200-10)+10));
    });
    $("#codeUrl").click(function(){
        $(this).attr("src","${pageContext.request.contextPath}/captcha?random="+(Math.random()*(200-10)+10));
    });
    </script>
</body>

</html>
