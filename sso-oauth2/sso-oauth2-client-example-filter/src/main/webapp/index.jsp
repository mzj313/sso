<%@ page language="java" contentType="textml; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="java.util.ResourceBundle" %>
<%
    String path = request.getContextPath();
    ResourceBundle resource = ResourceBundle.getBundle("OAuth2-config");

    String LOGOUT_URL = resource.getString("logout_url").trim();
    String REDIRECT_URL = resource.getString("redirect_url").trim();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>index</title>
    </head>
    <body>
        Welcome Access Application A!please to <a href="<%=path %>/home">home</a><br>
        <a href="<%=LOGOUT_URL%>?redirectUrl=<%=REDIRECT_URL %>">LOGOUT</a> Asserver.
    </body>
</html>