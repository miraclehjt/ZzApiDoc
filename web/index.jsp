<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8"/>
    <title>登陆页面</title>
    <link rel="stylesheet" type="text/css" href="css/common.css"/>
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
    <link rel="stylesheet" type="text/css" href="themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="themes/icon.css">
    <script type="text/javascript">

        /*关闭对话框*/
        function closeDialog() {
            $('#dlg').dialog('close');
        }

        /*提交表单*/
        function submitForm() {
            $('#ff').form('submit', {
                url: "v1/user/userLogin",
                onSubmit: function () {
                    return $(this).form('enableValidation').form('validate');
                },
                success: function (data) {
                    var json = JSON.parse(data);
                    if (json.code == 1) {
                        localStorage.userId = json.data.id;
                        self.location = 'main.jsp';
                    } else {
                        showDialog(json.msg);
                    }
                }
            });
        }

        /*显示对话框*/
        function showDialog(msg) {
            $('#dlg').html(msg);
            $('#dlg').dialog('open');
        }

        /*清空表单*/
        function clearForm() {
            $('#ff').form('clear');
        }

        /*默认关闭对话框*/
        $(document).ready(function () {
            closeDialog();
        });
    </script>
</head>

<body>

<div class="easyui-panel" title="小周接口文档管理" style="width:100%;max-width:400px;padding:30px 60px;">
    <form id="ff" method="post">
        <div style="margin-bottom:20px">
            <input class="easyui-textbox" name="phone" style="width:100%" data-options="label:'手机号:',required:true">
        </div>
        <div style="margin-bottom:20px">
            <input class="easyui-passwordbox" name="password" style="width:100%" data-options="label:'密码:',required:true">
        </div>
    </form>
    <div style="text-align:center;padding:5px 0">
        <a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm()" style="width:80px">登陆</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" onclick="clearForm()" style="width:80px">重置</a>
    </div>

    <div id="dlg" class="easyui-dialog" title="登陆" style="width:400px;height:200px;padding:10px">
    </div>
</div>
</body>

</html>