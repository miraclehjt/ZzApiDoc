$(document).ready(function () {
    //还原用户信息
    var username = localStorage.getItem("username");
    var userId = localStorage.getItem("userId");
    var pic = localStorage.getItem("userPic");
    if (userId === null || userId === "") {
        doExitLogin();
    } else {
        $("#tv-user-name").text(username);
        $("#box-user-info").show();
        $("#form-login").hide();
        var interfaceId = localStorage.getItem("interfaceId");
        var pid = localStorage.getItem("pid");
        getProjectList(interfaceId, userId, pid);
    }
    //密码框隐藏与显示
    $('#et-pswd-edit').password()
        .password('focus')
        .on('show.bs.password', function(e) {
            $('#eventLog').text('On show event');
            $('#methods').prop('checked', true);
        }).on('hide.bs.password', function(e) {
        $('#eventLog').text('On hide event');
        $('#methods').prop('checked', false);
    });
    $('#et-new-pswd-edit').password()
        .password('focus')
        .on('show.bs.password', function(e) {
            $('#eventLog').text('On show event');
            $('#methods').prop('checked', true);
        }).on('hide.bs.password', function(e) {
        $('#eventLog').text('On hide event');
        $('#methods').prop('checked', false);
    });
    //返回参数
    $(document).on("click", ".btn-see-res", function () {
        var colDbId = $(this).parent().parent().find(".db-id");
        localStorage.setItem("pid", colDbId.text());
        window.location.reload();
    });
    //编辑按钮
    $(document).on("click", ".btn-edit-res", function () {
        var colDbId = $(this).parent().parent().find(".db-id");
        localStorage.setItem("edit-res-id", colDbId.text());
        var type = $(this).parent().parent().find(".int-type").text();
        var name = $(this).parent().parent().find(".name").text();
        var isGlobal = $(this).parent().parent().find(".isGlobal").text();
        var defValue = $(this).parent().parent().find(".defValue").text();
        var note = $(this).parent().parent().find(".note").text();
        $("#select-param-type-edit").selectpicker('val', type);
        $("#et-param-name-edit").val(name);
        $("#et-def-value-edit").val(defValue);
        $("#et-param-note-edit").val(note);
        $("#cb-global-edit").prop("checked", isGlobal === "true");
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
        var interfaceId = localStorage.getItem("interfaceId");
        var pid = localStorage.getItem("pid");
        getProjectList(interfaceId, userId, pid);
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
    $("#btn-add").click(function () {
        $("#select-param-type").selectpicker('val', '0');
        $("#et-param-note").val("");
        $("#et-param-name").val("");
        $("#et-def-value").val("");
    });
    //添加对话框保存按钮
    $("#btn-add-save").click(function () {
        addResParam();
    });
    //编辑对话框保存按钮
    $("#btn-edit-save").click(function () {
        var responseArgId = localStorage.getItem("edit-res-id");
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
                    $("#row-hint").html("");
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
                    var interfaceId = localStorage.getItem("interfaceId");
                    var pid = localStorage.getItem("pid");
                    getProjectList(groupId, data.data.id, pid);
                }
            }

        });
}


/*注销*/
function doExitLogin() {
    //清空缓存
    localStorage.clear();
    location.href ="home";
}

/*获取项目列表*/
function getProjectList(interfaceId, userId, pid) {
    if (userId === null || userId.length === 0) {
        $("#box-user-info").hide();
        $("#form-login").show();
        showHintMsg("登录已过期，请重新登录");
        return;
    }
    if (interfaceId === null || interfaceId.length === 0) {
        showHintMsg("请先选择接口");
        return;
    }
    $.get("/ZzApiDoc/v1/responseArg/getResponseArgByInterfaceIdAndPid?interfaceId=" + interfaceId + "&userId=" + userId + "&pid=" + pid,
        function (data, status) {
            if (status === 'success') {
                if (data.code === 0) {
                    showHintMsg(data.msg);
                } else {
                    //填充表格
                    var c = "";
                    $.each(data.data, function (n, value) {
                        c += '<tr><td><div class="checkbox"><input type="checkbox" id="checkbox' + n + '" class="styled"><label for="checkbox'
                            + n + '">选择</label></div></td><td class="db-id hide">' + value.id + '</td><td class="int-type hide">' + value.type + '</td><td class="type">'
                            + getTypeName(value.type) + '</td><td  class="name">' + value.name + '</td><td class="isGlobal">' + value.isGlobal + '</td><td class="defValue">' + value.defValue + '</td><td class="note">' + value.note + '</td><td class="person">'
                            + value.createUserName + '</td><td class="createTime">' + value.createTime + '</td><td><button type="button" class="btn-see-res btn' + (isShowBtn(value.type) ? '' : ' hide')
                            + '">子参数</button></td><td><button type="button" class="btn-edit-res btn btn-primary"  data-toggle="modal" data-target="#editModel">编辑</button></td></tr>';
                    });
                    $("#project-list").html(c);
                }
            }

        });
    return false;
}


/**
 * 参数类型转文字
 * @param type
 * @returns {string}
 */
function isShowBtn(type) {
    var name = false;
    switch (type) {
        case "0":
            name = false;
            break;
        case "1":
            name = false;
            break;
        case "2":
            name = true;
            break;
        case "3":
            name = true;
            break;
        case "4":
            name = true;
            break;
        case "5":
            name = true;
            break;
        case "6":
            name = false;
            break;
        case "7":
            name = false;
            break;
        case "8":
            name = true;
            break;
        case "9":
            name = false;
            break;
        case "10":
            name = true;
            break;

    }
    return name;
}


/**
 * 参数类型转文字
 * @param type
 * @returns {string}
 */
function getTypeName(type) {
    var name = "string";
    switch (type) {
        case "0":
            name = "string";
            break;
        case "1":
            name = "int";
            break;
        case "2":
            name = "object";
            break;
        case "3":
            name = "array[object]";
            break;
        case "4":
            name = "array[string]";
            break;
        case "5":
            name = "array";
            break;
        case "6":
            name = "file";
            break;
        case "7":
            name = "unknown";
            break;
        case "8":
            name = "array[int]";
            break;
        case "9":
            name = "float";
            break;
        case "10":
            name = "array[float]";
            break;

    }
    return name;
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
    var ids = getChooseRowsDbIds();
    $.post("/ZzApiDoc/v1/responseArg/deleteResponseArgWeb", {
            userId: userId,
            ids: ids
        },
        function (data, status) {
            if (status === 'success') {
                if (data.code === 0) {
                    //error msg
                    showHintMsg(data.msg);
                } else {
                    showOkMsg(data.msg);
                    //重新加载数据
                    var interfaceId = localStorage.getItem("interfaceId");
                    var pid = localStorage.getItem("pid");
                    getProjectList(interfaceId, userId, pid);
                }
            }

        });
}
/**
 * 添加返回参数
 */
function editResParam(responseArgId) {
    var type = $("#select-param-type-edit").val();
    var note = $("#et-param-note-edit").val();
    var name = $("#et-param-name-edit").val();
    var defValue = $("#et-def-value-edit").val();
    var isGlobal = $("#cb-global-edit").is(':checked');
    var userId = localStorage.getItem("userId");
    var interfaceId = localStorage.getItem("interfaceId");
    var pid = localStorage.getItem("pid");
    var projectId = localStorage.getItem("projectId");
    $.post("/ZzApiDoc/v1/responseArg/updateResponseArg", {
            userId: userId,
            responseArgId: responseArgId,
            pid: pid,
            name: name,
            defValue: defValue,
            type: type,
            projectId: projectId,
            interfaceId: interfaceId,
            note: note,
            isGlobal: isGlobal
        },
        function (data, status) {
            if (status === 'success') {
                if (data.code === 0) {
                    //error msg
                    showHintMsg(data.msg);
                } else {
                    showOkMsg(data.msg);
                    //重新加载数据
                    var interfaceId = localStorage.getItem("interfaceId");
                    var pid = localStorage.getItem("pid");
                    getProjectList(interfaceId, userId, pid);
                }
            }

        });
}

/**
 * 添加返回参数
 */
function addResParam() {
    var type = $("#select-param-type").val();
    var note = $("#et-param-note").val();
    var name = $("#et-param-name").val();
    var defValue = $("#et-def-value").val();
    var isGlobal = $("#cb-global").is(':checked');
    var userId = localStorage.getItem("userId");
    var interfaceId = localStorage.getItem("interfaceId");
    var pid = localStorage.getItem("pid");
    var projectId = localStorage.getItem("projectId");
    $.post("/ZzApiDoc/v1/responseArg/addResponseArg", {
            userId: userId,
            pid: pid,
            name: name,
            defValue: defValue,
            type: type,
            projectId: projectId,
            interfaceId: interfaceId,
            note: note,
            isGlobal: isGlobal
        },
        function (data, status) {
            if (status === 'success') {
                if (data.code === 0) {
                    //error msg
                    showHintMsg(data.msg);
                } else {
                    showOkMsg(data.msg);
                    //重新加载数据
                    var interfaceId = localStorage.getItem("interfaceId");
                    var pid = localStorage.getItem("pid");
                    getProjectList(interfaceId, userId, pid);
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
    window.setTimeout("clearHint()",1500);//使用字符串执行方法
}
/**
 * 拼接成功html
 * @param msg
 * @returns {string}
 */
function showOkMsg(msg) {
    $("#row-hint").html('<div class="alert alert-success" id="tv-hint"> <a href="#" class="close" data-dismiss="alert"> &times;</a><label id="tv-hint-content">' + msg + '</label></div>');
    window.setTimeout("clearHint()",1500);//使用字符串执行方法
}

/**
 * 清空hint
 */
function clearHint() {
    $("#row-hint").html("");
}