<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-cn">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>小周接口文档管理系统</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/common.css"/>
    <link rel="stylesheet" href="css/awesome-bootstrap-checkbox.css">
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <link rel="stylesheet" href="css/bootstrap-select.min.css">

    <!--[if lt IE 9]>
    <script src="js/html5shiv.js"></script>
    <script src="js/respond.min.js"></script>
    <![endif]-->
</head>

<body>
<nav class="navbar navbar-inverse">
    <div class="container-fluid">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
                    data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="home">小周接口文档管理系统
                <img class="logo pull-left" src="img/web_logo.png">
            </a>
        </div>
        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <form id="form-search" class="navbar-form col-md-5">
                <div class="input-group">
                    <input id="et-search" type="text" class="form-control" placeholder="名称,路径,备注...">
                    <span class="input-group-btn">
                        <button id="btn-search" class="btn btn-primary" type="button">搜索</button>
                    </span>
                </div><!-- /input-group -->
            </form>

            <div id="box-user-info" class="navbar-right">
                <a id="tv-user-name" class="tv-white btn-link" data-toggle="modal" data-target="#userModel"></a>
                <button id="btn-unregister" type="button" class="btn btn-danger">
                    <span class="glyphicon glyphicon-off" aria-hidden="true"></span> 注销
                </button>
            </div>
            <form id="form-login" class="navbar-form navbar-right">
                <div class="form-group">
                    <input id="et-username" type="text" class="form-control" placeholder="手机号...">
                    <input id="et-password" type="password" class="form-control" placeholder="密码...">
                </div>
                <button id="btn-login" type="button" class="btn btn-default">登录</button>
            </form>

        </div>
        <!-- /.navbar-collapse -->
    </div>
    <!-- /.container-fluid -->
</nav>
<%--错误提示--%>
<div class="container-fluid">
    <div class="row" id="row-hint">

    </div>
    <div class="row">
        <div class="col-md-12" id="right-container">
            <div class="span6">
                <ul class="breadcrumb">
                    <li>
                        <a href="home">项目管理</a> <span class="divider"></span>
                    </li>
                    <li>
                        <a href="group">分组管理</a> <span class="divider"></span>
                    </li>
                    <li class="active">接口管理</li>
                </ul>
            </div>
            <hr/>
            <div id="btn-box">
                <!-- Standard button -->
                <button type="button" class="btn btn-primary" id="btn-refresh">
                    <span class="glyphicon glyphicon-repeat" aria-hidden="true"></span> 刷新
                </button>
                <!-- Standard button -->
                <button type="button" class="btn btn-primary" id="btn-add-interface" data-toggle="modal"
                        data-target="#addModel">
                    <span class="glyphicon glyphicon-plus" aria-hidden="true"></span> 新增
                </button>
                <!-- Indicates a dangerous or potentially negative action -->
                <button type="button" class="btn btn-danger" id="btn-delete">
                    <span class="glyphicon glyphicon-trash" aria-hidden="true"></span> 删除
                </button>
            </div>
            <div class="col-md-12 left-table">
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title" id="table-title">接口管理</h3>
                    </div>
                    <div class="panel-body">
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead>
                                <tr>
                                    <th>
                                        <div class="checkbox">
                                            <input type="checkbox" id="checkbox-all" class="styled">
                                            <label for="checkbox-all">
                                                全选
                                            </label>
                                        </div>
                                    </th>
                                    <th class="hide">ID</th>
                                    <th class="hide">methodId</th>
                                    <th>名称</th>
                                    <th>请求方式</th>
                                    <th>请求路径</th>
                                    <th>备注</th>
                                    <th>创建人</th>
                                    <th>创建时间</th>
                                    <th>请求参数</th>
                                    <th>返回参数</th>
                                    <th>操作</th>
                                </tr>
                                </thead>
                                <tbody id="project-list">
                                <%--js add content--%>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <ul class="pagination" id="page-indicator"></ul>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="js/jquery.min.js" type="text/javascript" charset="utf-8"></script>
<script src="js/bootstrap.min.js" type="text/javascript" charset="utf-8"></script>
<script src="js/jquery.twbsPagination.min.js" type="text/javascript" charset="utf-8"></script>
<script src="js/interf.js" type="text/javascript" charset="utf-8"></script>
<script src="js/bootstrap-select.min.js" type="text/javascript" charset="utf-8"></script>
<script src="js/bootstrap-show-password.min.js" type="text/javascript" charset="utf-8"></script>
<script src="js/md5.js" type="text/javascript" charset="utf-8"></script>

<!-- 编辑对话框 -->
<div class="modal fade" id="editModel" tabindex="-1" role="dialog" aria-labelledby="editTitle">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="editTitle">编辑接口</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal" id="form-edit">
                    <div class="form-group form-group-sm">
                        <label class="col-sm-2 control-label" for="select-interface-type-edit">参数类型</label>
                        <div class="col-sm-10">
                            <select class="selectpicker" id="select-interface-type-edit">
                                <option value="0">GET</option>
                                <option value="1">POST</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group form-group-sm">
                        <label class="col-sm-2 control-label" for="et-interface-name-edit">名称</label>
                        <div class="col-sm-10">
                            <input class="form-control" type="text" id="et-interface-name-edit" placeholder="名称">
                        </div>
                    </div>
                    <div class="form-group form-group-sm">
                        <label class="col-sm-2 control-label" for="et-interface-version-edit">版本号</label>
                        <div class="col-sm-10">
                            <input class="form-control" type="text" id="et-interface-version-edit" placeholder="版本号">
                        </div>
                    </div>
                    <div class="form-group form-group-sm">
                        <label class="col-sm-2 control-label" for="et-interface-path-edit">请求路径</label>
                        <div class="col-sm-10">
                            <input class="form-control" type="text" id="et-interface-path-edit" placeholder="请求路径">
                        </div>
                    </div>
                    <div class="form-group form-group-sm">
                        <label class="col-sm-2 control-label" for="et-interface-note-edit">备注</label>
                        <div class="col-sm-10">
                            <input class="form-control" type="text" id="et-interface-note-edit" placeholder="备注">
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" data-dismiss="modal" id="btn-edit-save">保存</button>
            </div>
        </div>
    </div>
</div>

<!-- 新增对话框 -->
<div class="modal fade" id="addModel" tabindex="-1" role="dialog" aria-labelledby="addTitle">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="addTitle">新增接口</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal" id="form-add">
                    <div class="form-group form-group-sm">
                        <label class="col-sm-2 control-label" for="select-interface-type">参数类型</label>
                        <div class="col-sm-10">
                            <select class="selectpicker" id="select-interface-type">
                                <option value="0">GET</option>
                                <option value="1">POST</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group form-group-sm">
                        <label class="col-sm-2 control-label" for="et-interface-name">名称</label>
                        <div class="col-sm-10">
                            <input class="form-control" type="text" id="et-interface-name" placeholder="名称">
                        </div>
                    </div>
                    <div class="form-group form-group-sm">
                        <label class="col-sm-2 control-label" for="et-interface-version">版本号</label>
                        <div class="col-sm-10">
                            <input class="form-control" type="text" id="et-interface-version" placeholder="版本号">
                        </div>
                    </div>
                    <div class="form-group form-group-sm">
                        <label class="col-sm-2 control-label" for="et-interface-path">请求路径</label>
                        <div class="col-sm-10">
                            <div class="input-group">
                                <input class="form-control" type="text" id="et-interface-path" placeholder="请求路径">
                                <span class="input-group-btn">
                                    <button id="btn-translate" class="btn btn-primary btn-sm" type="button">自动生成</button>
                                </span>
                            </div>
                        </div>
                    </div>
                    <div class="form-group form-group-sm">
                        <label class="col-sm-2 control-label" for="et-interface-note">备注</label>
                        <div class="col-sm-10">
                            <input class="form-control" type="text" id="et-interface-note" placeholder="备注">
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" data-dismiss="modal" id="btn-add-save">保存</button>
            </div>
        </div>
    </div>
</div>
<!-- 用户信息对话框 -->
<div class="modal fade" id="userModel" tabindex="-1" role="dialog" aria-labelledby="userTitle">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="userTitle">用户信息编辑</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal" id="form-edit-user">
                    <div class="form-group form-group-sm">
                        <label class="col-sm-2 control-label" for="et-phone-edit">手机号</label>
                        <div class="col-sm-10">
                            <input class="form-control" type="text" id="et-phone-edit" placeholder="手机号">
                        </div>
                    </div>
                    <div class="form-group form-group-sm">
                        <label class="col-sm-2 control-label" for="et-pswd-edit">原密码</label>
                        <div class="col-sm-10">
                            <input class="form-control" type="password" id="et-pswd-edit" placeholder="原密码">
                        </div>
                    </div>
                    <div class="form-group form-group-sm">
                        <label class="col-sm-2 control-label" for="et-new-pswd-edit">新密码</label>
                        <div class="col-sm-10">
                            <input class="form-control" type="password" id="et-new-pswd-edit" placeholder="新密码">
                        </div>
                    </div>
                    <div class="form-group form-group-sm">
                        <label class="col-sm-2 control-label" for="et-user-name-edit">姓名</label>
                        <div class="col-sm-10">
                            <input class="form-control" type="text" id="et-user-name-edit" placeholder="姓名">
                        </div>
                    </div>
                    <div class="form-group form-group-sm">
                        <label class="col-sm-2 control-label" for="et-email-edit">邮箱</label>
                        <div class="col-sm-10">
                            <input class="form-control" type="email" id="et-email-edit" placeholder="邮箱">
                        </div>
                    </div>
                    <div class="form-group form-group-sm">
                        <label class="col-sm-2 control-label" for="select-sex-edit">性别</label>
                        <div class="col-sm-10">
                            <select class="selectpicker" id="select-sex-edit">
                                <option value="男">男</option>
                                <option value="女">女</option>
                            </select>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" data-dismiss="modal" id="btn-user-ok">确定</button>
            </div>
        </div>
    </div>
</div>

</body>

</html>