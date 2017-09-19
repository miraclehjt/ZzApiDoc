<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>主页</title>
    <link rel="stylesheet" type="text/css" href="css/common.css"/>
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
    <link rel="stylesheet" type="text/css" href="themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="themes/icon.css">
    <script type="text/javascript">

        function getProject() {
            var userId = localStorage.userId;
            return "v1/project/getAllProjectWeb?userId=" + userId;
        }

        function getInterfaceGroup() {
            var userId = localStorage.userId;
            var projectId = $('#cc').combogrid('getValue');
            return "v1/interfaceGroup/getAllInterfaceGroupWeb?userId=" + userId + "&projectId=" + projectId;
        }

        function reloadGroup() {
            $('#gp').combogrid('grid').datagrid("options").url = getInterfaceGroup();
            $('#gp').combogrid('grid').datagrid('reload');
        }

        function getInterface() {
            var userId = localStorage.userId;
            var projectId = $('#cc').combogrid('getValue');
            var groupId = $('#gp').combogrid('getValue');
            return "v1/interface/getInterfaceByGroupIdWeb?userId=" + userId
                + "&projectId=" + projectId
                + "&groupId=" + groupId
                + "&_t=" + new Date().getTime();
        }

        function reloadInterface() {
//            $('#interf').datagrid("options").url = getInterface();
//            $('#interf').datagrid('reload');

            $('#interf').datagrid({url:getInterface()}).reload();

        }

        var url;

        function newInterface() {
            $('#dlg').dialog('open').dialog('center').dialog('setTitle', '添加接口');
            $('#fm').form('clear');
            url = 'v1/interface/addInterface';
        }

        function editInterface() {
            var row = $('#interf').datagrid('getSelected');
            if (row) {
                $('#dlg').dialog('open').dialog('center').dialog('setTitle', '编辑接口');
                $('#fm').form('load', row);
                url = 'v1/interface/updateInterface?interfaceId=' + row.id;
            }
        }

        function saveInterface() {
            $('#fm').form('submit', {
                url: url,
                onSubmit: function () {
                    return $(this).form('validate');
                },
                success: function (result) {
                    var r = JSON(result);
                    if (r.code == 0) {
                        $.messager.show({
                            title: 'Error',
                            msg: r.msg
                        });
                    } else {
                        $('#dlg').dialog('close'); // close the dialog
                        $('#interf').datagrid('reload'); // reload the user data
                    }
                }
            });
        }

        function deleteInterface() {
            var row = $('#interf').datagrid('getSelected');
            if (row) {
                $.messager.confirm('确认', '确定删除这个接口吗?', function (r) {
                    if (r) {
                        $.post('v1/interface/deleteInterface', {
                            userId: localStorage.userId,
                            id: row.id
                        }, function (result) {
                            var r = JSON(result);
                            if (r.code == 1) {
                                $('#interf').datagrid('reload'); // reload the user data
                            } else {
                                $.messager.show({ // show error message
                                    title: 'Error',
                                    msg: r.msg
                                });
                            }
                        }, 'json');
                    }
                });
            }
        }
    </script>
</head>

<body>
<div class="easyui-layout" style="width:700px;height:350px;">


    <div data-options="region:'west',split:true" title="项目和分组" style="width:150px;">

        <p id="projectMsg"></p>

        <div class="easyui-panel" style="width:100%;max-width:400px;padding:5px 5px;">
            <div style="margin-bottom:20px">
                <input id="cc" class="easyui-combogrid" style="width:100%" data-options="
					panelWidth: 500,
					idField: 'id',
					textField: 'name',
					url: getProject(),
					method: 'get',
					columns: [[
						{field:'name',title:'名称',width:180},
						{field:'note',title:'备注',width:120},
						{field:'interfaceNo',title:'接口数量',width:80,align:'right'},
						{field:'createUserName',title:'创建人',width:100}
					]],
					fitColumns: true,
					label: '选择项目 :',
					labelPosition: 'top'
				">
            </div>
        </div>

        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-reload" onclick="reloadGroup()"
           style="width:90px">刷新</a>

        <p id="errormsg"></p>

        <div class="easyui-panel" style="width:100%;max-width:400px;padding:5px 5px;">
            <div style="margin-bottom:20px">
                <input id="gp" class="easyui-combogrid" style="width:100%;" data-options="
					panelWidth: 500,
					idField: 'id',
					textField: 'name',
					url: getInterfaceGroup(),
					method: 'get',
					columns: [[
						{field:'name',title:'名称',width:120},
						{field:'ip',title:'IP地址',width:240},
						{field:'interfaceNo',title:'接口数量',width:40,align:'right'},
						{field:'createUserName',title:'创建人',width:100}
					]],
					fitColumns: true,
					label: '选择分组 :',
					labelPosition: 'top'
				">
            </div>
        </div>

    </div>
    <div data-options="region:'center',title:'接口管理',iconCls:'icon-ok'">

        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-reload" onclick="reloadInterface()"
           style="width:90px">刷新</a>

        <p id="interfaceMsg"></p>

        <table id="interf" title="我的接口" class="easyui-datagrid" style="width:700px;height:250px;"
               url="getInterface()" toolbar="#toolbar" pagination="true" rownumbers="true" fitColumns="true"
               singleSelect="true">
            <thead>
            <tr>
                <th field="name" width="100">名称</th>
                <th field="note" width="100">备注</th>
                <th field="createUserName" width="50">创建人</th>
            </tr>
            </thead>
        </table>
        <div id="toolbar">
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true"
               onclick="newInterface()">添加</a>
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true"
               onclick="editInterface()">编辑</a>
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true"
               onclick="deleteInterface()">删除</a>
        </div>

        <div id="dlg" class="easyui-dialog" style="width:400px" closed="true" buttons="#dlg-buttons">
            <form id="fm" method="post" novalidate style="margin:0;padding:20px 20px">
                <div style="margin-bottom:20px;font-size:14px;border-bottom:1px solid #ccc">接口信息</div>
                <div style="margin-bottom:10px">
                    <input name="name" class="easyui-textbox" required="true" label="名称:" style="width:100%">
                </div>
                <div style="margin-bottom:10px">
                    <input name="path" class="easyui-textbox" required="true" label="路径:" style="width:100%">
                </div>
                <div style="margin-bottom:10px">
                    <input name="note" class="easyui-textbox" required="true" label="备注:" style="width:100%">
                </div>
            </form>
        </div>
        <div id="dlg-buttons">
            <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="saveInterface()"
               style="width:90px">保存</a>
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel"
               onclick="javascript:$('#dlg').dialog('close')" style="width:90px">取消</a>
        </div>
    </div>
</div>

</body>

</html>