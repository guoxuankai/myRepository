<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<body>

<a href="/export">导出</a> <br/>
<br>
<form action="/import" enctype="multipart/form-data" method="post">
    <input type="file" name="file"/>
    <input type="submit" value="导入Excel">
</form>
</body>
</html>
