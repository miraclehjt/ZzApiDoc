$(document).ready(function () {
    //还原用户信息
    var username = localStorage.getItem("username");
    var userId = localStorage.getItem("userId");
    var pic = localStorage.getItem("userPic");
    if (userId === null || userId === "") {
        $("#box-user-info").hide();
        $("#form-login").show();
    } else {
        $("#tv-user-name").val(username);
        $("#box-user-info").show();
        $("#form-login").hide();
    }

    //隐藏提示
    $("#tv-hint").alert("close");

    //登录按钮点击
    $("#btn-login").click(function () {
        //	$("#tv-user-name").show();
        doLogin();
    });
    //注销按钮点击
    $("#btn-unregister").click(function () {
        doUnRegister();
    });

});

/*用户登录*/
function doLogin() {
    var username = $("#et-username").val();
    var password = $("#et-password").val();
    $.post("/ZzApiDoc/v1/user/userLogin", {
            phone: username,
            password: password
        },
        function (data, status) {
            if (status === 'success') {
                if (data.code === 0) {
                    //error msg
                    $("#tv-hint-content").val(data.msg);
                    $(".alert").alert()
                } else {
                    //fill data
                    //隐藏登录框
                    $("#form-login").hide();
                    //显示用户名
                    $("#tv-user-name").val(data.data.name);
                    $("#box-user-info").show();
                    //保存用户信息
                    localStorage.setItem("username", data.data.name);
                    localStorage.setItem("userId", data.data.id);
                    localStorage.setItem("userPic", data.data.pic);
                }
            }

        });
}

/*注销*/
function doUnRegister() {
    //清空缓存
    localStorage.removeItem("username");
    localStorage.removeItem("userId");
    localStorage.removeItem("userPic");
    $("#box-user-info").hide();
    $("#form-login").show();
}