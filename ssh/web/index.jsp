<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@taglib uri="/struts-tags" prefix="s" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
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
    <a href="<%=request.getContextPath() %>/person_toAdd">添加</a>
   <table border="1">
	<tr>
		<td>用户id</td>
		<td>用户名</td>
		<td>性别</td>
		<td>部门</td>
	</tr>
	<s:iterator value="personList" var="list">
	<tr>
		<td>
			<s:property value="#list.pid"/>
		</td>
		<td>
			<s:property value="#list.pname"/>
		</td>
		<td>		
			<s:property value="#list.gender==1?'男':'女'" />
		</td>
		<td>
			<s:property value="#list.dept.dname"/>
		</td>
		<td>
			<a href="<%=request.getContextPath() %>/person_delete?pid=<s:property value="#list.pid"/>">删除</a>
			<a href="<%=request.getContextPath() %>/person_toUpdate?pid=<s:property value="#list.pid"/>">更新</a>
		</td>			
	</tr>
	</s:iterator>
</table>
  </body>
</html>
