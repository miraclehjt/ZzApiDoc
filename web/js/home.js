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
        getProjectList(userId);
    }

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
    if (username === "" || password === "") {
        //error msg
        $("#row-hint").html(
            '<div class="alert alert-warning" id="tv-hint"> <a href="#" class="close" data-dismiss="alert"> &times;</a><label id="tv-hint-content">' + "用户名或密码不能为空" + '</label></div>'
        );
        return;
    }
    $.post("/ZzApiDoc/v1/user/userLogin", {
            phone: username,
            password: password
        },
        function (data, status) {
            if (status === 'success') {
                if (data.code === 0) {
                    //error msg
                    $("#row-hint").html(
                        '<div class="alert alert-warning" id="tv-hint"> <a href="#" class="close" data-dismiss="alert"> &times;</a><label id="tv-hint-content">' + data.msg + '</label></div>'
                    );
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
                    getProjectList(data.data.id);
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

/*获取项目列表*/
function getProjectList(userId) {
    $.get("/ZzApiDoc/v1/project/getAllProject?userId="+userId,
        function (data, status) {
            if (status === 'success') {
                if (data.code === 0) {
                    //error msg
                    $("#row-hint").html(
                        '<div class="alert alert-warning" id="tv-hint"> <a href="#" class="close" data-dismiss="alert"> &times;</a><label id="tv-hint-content">' + data.msg + '</label></div>'
                    );
                } else {
                    //fill data
                    var c = "";
                    $.each(data.data, function (n, value) {
                        c += '<tr><td><div class="checkbox"><input type="checkbox" id="checkbox2" class="styled"><label for="checkbox2">选择</label></div></td><td>' + value.name + '</td><td>'+value.note+'</td><td>'+value.property+'</td><td>'+value.interfaceNo+'</td><td>'+value.createUserName+'</td><td>'+value.createTime+'</td></tr>';
                    });
                    $("#project-list").html(c);
                }
            }

        });
}