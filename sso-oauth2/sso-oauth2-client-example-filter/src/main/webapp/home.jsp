<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    String path = request.getContextPath();
    String openId = (String) request.getAttribute("openId");
    String userId = (String) request.getAttribute("userId");
    String username = (String) request.getAttribute("username");
    String asToken = (String) request.getAttribute("asToken");
    String expir = (String) request.getAttribute("expir");

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>index</title>
</head>
<body>
Welcome home,<%=username%>,your OPENID is <%=openId%> and your USERID is <%=userId%><br>
please use your Token(<%=asToken%>) before <%=expir%>。
<br><br><br>
if you want to test except url ,please click <a href="<%=path%>/except">here</a><br>
if you want to look the detail person info,please click <a href="<%=path%>/userInfoApi">here</a>!<br>
if you want to refresh the Token,please click <a href="<%=path%>/refreshToken">here</a>!<br>
if you want to logout the app_A ,please click <a href="<%=path%>/logOut">logout</a><br>

<%@page import="com.chinasofti.oauth2.client.model.OAuth2Config"%>
<%@page import="java.util.Base64"%>
<%
OAuth2Config config1 = new OAuth2Config("OAuth2-config.properties");
String apiUrl = config1.getApiURL();
//System.out.println(apiUrl);
Base64.Encoder encoder = Base64.getEncoder();
asToken = encoder.encodeToString(asToken.getBytes());
%>
<form action="<%=apiUrl%>/userapi/password2" method="post" target='_blank'>
	<input type="hidden" id="token2" name="token" value="<%=asToken %>">
	<!-- flag:<input type="text" name="flag"> -->
	<input type="submit" value="修改密码">
</form>

</body>
</html>