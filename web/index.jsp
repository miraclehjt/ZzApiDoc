<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8"/>
    <title>登陆页面</title>
    <link rel="stylesheet" type="text/css" href="css/common.css"/>
    <script src="js/jquery-3.2.1.min.js"></script>
    <script type="text/javascript">

    </script>
</head>

<body>
<div class="login_box">
    <h2>小周接口文档管理</h2>
    <div class="login_username">
        手机号：
        <input type="text" name="username" id="et_username" value=""/>
    </div>
    <div class="login_password">
        密码：
        <input type="text" name="password" id="et_password" value=""/>
    </div>
    <%--<input type="button" name="" id="login_btn" value="登陆" class="submit_btn"/>--%>
    <button id="login_btn">登陆</button>
</div>
</body>

</html>