<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'update.jsp' starting page</title>
    
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
   <form action="<%=request.getContextPath() %>/person_update" method="post">
   		<input type="hidden" name="pid" value="${persons.pid }">
    	<label>用户名</label>
  		<input type="text" name="pname" value="${persons.pname }">
  		<br>
  		<label>性别</label>
  		
  		<input type="radio" name="gender" value="1"
  			<c:choose>
  				<c:when test="${persons.gender==1}">checked="checked"</c:when>
  				<c:otherwise></c:otherwise>
  			</c:choose>
  		>男
  		<input type="radio" name="gender" value="0"
  			<c:choose>
  				<c:when test="${persons.gender==0}">checked="checked"</c:when>
  				<c:otherwise></c:otherwise>
  			</c:choose>
  		>女
  		<br>
  		<label>部门</label>
  		<select name="dept.did">		
  			<c:forEach items="${deptList }" var="dl">
  			<option value="${dl.did }"
  				<c:if test="${dl.did==persons.dept.did }">selected="selected"</c:if>
  			>${dl.dname }</option>
  			</c:forEach>
  		</select>
  		<br>
  		<input type="submit" value="提交">
    </form>
  </body>
</html>
