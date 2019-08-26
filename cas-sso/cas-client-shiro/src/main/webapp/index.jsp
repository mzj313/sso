<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="org.apache.shiro.subject.Subject,org.apache.shiro.SecurityUtils"%>
<!DOCTYPE html>
<html>
<head>
	<title>Shiro CAS Client</title>
	<meta charset="UTF-8" />
	<link rel="stylesheet" href="/resources/css/bootstrap.min.css" />
</head>
<body>
	<div class="container">
		<ul class="nav nav-tabs">
			<li class="active"><a href="/index.jsp">You are on the /index.jsp page</a></li>
			<li><a href="/protected/index.jsp">Call the /protected/index.jsp page</a></li>
			<!-- #### change with your own CAS server and your host name #### -->
			<li><a href="http://localhost:8280/cas/logout?service=http://localhost:8282">Call the CAS logout</a></li>
		</ul>
		<br>
		<% 
		    Subject subject = SecurityUtils.getSubject();
		    System.out.println("subject: " + subject);
		%>
		<h3>
			<p>principals : <%=subject.getPrincipals()%></p>
			<p>isAuthenticated : <%=subject.isAuthenticated()%></p>
		</h3>
	</div>
</body>
</html>
