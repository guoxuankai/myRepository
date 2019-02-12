<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'add.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
    <form action="<%=request.getContextPath() %>/person_add" method="post">
    	<label>用户名</label>
  		<input type="text" name="pname">
  		<br>
  		<label>性别</label>
  		<input type="radio" name="gender" value="1">男
  		<input type="radio" name="gender" value="0">女
  		<br>
  		<label>部门</label>
  		<select name="dept.did">
  			<s:iterator value="deptList" var="pl">			
  			<option value="<s:property value='#pl.did'/>"><s:property value="#pl.dname"/></option>
  			</s:iterator>
  		</select>
  		<br>
  		<input type="submit" value="提交">
    </form>
  </body>
</html>
