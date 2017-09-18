<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title></title>
    <link rel="stylesheet" type="text/css" href="css/common.css"/>
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
    <link rel="stylesheet" type="text/css" href="themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="themes/icon.css">
    <script type="text/javascript">
        function getValue() {
            var val = $('#cc').combogrid('getValue');
            alert(val);
        }

        function getProject() {
            var userId = localStorage.userId;
            return "v1/project/getAllProject?userId=" + userId;
        }
    </script>
</head>

<body>

<div style="padding:5px 0;">
    <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add'" style="width:30%;max-width:200px;">Add</a>
    <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-remove'"
       style="width:30%;max-width:200px;">Remove</a>
</div>
<div class="easyui-panel" style="width:100%;max-width:400px;padding:30px 60px;">
    <div style="margin-bottom:20px">
        <input id="cc" class="easyui-combogrid" style="width:100%" data-options="
					panelWidth: 500,
					idField: 'id',
					textField: 'name',
					url: getProject(),
					method: 'get',
					columns: [[
						{field:'name',title:'name',width:80},
						{field:'note',title:'note',width:120},
						{field:'property',title:'property',width:80,align:'right'},
						{field:'interfaceNo',title:'interfaceNo',width:80,align:'right'},
						{field:'createUserName',title:'createUserName',width:200},
						{field:'createTime',title:'createTime',width:60,align:'center'}
					]],
					fitColumns: true,
					label: '选择项目 :',
					labelPosition: 'right'
				">
    </div>
</div>

<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-save'" style="width:30%;max-width:133px;">Save</a>

</body>

</html>