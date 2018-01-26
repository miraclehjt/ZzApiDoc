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
        getProjectList(userId, 1);
    }
    //列表按钮事件绑定
    $(document).on("click",".btn-see-group",function(){
        var colDbId = $(this).parent().parent().find(".db-id");
        localStorage.setItem("projectId", colDbId.text());
        location.href ="groupList.jsp"
    });
    //编辑按钮
    $(document).on("click", ".btn-edit-project", function () {
        var colDbId = $(this).parent().parent().find(".db-id");
        localStorage.setItem("edit-project-id", colDbId.text());
        var name = $(this).parent().parent().find(".name").text();
        var note = $(this).parent().parent().find(".note").text();
        var packageName = $(this).parent().parent().find(".package-name").text();
        var type = $(this).parent().parent().find(".int-type").text();
        $("#select-project-type-edit").selectpicker('val', type);
        $("#et-project-name-edit").val(name);
        $("#et-note-edit").val(note);
        $("#et-package-name-edit").val(packageName);
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
        getProjectList(userId, 1);
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
    $("#btn-add-project").click(function () {
        $("#select-project-type").selectpicker('val', '0');
        $("#et-project-name").val("");
        $("#et-note").val("");
        $("#et-package-name").val("");
    });
    //添加对话框保存按钮
    $("#btn-add-save").click(function () {
        addResParam();
    });
    //编辑对话框保存按钮
    $("#btn-edit-save").click(function () {
        var projectId = localStorage.getItem("edit-project-id");
        editResParam(projectId);
    });


});

/*用户登录*/
function doLogin() {
    var username = $("#et-username").val();
    var password = $("#et-password").val();
    if (username === "" || password === "") {
        //error msg
        $("#row-hint").html(getHintContent("用户名或密码不能为空"));
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
                    $("#row-hint").html(getHintContent(data.msg));
                } else {
                    $("#row-hint").html("");
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
                    getProjectList(data.data.id, 1);
                }
            }

        });
}

/*注销*/
function doExitLogin() {
    //清空缓存
    localStorage.removeItem("username");
    localStorage.removeItem("userId");
    localStorage.removeItem("userPic");
    //用户信息隐藏
    $("#box-user-info").hide();
    //显示用户名和密码输入框
    $("#form-login").show();
    //清空列表
    getProjectList("", 1);
}

/*获取项目列表*/
function getProjectList(userId, index) {
    if (userId === null || userId.length === 0) {
        $("#box-user-info").hide();
        $("#form-login").show();
        $("#row-hint").html(getHintContent("登录已过期，请重新登录"));
        return;
    }
    $.get("/ZzApiDoc/v1/project/getAllProjectWeb?userId=" + userId + "&page=" + index,
        function (data, status) {
            if (status === 'success') {
                //填充表格
                var c = "";
                $.each(data.rows, function (n, value) {
                    c += '<tr><td><div class="checkbox"><input type="checkbox" id="checkbox' + n + '" class="styled"><label for="checkbox'
                        + n + '">选择</label></div></td><td class="db-id hide">' + value.id + '</td><td class="int-type hide">' + value.property + '</td><td class="name">'
                        + value.name + '</td><td class="note">' + value.note + '</td><td class="package-name hide">' + value.packageName + '</td><td>'
                        + (value.property === "0" ? '公有' : '私有') + '</td><td>' + value.interfaceNo + '</td><td>'
                        + value.createUserName + '</td><td>' + value.createTime + '</td><td><button type="button" class="btn-see-group btn">分组管理</button></td>' +
                        '<td><button type="button" class="btn-edit-project btn btn-primary"  data-toggle="modal" data-target="#editModel">编辑</button></td>' +
                        '<td><a href="v1/interface/downloadPdf?userId='+userId+'&projectId='+value.id+'" target="_blank" class="btn-edit-project btn btn-primary">点击下载</a></td></tr>';
                });
                $("#project-list").html(c);

                // if($("#page-indicator").data("twbs-pagination")){
                //     $("#page-indicator").twbsPagination("destroy");
                // }
                var indicator = $("#page-indicator");
                indicator.twbsPagination("destroy");
                //分页绑定
                indicator.twbsPagination({
                    totalPages: data.totalPage,
                    visiblePages: 10,
                    onPageClick: function (event, page) {
                        //全选取消
                        $("#checkbox-all").prop("checked", false);
                        //重载数据
                        var mid = localStorage.getItem("userId");
                        justUpdateList(mid, page);
                    }
                });
            }

        });
    return false;
}

/*获取项目列表*/
function justUpdateList(userId, index) {
    if (userId === null || userId.length === 0) {
        $("#box-user-info").hide();
        $("#form-login").show();
        $("#row-hint").html(getHintContent("登录已过期，请重新登录"));
        return;
    }
    $.get("/ZzApiDoc/v1/project/getAllProjectWeb?userId=" + userId + "&page=" + index,
        function (data, status) {
            if (status === 'success') {
                //填充表格
                var c = "";
                $.each(data.rows, function (n, value) {
                    c += '<tr><td><div class="checkbox"><input type="checkbox" id="checkbox' + n + '" class="styled"><label for="checkbox'
                        + n + '">选择</label></div></td><td class="db-id hide">' + value.id + '</td><td class="int-type hide">' + value.property + '</td><td class="name">'
                        + value.name + '</td><td class="note">' + value.note + '</td><td class="package-name hide">' + value.packageName + '</td><td>'
                        + (value.property === "0" ? '公有' : '私有') + '</td><td>' + value.interfaceNo + '</td><td>'
                        + value.createUserName + '</td><td>' + value.createTime + '</td><td><button type="button" class="btn-see-group btn">分组管理</button></td>' +
                        '<td><button type="button" class="btn-edit-project btn btn-primary"  data-toggle="modal" data-target="#editModel">编辑</button></td>' +
                        '<td><a href="v1/interface/downloadPdf?userId='+userId+'&projectId='+value.id+'" target="_blank" class="btn-edit-project btn btn-primary">点击下载</a></td></tr>';
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
        $("#row-hint").html(getHintContent("登录已过期，请重新登录"));
        return;
    }
    $.post("/ZzApiDoc/v1/project/deleteProjectWeb", {
            userId: userId,
            ids: getChooseRowsDbIds()
        },
        function (data, status) {
            if (status === 'success') {
                if (data.code === 0) {
                    //error msg
                    $("#row-hint").html(getHintContent(data.msg));
                } else {
                    $("#row-hint").html(getOkContent(data.msg));
                    //重新加载数据
                    getProjectList(userId, 1);
                }
            }

        });
}

/**
 * 编辑项目
 */
function editResParam(projectId) {
    var type = $("#select-project-type-edit").val();
    var note = $("#et-note-edit").val();
    var name = $("#et-project-name-edit").val();
    var packageName = $("#et-package-name-edit").val();
    var userId = localStorage.getItem("userId");
    $.post("/ZzApiDoc/v1/project/updateProject", {
            userId: userId,
            name: name,
            property: type,
            note: note,
            packageName: packageName,
            projectId: projectId
        },
        function (data, status) {
            if (status === 'success') {
                if (data.code === 0) {
                    //error msg
                    $("#row-hint").html(getHintContent(data.msg));
                } else {
                    $("#row-hint").html(getOkContent(data.msg));
                    //重新加载数据
                    getProjectList(userId, 1);
                }
            }

        });
}

/**
 * 添加项目
 */
function addResParam() {
    var type = $("#select-project-type").val();
    var note = $("#et-note").val();
    var name = $("#et-project-name").val();
    var packageName = $("#et-package-name").val();
    var userId = localStorage.getItem("userId");
    $.post("/ZzApiDoc/v1/project/addProject", {
            userId: userId,
            name: name,
            property: type,
            note: note,
            packageName: packageName
        },
        function (data, status) {
            if (status === 'success') {
                if (data.code === 0) {
                    //error msg
                    $("#row-hint").html(getHintContent(data.msg));
                } else {
                    $("#row-hint").html(getOkContent(data.msg));
                    //重新加载数据
                    getProjectList(userId, 1);
                }
            }

        });
}


/**
 * 拼接提示html
 * @param msg
 * @returns {string}
 */
function getHintContent(msg) {
    return '<div class="alert alert-warning" id="tv-hint"> <a href="#" class="close" data-dismiss="alert"> &times;</a><label id="tv-hint-content">' + msg + '</label></div>'
}
/**
 * 拼接成功html
 * @param msg
 * @returns {string}
 */
function getOkContent(msg) {
    return '<div class="alert alert-success" id="tv-hint"> <a href="#" class="close" data-dismiss="alert"> &times;</a><label id="tv-hint-content">' + msg + '</label></div>'
}