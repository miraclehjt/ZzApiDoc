<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>小周接口文档管理系统</title>
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="css/home.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="css/common.css"/>

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
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">小周接口文档管理系统</a>
        </div>
        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <li class="active"><a href="#">首页<span class="sr-only">(current)</span></a>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">功能<span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <li class="dropdown-header">业务功能</li>
                        <li><a href="#">信息建立</a>
                        </li>
                        <li><a href="#">信息查询</a>
                        </li>
                        <li><a href="#">信息管理</a>
                        </li>
                        <li role="separator" class="divider"></li>
                        <li class="dropdown-header">系统功能</li>
                        <li><a href="#">设置</a>
                        </li>
                    </ul>
                </li>
                <li><a href="#">帮助</a>
                </li>
            </ul>
            <div id="box-user-info" class="navbar-right">
                <label id="tv-user-name" class="navbar-right tv-white">周卓</label>
                <button id="btn-unregister" type="button" class="btn btn-danger">注销</button>
            </div>
            <form id="form-login" class="navbar-form navbar-right">
                <div class="form-group">
                    <input id="et-username" type="text" class="form-control" placeholder="用户名...">
                    <input id="et-password" class="form-control" placeholder="密码...">
                </div>
                <button id="btn-login" type="button" class="btn btn-default">登录</button>
            </form>


        </div>
        <!-- /.navbar-collapse -->
    </div>
    <!-- /.container-fluid -->
</nav>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-2" id="left-container">
            <p class="bg-primary">首页</p>
            <ul class="nav nav-pills nav-stacked">
                <li role="presentation"><a href="#">信息建立</a>
                </li>
                <li role="presentation"><a href="#">信息管理</a>
                </li>
                <li role="presentation"><a href="#">信息查询</a>
                </li>
                <li role="presentation" id="setting"><a href="#">设置</a>
                </li>
                <li role="presentation"><a href="#">帮助</a>
                </li>
            </ul>
        </div>
        <div class="col-md-10" id="right-container">
            <h2>管理控制台</h2>
            <hr/>
            <div id="btn-box">
                <!-- Standard button -->
                <button type="button" class="btn btn-default btn-lg">操作1</button>
                <!-- Provides extra visual weight and identifies the primary action in a set of buttons -->
                <button type="button" class="btn btn-primary btn-lg">操作2</button>
                <!-- Indicates a successful or positive action -->
                <button type="button" class="btn btn-success btn-lg">操作3</button>
                <!-- Contextual button for informational alert messages -->
                <button type="button" class="btn btn-info btn-lg">操作4</button>
                <!-- Indicates caution should be taken with this action -->
                <button type="button" class="btn btn-warning btn-lg">操作5</button>
                <!-- Indicates a dangerous or potentially negative action -->
                <button type="button" class="btn btn-danger btn-lg">操作6</button>
            </div>
            <div class="col-md-6 left-table" >
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title">最新提醒</h3>
                    </div>
                    <div class="panel-body">
                        <div class="alert alert-info" role="alert">
                            <strong>提示 </strong>您的订单（2014001）已被审核通过！ </div>
                        <div class="alert alert-warning" role="alert">
                            <strong>提示 </strong>您的订单（2014002）已被打回！ </div>
                        <div class="alert alert-danger" role="alert">
                            <strong>提示 </strong>您的订单（2013001）客户付款延时！ </div>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title">我的任务</h3>
                    </div>
                    <div class="panel-body">
                        <div class="alert alert-info" role="alert"> 订单审批<span class="badge">42</span>
                        </div>
                        <div class="alert alert-info" role="alert"> 收款确认<span class="badge">20</span>
                        </div>
                        <div class="alert alert-info" role="alert"> 付款确认<span class="badge">10</span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-6 left-table" >
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title">最新订单</h3>
                    </div>
                    <div class="panel-body">
                        <table class="table table-striped">
                            <thead>
                            <tr>
                                <th>#</th>
                                <th>产品</th>
                                <th>数量</th>
                                <th>金额</th>
                                <th>业务员</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>1</td>
                                <td>Apple Macbook air</td>
                                <td>10</td>
                                <td>80000</td>
                                <td>王小贱</td>
                            </tr>
                            <tr>
                                <td>2</td>
                                <td>Apple iPad air</td>
                                <td>20</td>
                                <td>65000</td>
                                <td>尹开花</td>
                            </tr>
                            <tr>
                                <td>3</td>
                                <td>Apple Macbook pro</td>
                                <td>5</td>
                                <td>50000</td>
                                <td>刘老根</td>
                            </tr>
                            </tbody>
                        </table>
                        <!-- Provides extra visual weight and identifies the primary action in a set of buttons -->
                        <button type="button" class="btn btn-primary">查看详情>></button>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title">工程进度</h3>
                    </div>
                    <div class="panel-body">
                        <span class="label label-primary pro-title">水井挖掘工程</span>
                        <div class="progress">
                            <div class="progress-bar" role="progressbar" aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: 60%;">
                                <span class="sr-only">60% Complete</span>
                            </div>
                        </div>
                        <span class="label label-danger pro-title">基建工程</span>
                        <div class="progress">
                            <div class="progress-bar progress-bar-danger" role="progressbar" aria-valuenow="80" aria-valuemin="0" aria-valuemax="100" style="width: 80%">
                                <span class="sr-only">80% Complete (danger)</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="js/jquery.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/home.js" type="text/javascript" charset="utf-8"></script>
</body>
</html>