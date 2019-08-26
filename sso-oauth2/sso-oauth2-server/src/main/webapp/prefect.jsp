<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2016/1/22 0022
  Time: 下午 15:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>完善信息</title>
</head>
<body>
${param.account}
请完善你的信息:<br/>
<form action="${pageContext.request.contextPath}/connection" method="post">
<input type="hidden" name="userId" value="${param.userId}"><br/>
  邮&nbsp;箱:<input type="email" name="email" value=""><input id="sendEmailCode" type="button" value="发送邮箱验证码"><br/>
  验证码:<input type="text" name="emailCode"><br/>
  手&nbsp;机:<input type="tel" name="tel"><input id="sendCode" type="button" value="发送手机验证码"><br/>
  验证码:<input type="text" name="code"><br/>
  <input type="submit" value="提交">
</form>
<br>
发送手机验证码：
<form action="${pageContext.request.contextPath}/sendCode" method="post">
  <input type="text" name="userId" value="${param.userId}">
  <input type="tel" name="tel">
  <input type="submit" value="发送验证码">
</form>
<br>
发送邮箱验证码：
<form action="${pageContext.request.contextPath}/sendEmailCode" method="post">
  <input type="text" name="userId" value="${param.userId}">
  <input type="text" name="email">
  <input type="submit" value="发送验证码">
</form>
<br>


</body>
</html>
