<!doctype html>
<%@ page language="java" contentType="text/html; ISO-8859-1" pageEncoding="UTF-8" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
</head>
<body>

    <h1>User Login</h1>

    <form action="${pageContext.request.contextPath}/user/login" method="post">
        用户名: <input type="test" name="username"> <br/>
        密码: <input type="test" name="password"> <br/>

        <input type="submit" value="登录">
    </form>

</body>
</html>