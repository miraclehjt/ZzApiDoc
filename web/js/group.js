$(document).ready(function () {
    //还原用户信息
    var username = localStorage.getItem("username");
    var userId = localStorage.getItem("userId");
    var pic = localStorage.getItem("userPic");
    var projectId = localStorage.getItem("projectId");
    if (userId === null || userId === "") {
        doExitLogin();
    } else {
        $("#tv-user-name").val(username);
        $("#box-user-info").show();
        $("#form-login").hide();
        getProjectList(projectId, userId, 1);
    }
    //列表按钮事件绑定
    $(document).on("click",".btn-see-group",function(){
        var colDbId = $(this).parent().parent().find(".db-id");
        localStorage.setItem("groupId", colDbId.text());
        location.href ="interface";
    });
    //编辑按钮
    $(document).on("click", ".btn-edit-group", function () {
        var colDbId = $(this).parent().parent().find(".db-id");
        localStorage.setItem("edit-group-id", colDbId.text());
        var name = $(this).parent().parent().find(".name").text();
        var ipAddr = $(this).parent().parent().find(".ip-addr").text();
        $("#et-group-name-edit").val(name);
        $("#et-ip-addr-edit").val(ipAddr);
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
        getProjectList(projectId, userId, 1);
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
    $("#btn-add-group").click(function () {
        $("#et-ip-addr").val("http://");
        $("#et-group-name").val("");
    });
    //添加对话框保存按钮
    $("#btn-add-save").click(function () {
        addResParam();
    });
    //编辑对话框保存按钮
    $("#btn-edit-save").click(function () {
        var responseArgId = localStorage.getItem("edit-group-id");
        editResParam(responseArgId);
    });


});

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
                    $("#tv-user-name").val(data.data.name);
                    $("#box-user-info").show();
                    //保存用户信息
                    localStorage.setItem("username", data.data.name);
                    localStorage.setItem("userId", data.data.id);
                    localStorage.setItem("userPic", data.data.pic);
                    var projectId = localStorage.getItem("projectId");
                    getProjectList(projectId, data.data.id, 1);
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
function getProjectList(projectId, userId, index) {
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

    $.get("/ZzApiDoc/v1/interfaceGroup/getAllInterfaceGroupWeb?projectId="+projectId+"&userId=" + userId + "&page=" + index,
        function (data, status) {
            if (status === 'success') {
                //填充表格
                var c = "";
                $.each(data.rows, function (n, value) {
                    c += '<tr><td><div class="checkbox"><input type="checkbox" id="checkbox' + n + '" class="styled"><label for="checkbox'
                        + n + '">选择</label></div></td><td class="db-id hide">' + value.id + '</td><td class="name">'
                        + value.name + '</td><td class="ip-addr">' + value.ip + '</td><td>'
                        + value.interfaceNo + '</td><td>' + value.createUserName + '</td><td>' + value.createTime + '</td>' +
                        '<td><button type="button" class="btn-see-group btn">接口管理</button></td>' +
                        '<td><button type="button" class="btn-edit-group btn btn-primary"  data-toggle="modal" data-target="#editModel">编辑</button></td></tr>';
                });
                $("#project-list").html(c);

                // if($("#page-indicator").data("twbs-pagination")){
                //     $("#page-indicator").twbsPagination("destroy");
                // }
                var sel =$("#page-indicator");
                sel.twbsPagination("destroy");
                //分页绑定
                sel.twbsPagination({
                    totalPages: data.totalPage,
                    visiblePages: 10,
                    onPageClick: function (event, page) {
                        //全选取消
                        $("#checkbox-all").prop("checked", false);
                        //重载数据
                        var mid = localStorage.getItem("userId");
                        var projectId = localStorage.getItem("projectId");
                        justUpdateList(projectId, mid, page);
                    }
                });
            }

        });
    return false;
}

/*获取项目列表*/
function justUpdateList(projectId, userId, index) {
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

    $.get("/ZzApiDoc/v1/interfaceGroup/getAllInterfaceGroupWeb?projectId="+projectId+"&userId=" + userId + "&page=" + index,
        function (data, status) {
            if (status === 'success') {
                //填充表格
                var c = "";
                $.each(data.rows, function (n, value) {
                    c += '<tr><td><div class="checkbox"><input type="checkbox" id="checkbox' + n + '" class="styled"><label for="checkbox'
                        + n + '">选择</label></div></td><td class="db-id hide">' + value.id + '</td><td class="name">'
                        + value.name + '</td><td class="ip-addr">' + value.ip + '</td><td>'
                        + value.interfaceNo + '</td><td>' + value.createUserName + '</td><td>' + value.createTime + '</td>' +
                        '<td><button type="button" class="btn-see-group btn">接口管理</button></td>' +
                        '<td><button type="button" class="btn-edit-group btn btn-primary"  data-toggle="modal" data-target="#editModel">编辑</button></td></tr>';
                });
                $("#project-list").html(c);
            }

        });
    return false;
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
        ids = ids.substr(0, ids.length-1);
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
    $.post("/ZzApiDoc/v1/interfaceGroup/deleteInterfaceGroupWeb", {
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
                    getProjectList(projectId, userId, 1);
                }
            }

        });
}

/**
 * 编辑分组
 */
function editResParam(interfaceGroupId) {
    var ip = $("#et-ip-addr-edit").val();
    var name = $("#et-group-name-edit").val();
    var userId = localStorage.getItem("userId");
    var projectId = localStorage.getItem("projectId");
    $.post("/ZzApiDoc/v1/interfaceGroup/updateInterfaceGroup", {
            userId: userId,
            interfaceGroupId: interfaceGroupId,
            name: name,
            ip: ip,
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
                    getProjectList(projectId, userId, 1);
                }
            }

        });
}

/**
 * 添加分组
 */
function addResParam() {
    var ip = $("#et-ip-addr").val();
    var name = $("#et-group-name").val();
    var userId = localStorage.getItem("userId");
    var projectId = localStorage.getItem("projectId");
    $.post("/ZzApiDoc/v1/interfaceGroup/addInterfaceGroup", {
            userId: userId,
            name: name,
            ip: ip,
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
                    getProjectList(projectId, userId, 1);
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