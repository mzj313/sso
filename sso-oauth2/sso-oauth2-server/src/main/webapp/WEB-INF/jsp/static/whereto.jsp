<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<html>
<head>
  <title>Where TO!</title>
  <!-- 设置字符编码 -->
  <meta charset="utf-8">
  <!-- 不缓存页面 -->
  <meta HTTP-EQUIV="pragma" CONTENT="no-cache">
  <meta HTTP-EQUIV="Cache-Control" CONTENT="no-cache, must-revalidate">
  <meta HTTP-EQUIV="expires" CONTENT="0">
  <!-- 使用IE兼容模式 -->
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <!-- 适用不同尺寸的设备显示 -->
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/js/easyui/1.4.1/themes/default/easyui.css">
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/style.css" />
  <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery-1.11.0.min.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.form.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/easyui/1.4.1/jquery.easyui.min.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/easyui/1.4.1/locale/easyui-lang-zh_CN.js"></script>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/extends/css/style.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/extends/css/icon.css">

  <div class="mask-msg" style="display: none; position:fixed; background:#ccc; opacity:.3; filter:alpha(opacity=30); top:0px; left:0px; width:100%; height:100%; z-index:10000"></div>
  <div class="mask-msg datagrid-mask-msg" style="display: none; left: 50%; height: 16px; margin-left: -91px; line-height: 16px; font-size:12px; z-index:10001">正在处理，请稍待。。。</div>
</head>
<body style="background:url(${pageContext.request.contextPath}/static/images/login/bj.jpg) top center no-repeat; background-size:cover;">


<div id="divPanel" style="position: absolute; width: 100%; height: 280px;" align="center">
  <div style="width: 100%; height: 80px;" align="center">
    <img title="logo" src="${pageContext.request.contextPath}/static/images/login/logo.png" style="width: 418px; height: 37px;" />
  </div>
  <div title="" style="width: 418px;" align="center">
    <!-- <div class="easyui-window" title="" style="width: 418px;"> -->
    <h1>Where To!</h1><br>
    <a href="${pageContext.request.contextPath}/logout">退出</a>
  </div>
</div>

</body>
<script type="text/javascript">

  $(document).ready(function(){


    $("#divPanel").css("top",($(window).height() - 372)/2);


  });

</script>
</html>