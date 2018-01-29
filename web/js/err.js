$(document).ready(function () {
    //还原用户信息
    var username = localStorage.getItem("username");
    var userId = localStorage.getItem("userId");
    var pic = localStorage.getItem("userPic");
    var projectId = localStorage.getItem("projectId");
    /*表标题改为项目名*/
    var projectName = localStorage.getItem("projectName");
    $("#table-title").text(projectName+" 的全局错误码");
    if (userId === null || userId === "") {
        doExitLogin();
    } else {
        $("#tv-user-name").text(username);
        $("#box-user-info").show();
        $("#form-login").hide();
        getProjectList(projectId, userId);
    }
    //密码框隐藏与显示
    $('#et-pswd-edit').password()
        .password('focus')
        .on('show.bs.password', function (e) {
            $('#eventLog').text('On show event');
            $('#methods').prop('checked', true);
        }).on('hide.bs.password', function (e) {
        $('#eventLog').text('On hide event');
        $('#methods').prop('checked', false);
    });
    $('#et-new-pswd-edit').password()
        .password('focus')
        .on('show.bs.password', function (e) {
            $('#eventLog').text('On show event');
            $('#methods').prop('checked', true);
        }).on('hide.bs.password', function (e) {
        $('#eventLog').text('On hide event');
        $('#methods').prop('checked', false);
    });
    //编辑按钮
    $(document).on("click", ".btn-edit-err", function () {
        var colDbId = $(this).parent().parent().find(".db-id");
        localStorage.setItem("edit-err-id", colDbId.text());
        var code = $(this).parent().parent().find(".code").text();
        var note = $(this).parent().parent().find(".note").text();
        $("#et-err-code-edit").val(code);
        $("#et-err-note-edit").val(note);
    });

    //登录按钮点击
    $("#btn-login").click(function () {
        //	$("#tv-user-name").show();
        doLogin();
    });
    //注销按钮点击
    $("#btn-unregister").click(function () {
        doExitLogin();
    });
    //刷新按钮点击
    $("#btn-refresh").click(function () {
        var userId = localStorage.getItem("userId");
        var projectId = localStorage.getItem("projectId");
        getProjectList(projectId, userId);
    });
    //删除按钮点击
    $("#btn-delete").click(function () {
        doDelete();
    });
    //全选监听
    $("#checkbox-all").change(function () {
        var isCheck = $(this).is(':checked');
        $(".styled").prop("checked", isCheck);
    });

    //添加按钮点击
    $("#btn-add-err").click(function () {
        $("#et-err-code").val("");
        $("#et-err-note").val("");
    });
    //添加对话框保存按钮
    $("#btn-add-save").click(function () {
        addResParam();
    });
    //编辑对话框保存按钮
    $("#btn-edit-save").click(function () {
        var responseArgId = localStorage.getItem("edit-err-id");
        editResParam(responseArgId);
    });
    //用户名点击
    $("#tv-user-name").click(function () {
        var username = localStorage.getItem("username");
        var userId = localStorage.getItem("userId");
        var phone = localStorage.getItem("phone");
        var email = localStorage.getItem("email");
        var sex = localStorage.getItem("sex");

        $("#et-user-name-edit").val(username);
        $("#et-phone-edit").val(phone);
        $("#et-pswd-edit").val("");
        $("#et-new-pswd-edit").val("");
        $("#et-email-edit").val(email);
        $("#select-sex-edit").selectpicker('val', sex);
    });

    //修改用户信息
    $("#btn-user-ok").click(function () {
        updateUserInfo();
    });

});

function updateUserInfo() {
    var userId = localStorage.getItem("userId");
    var username = $("#et-user-name-edit").val();
    var phone = $("#et-phone-edit").val();
    var pswd = $("#et-pswd-edit").val();
    var pswdNew = $("#et-new-pswd-edit").val();
    var email = $("#et-email-edit").val();
    var sex = $("#select-sex-edit").val();
    $.post("/ZzApiDoc/v1/user/updateUserInfo", {
            userId: userId,
            phone: phone,
            oldPassword: pswd,
            password: pswdNew,
            name: username,
            sex: sex,
            email: email
        },
        function (data, status) {
            if (status === 'success') {
                if (data.code === 0) {
                    //error msg
                    showHintMsg(data.msg);
                } else {
                    showOkMsg(data.msg);
                    //fill data
                    doExitLogin();
                    $("#et-username").val(phone);
                }
            }

        });
}
/*用户登录*/
function doLogin() {
    var username = $("#et-username").val();
    var password = $("#et-password").val();
    if (username === "" || password === "") {
        //error msg
        showHintMsg("用户名或密码不能为空");
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
                    showHintMsg(data.msg);
                } else {
                    clearHint();
                    //fill data
                    //隐藏登录框
                    $("#form-login").hide();
                    //显示用户名
                    $("#tv-user-name").text(data.data.name);
                    $("#box-user-info").show();
                    //保存用户信息
                    localStorage.setItem("username", data.data.name);
                    localStorage.setItem("userId", data.data.id);
                    localStorage.setItem("userPic", data.data.pic);
                    localStorage.setItem("phone", data.data.phone);
                    localStorage.setItem("email", data.data.email);
                    localStorage.setItem("sex", data.data.sex);
                    var projectId = localStorage.getItem("projectId");
                    getProjectList(projectId, data.data.id);
                }
            }

        });
}

/*注销*/
function doExitLogin() {
    //清空缓存
    localStorage.clear();
    location.href = "home";
}

/*获取项目列表*/
function getProjectList(projectId, userId) {
    if (userId === null || userId.length === 0) {
        $("#box-user-info").hide();
        $("#form-login").show();
        showHintMsg("登录已过期，请重新登录");
        return;
    }
    if (projectId === null || projectId.length === 0) {
        showHintMsg("请先选择项目");
        return;
    }

    $.get("/ZzApiDoc/v1/errorCode/getAllErrorCode?projectId=" + projectId + "&userId=" + userId + "&global=" + true + "&group=" + false,
        function (data, status) {
            if (status === 'success') {
                if (data.code === 0) {
                    showHintMsg(data.msg);
                } else {
                    //填充表格
                    var c = "";
                    $.each(data.data, function (n, value) {
                        c += '<tr>' +
                            '<td><div class="checkbox"><input type="checkbox" id="checkbox' + n + '" class="styled"><label for="checkbox' + n + '">选择</label></div></td>' +
                            '<td class="db-id hide">' + value.id + '</td>' +
                            '<td class="code">' + value.code + '</td>' +
                            '<td class="note">' + value.note + '</td>' +
                            '<td>' + value.createMan + '</td>' +
                            '<td>' + value.createTime + '</td>' +
                            '<td><button type="button" class="btn-edit-err btn btn-primary"  data-toggle="modal" data-target="#editModel">' +
                            '<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span> 编辑</button></td>' +
                            '</tr>';
                    });
                    $("#project-list").html(c);
                }

            }

        });
}

/*获取选中的行数*/
function getChooseRowsCount() {
    var count = 0;
    var trs = $("#project-list").find("tr");
    for (var i = 0; i < trs.length; i++) {
        var row = trs.eq(i);
        var cb = row.find(":checkbox");
        var isCheck = cb.is(':checked');
        if (isCheck) {
            count++;
        }
    }
    return count;
}
/*获取选中行的数据库id，多个用逗号隔开*/
function getChooseRowsDbIds() {
    var ids = "";
    var trs = $("#project-list").find("tr");
    for (var i = 0; i < trs.length; i++) {
        var row = trs.eq(i);
        var cb = row.find(":checkbox");
        var isCheck = cb.is(':checked');
        if (isCheck) {
            var colDbId = row.find("td.db-id");
            ids += colDbId.text();
            ids += ",";
        }
    }
    if (getChooseRowsCount() > 0) {
        ids = ids.substr(0, ids.length - 1);
    }
    return ids;
}

/**
 * 删除选中行
 */
function doDelete() {
    var userId = localStorage.getItem("userId");
    if (userId === null || userId.length === 0) {
        $("#box-user-info").hide();
        $("#form-login").show();
        showHintMsg("登录已过期，请重新登录");
        return;
    }
    $.post("/ZzApiDoc/v1/errorCode/deleteErrorCodeWeb", {
            userId: userId,
            ids: getChooseRowsDbIds()
        },
        function (data, status) {
            if (status === 'success') {
                if (data.code === 0) {
                    //error msg
                    showHintMsg(data.msg);
                } else {
                    showOkMsg(data.msg);
                    //重新加载数据
                    var projectId = localStorage.getItem("projectId");
                    getProjectList(projectId, userId);
                }
            }

        });
}

/**
 * 编辑错误码
 */
function editResParam(codeId) {
    var code = $("#et-err-code-edit").val();
    var note = $("#et-err-note-edit").val();
    var userId = localStorage.getItem("userId");
    $.post("/ZzApiDoc/v1/errorCode/updateErrorCode", {
            userId: userId,
            codeId: codeId,
            code: code,
            note: note
        },
        function (data, status) {
            if (status === 'success') {
                if (data.code === 0) {
                    //error msg
                    showHintMsg(data.msg);
                } else {
                    showOkMsg(data.msg);
                    //重新加载数据
                    var projectId = localStorage.getItem("projectId");
                    getProjectList(projectId, userId);
                }
            }

        });
}

/**
 * 添加错误码
 */
function addResParam() {
    var code = $("#et-err-code").val();
    var note = $("#et-err-note").val();
    var userId = localStorage.getItem("userId");
    var projectId = localStorage.getItem("projectId");
    $.post("/ZzApiDoc/v1/errorCode/addErrorCode", {
            userId: userId,
            code: code,
            note: note,
            isGlobal: true,
            projectId: projectId
        },
        function (data, status) {
            if (status === 'success') {
                if (data.code === 0) {
                    //error msg
                    showHintMsg(data.msg);
                } else {
                    showOkMsg(data.msg);
                    //重新加载数据
                    var projectId = localStorage.getItem("projectId");
                    getProjectList(projectId, userId);
                }
            }
        });
}

/**
 * 拼接提示html
 * @param msg
 * @returns {string}
 */
function showHintMsg(msg) {
    $("#row-hint").html('<div class="alert alert-warning" id="tv-hint"> <a href="#" class="close" data-dismiss="alert"> &times;</a><label id="tv-hint-content">' + msg + '</label></div>');
    window.setTimeout("clearHint()", 1500);//使用字符串执行方法
}
/**
 * 拼接成功html
 * @param msg
 * @returns {string}
 */
function showOkMsg(msg) {
    $("#row-hint").html('<div class="alert alert-success" id="tv-hint"> <a href="#" class="close" data-dismiss="alert"> &times;</a><label id="tv-hint-content">' + msg + '</label></div>');
    window.setTimeout("clearHint()", 1500);//使用字符串执行方法
}

/**
 * 清空hint
 */
function clearHint() {
    $("#row-hint").html("");
}