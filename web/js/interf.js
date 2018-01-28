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
        var groupId = localStorage.getItem("groupId");
        getProjectList(groupId, userId, 1);
    }
    //请求参数
    $(document).on("click",".btn-see-req",function(){
        var colDbId = $(this).parent().parent().find(".db-id");
        localStorage.setItem("interfaceId", colDbId.text());
        localStorage.setItem("pid", "0");
        location.href ="reqArg";
    });
    //返回参数
    $(document).on("click",".btn-see-res",function(){
        var colDbId = $(this).parent().parent().find(".db-id");
        localStorage.setItem("interfaceId", colDbId.text());
        localStorage.setItem("pid", "0");
        location.href ="respArg";
    });
    //编辑按钮
    $(document).on("click", ".btn-edit-interface", function () {
        var colDbId = $(this).parent().parent().find(".db-id");
        localStorage.setItem("edit-interface-id", colDbId.text());
        var methodId = $(this).parent().parent().find(".method-id").text();
        var name = $(this).parent().parent().find(".name").text();
        var note = $(this).parent().parent().find(".note").text();
        var path = $(this).parent().parent().find(".path").text();
        var version = localStorage.getItem("version");
        $("#et-interface-name-edit").val(name);
        $("#et-interface-version-edit").val(version);
        $("#et-interface-path-edit").val(path);
        $("#et-interface-note-edit").val(note);
        getHttpMethodsEdit(methodId);
    });

    //搜索按钮
    $("#btn-search").click(function () {
        var userId = localStorage.getItem("userId");
        var groupId = localStorage.getItem("groupId");
        getProjectList(groupId, userId, 1);
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
        var groupId = localStorage.getItem("groupId");
        getProjectList(groupId, userId, 1);
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
    $("#btn-add-interface").click(function () {
        $("#et-interface-note").val("");
        $("#et-interface-name").val("");
        $("#et-interface-path").val("");
        var version = localStorage.getItem("version");
        $("#et-interface-version").val(version);
        getHttpMethods("");
    });
    //添加对话框保存按钮
    $("#btn-add-save").click(function () {
        addResParam();
    });
    //编辑对话框保存按钮
    $("#btn-edit-save").click(function () {
        var interfaceId = localStorage.getItem("edit-interface-id");
        editResParam(interfaceId);
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

    $("#btn-translate").click(function () {
        var ch = $("#et-interface-name").val();
        translate(ch);
    });

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
function getHttpMethods(defValue) {
    $.get("/ZzApiDoc/v1/dictionary/getDictionary?type=method",
        function (data, status) {
            if (status === 'success') {
                if (data.code === 0) {
                    showHintMsg(data.msg);
                } else {
                    //填充选项
                    var sel = $("#select-interface-type");
                    sel.empty();
                    var firstOne = "";
                    var sameOne = "";
                    $.each(data.data, function (n, value) {
                        sel.append(
                            "<option value='"+value.id+"'>"+value.name+"</option>"
                        );
                        if(n === 0) {
                            firstOne = value.id;
                        }
                        if (defValue === value.id) {
                            sameOne = value.id;
                        }
                    });
                    sel.selectpicker('refresh');
                    if (sameOne === null || sameOne === "") {
                        sel.selectpicker('val', firstOne);
                    } else {
                        sel.selectpicker('val', sameOne);
                    }
                    sel.selectpicker('refresh');
                }
            }
        });

}
function getHttpMethodsEdit(defValue) {
    $.get("/ZzApiDoc/v1/dictionary/getDictionary?type=method",
        function (data, status) {
            if (status === 'success') {
                if (data.code === 0) {
                    showHintMsg(data.msg);
                } else {
                    //填充选项
                    var sel = $("#select-interface-type-edit");
                    sel.empty();
                    var firstOne = "";
                    var sameOne = "";
                    $.each(data.data, function (n, value) {
                        sel.append(
                            "<option value='"+value.id+"'>"+value.name+"</option>"
                        );
                        if(n === 0) {
                            firstOne = value.id;
                        }
                        if (defValue === value.id) {
                            sameOne = value.id;
                        }
                    });
                    if (sameOne === null || sameOne === "") {
                        sel.selectpicker('val', firstOne);
                    } else {
                        sel.selectpicker('val', sameOne);
                    }
                    sel.selectpicker('refresh');
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
                    var groupId = localStorage.getItem("groupId");
                    getProjectList(groupId, data.data.id, 1);
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
function getProjectList(groupId, userId, index) {
    if (userId === null || userId.length === 0) {
        $("#box-user-info").hide();
        $("#form-login").show();
        showHintMsg("登录已过期，请重新登录");
        return;
    }
    if (groupId === null || groupId.length === 0) {
        showHintMsg("请先选择分组");
        return;
    }
    var url = "/ZzApiDoc/v1/interface/getInterfaceByGroupIdWeb?groupId="+groupId+"&userId=" + userId + "&page=" + index;
    var search = $("#et-search").val();
    if (search === null || search.length === 0) {
    } else {
        url += ("&search="+search);
    }
    $.get(url, function (data, status) {
        if (status === 'success') {
            //填充表格
            var c = "";
            $.each(data.rows, function (n, value) {
                c += '<tr>' +
                    '<td><div class="checkbox"><input type="checkbox" id="checkbox' + n + '" class="styled"><label for="checkbox' + n + '">选择</label></div></td>' +
                    '<td class="db-id hide">' + value.id + '</td>' +
                    '<td class="method-id hide">' + value.methodId + '</td>' +
                    '<td class="name">' + value.name + '</td>' +
                    '<td>' + value.methodName + '</td>' +
                    '<td class="path">' + value.path + '</td>' +
                    '<td class="note">' + value.note + '</td>' +
                    '<td>' + value.createUserName + '</td>' +
                    '<td>' + value.createTime + '</td>' +
                    '<td><button type="button" class="btn-see-req btn">请求参数</button></td>' +
                    '<td><button type="button" class="btn-see-res btn">返回参数</button></td>' +
                    '<td><button type="button" class="btn-edit-interface btn btn-primary"  data-toggle="modal" data-target="#editModel">' +
                    '<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span> 编辑</button></td></tr>';
            });
            $("#project-list").html(c);

            // if($("#page-indicator").data("twbs-pagination")){
            //     $("#page-indicator").twbsPagination("destroy");
            // }
            $("#page-indicator").twbsPagination("destroy");
            //分页绑定
            $('#page-indicator').twbsPagination({
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
function justUpdateList(groupId, userId, index) {
    if (userId === null || userId.length === 0) {
        $("#box-user-info").hide();
        $("#form-login").show();
        showHintMsg("登录已过期，请重新登录");
        return;
    }
    if (groupId === null || groupId.length === 0) {
        showHintMsg("请先选择分组");
        return;
    }
    var url = "/ZzApiDoc/v1/interface/getInterfaceByGroupIdWeb?groupId="+groupId+"&userId=" + userId + "&page=" + index;
    var search = $("#et-search").val();
    if (search === null || search.length === 0) {
    } else {
        url.append("&search="+search);
    }
    $.get(url,
        function (data, status) {
            if (status === 'success') {
                //填充表格
                var c = "";
                $.each(data.rows, function (n, value) {
                    c += '<tr>' +
                        '<td><div class="checkbox"><input type="checkbox" id="checkbox' + n + '" class="styled"><label for="checkbox' + n + '">选择</label></div></td>' +
                        '<td class="db-id hide">' + value.id + '</td>' +
                        '<td class="method-id hide">' + value.methodId + '</td>' +
                        '<td class="name">' + value.name + '</td>' +
                        '<td>' + value.methodName + '</td>' +
                        '<td class="path">' + value.path + '</td>' +
                        '<td class="note">' + value.note + '</td>' +
                        '<td>' + value.createUserName + '</td>' +
                        '<td>' + value.createTime + '</td>' +
                        '<td><button type="button" class="btn-see-req btn">请求参数</button></td>' +
                        '<td><button type="button" class="btn-see-res btn">返回参数</button></td>' +
                        '<td><button type="button" class="btn-edit-interface btn btn-primary"  data-toggle="modal" data-target="#editModel">' +
                        '<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span> 编辑</button></td></tr>';
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
        showHintMsg("登录已过期，请重新登录");
        return;
    }
    $.post("/ZzApiDoc/v1/interface/deleteInterfaceWeb", {
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
                    var groupId = localStorage.getItem("groupId");
                    getProjectList(groupId, userId, 1);
                }
            }

        });
}

/**
 * 编辑接口
 */
function editResParam(interfaceId) {
    //暂存版本号
    var version = $("#et-interface-version-edit").val();
    localStorage.setItem("version", version);

    var note = $("#et-interface-note-edit").val();
    var name = $("#et-interface-name-edit").val();
    var path = $("#et-interface-path-edit").val();
    var httpMethodId = $("#select-interface-type-edit").val();
    var userId = localStorage.getItem("userId");
    var groupId = localStorage.getItem("groupId");
    var projectId = localStorage.getItem("projectId");
    $.post("/ZzApiDoc/v1/interface/updateInterface", {
            userId: userId,
            name: name,
            path: path,
            note: note,
            httpMethodId: httpMethodId,
            groupId: groupId,
            projectId: projectId,
            interfaceId: interfaceId
        },
        function (data, status) {
            if (status === 'success') {
                if (data.code === 0) {
                    //error msg
                    showHintMsg(data.msg);
                } else {
                    showOkMsg(data.msg);
                    //重新加载数据
                    var groupId = localStorage.getItem("groupId");
                    getProjectList(groupId, userId, 1);
                }
            }

        });
}

/**
 * 添加接口
 */
function addResParam() {
    var version = $("#et-interface-version").val();
    localStorage.setItem("version", version);
    var note = $("#et-interface-note").val();
    var name = $("#et-interface-name").val();
    var path = $("#et-interface-path").val();
    var httpMethodId = $("#select-interface-type").val();
    var userId = localStorage.getItem("userId");
    var groupId = localStorage.getItem("groupId");
    var projectId = localStorage.getItem("projectId");
    $.post("/ZzApiDoc/v1/interface/addInterface", {
            userId: userId,
            name: name,
            path: path,
            note: note,
            httpMethodId: httpMethodId,
            groupId: groupId,
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
                    var groupId = localStorage.getItem("groupId");
                    getProjectList(groupId, userId, 1);
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

function translate(query) {
    var appid = '2015063000000001';
    var key = '12345678';
    var salt = (new Date).getTime();
// 多个query可以用\n连接  如 query='apple\norange\nbanana\npear'
    var from = 'zh';
    var to = 'en';
    var str1 = appid + query + salt +key;
    var sign = MD5(str1);
    $.ajax({
        url: 'http://api.fanyi.baidu.com/api/trans/vip/translate',
        type: 'get',
        dataType: 'jsonp',
        data: {
            q: query,
            appid: appid,
            salt: salt,
            from: from,
            to: to,
            sign: sign
        },
        success: function (data) {
            // alert(JSON.stringify(data));
            var list= data.trans_result;
            if (list !== null && list.length > 0) {
                var obj = list[0];
                var en = obj.dst;
                if (en.indexOf(" ") === -1) {
                    var result = en.toLowerCase();
                    var version = $("#et-interface-version").val();
                    var method = $("#select-interface-type").find("option:selected").text();
                    $("#et-interface-path").val("v"+version+"/"+method.toUpperCase()+"/"+result);
                } else {
                    var words = en.split(' ')
                    var result = "";
                    for (var i = 0; i < words.length; i++) {
                        var word = words[i];
                        if (i === 0) {
                            result += (word.substring(0, 1).toLowerCase()+word.substring(1).toLowerCase());
                        } else {
                            result += (word.substring(0, 1).toUpperCase()+word.substring(1).toLowerCase());
                        }
                    }
                    result = result.replace("'", "");
                    var version = $("#et-interface-version").val();
                    var method = $("#select-interface-type").find("option:selected").text();
                    $("#et-interface-path").val("v"+version+"/"+method.toUpperCase()+"/"+result);
                }
            } else {
                alert("翻译失败");
            }
        }
    });
}