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
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
              data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
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
        <li class="active">
          <a href="#">首页<span class="sr-only">(current)</span></a>
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
        <li role="presentation">
          <a href="#">项目管理</a>
        </li>
        <li role="presentation">
          <a href="#">分组管理</a>
        </li>
        <li role="presentation">
          <a href="#">接口管理</a>
        </li>
        <li role="presentation">
          <a href="#">全局参数管理</a>
        </li>
        <li role="presentation">
          <a href="#">全局错误码管理</a>
        </li>

      </ul>
    </div>
    <div class="col-md-10" id="right-container">
      <h2 id="tv-content-title">项目管理</h2>
      <hr/>
      <div id="btn-box">
        <!-- Standard button -->
        <button type="button" class="btn btn-default">新增</button>
        <!-- Provides extra visual weight and identifies the primary action in a set of buttons -->
        <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#myModal">编辑</button>
        <!-- Indicates a dangerous or potentially negative action -->
        <button type="button" class="btn btn-danger">删除</button>
      </div>
      <div class="col-md-12 left-table">
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
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<script src="js/jquery.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/home.js" type="text/javascript" charset="utf-8"></script>

<!-- 编辑对话框 -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
        </button>
        <h4 class="modal-title" id="myModalLabel">Modal title</h4>
      </div>
      <div class="modal-body">
        ...
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary">Save changes</button>
      </div>
    </div>
  </div>
</div>

<%--错误提示--%>
<div class="alert alert-warning" id="tv-hint">
  <a href="#" class="close" data-dismiss="alert">
    &times;
  </a>
  <strong>警告！</strong><<label id="tv-hint-content">您的网络连接有问题。</label>>
</div>

</body>

</html>