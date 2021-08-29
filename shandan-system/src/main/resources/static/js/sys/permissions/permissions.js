layui.extend({
    permisConfig: `{/}${ctx}/js/sys/permissions/permisConfig` // {/}的意思即代表采用自有路径，即不跟随 base 路径
})
layui.use(['form', 'menuTree', 'layer', 'gtable', 'permisConfig'], function () {
    let layer = layui.layer,
        gtable = layui.gtable,
        form = layui.form;
    const permisConfLayer = layui.permisConfig.init()
    // 用于临时存储查询条件
    let searchText = '';

    /**
     * 打开编辑窗口
     * @param permisId 当前部门
     */
    let editLayerWin;
    const openEditLayer = function (permisId = '') {
        layer.open({
            id: 'permissionsEdit',
            type: 2,
            title: '权限编辑',
            area: ['650px', '450px'],
            content: ctx + `/sys/permissions/edit?permisId=${permisId}`,
            btn: ['确定', '取消'],
            success: function (layero, index) {
                editLayerWin = window[layero.find('iframe')[0]['name']]
            },
            yes: function (index) {
                editLayerWin && editLayerWin.save().then(ok => {
                    if (ok) {
                        gtable.reload();
                        layer.close(index);
                    }
                });
            },
            end: function(){}
        });
    }

    /**
     * 删除角色
     * @param id
     * @param callback
     */
    const deletePermissions = function (id, callback) {
        $.delete(`${ctx}/sys/permissions/delete/${id}`, {}, function (res) {
            res.msg = res.flag ? '删除成功' : '删除失败';
            let icon = res.flag ? 1 : 5;
            layer.msg(res.msg, {icon, time: 2000}, function () {
                if (res.flag) {
                    gtable.reload();
                }
            });
            callback && callback();
        });
    }

    /**
     * 数据表格点击事件
     */
    const tableEventCallback = function (obj) {
        let rowData = obj.data;
        switch (obj.event) {
            case 'add':
                openEditLayer();
                break;
            case 'query':
                searchText = $('#searchKeyInput').val().trim();
                let queryOps = {page: {current: 1}}
                if (searchText) {
                    queryOps.where = {permisName: searchText}
                }
                gtable.reload(queryOps);
                break;
            case 'edit':
                openEditLayer(rowData.permisId);
                break;
            case 'delete':
                layer.confirm('确定要删除该权限吗？', function (index) {
                    deletePermissions(rowData.permisId, () => layer.close(index));
                });
                break;
            case 'config':
                permisConfLayer.showLayer(rowData)
        }
    }
    /**
     * 数据表格加载完成回调
     * @param res
     */
    const tableDone = function (res) {
        //console.info('数据表格加载完成', res);
        $('#searchKeyInput').val(searchText);
    }

    // 加载角色列表
    let options = {
        id: 'permissionsTable',
        url: ctx + '/sys/permissions/page',
        toolbar: '#tableToolBar',
        title: '权限列表',
        cols: [[
            {field: 'permisId', title: '权限标识', width: 200, hide:true},
            {field: 'permisName', title: '权限名称', width: 250},
            {field: 'permisRemark', title: '权限描述'},
            {field: 'permisScopeRemark', title: '权限范围'},
            {field: 'hasSelect', title: '查询', width: 80, align: 'center', templet: data => data.hasSelect ? '是' : '否', hide:true},
            {field: 'hasAdd', title: '新增', width: 80, align: 'center', templet: data => data.hasAdd ? '是' : '否', hide:true},
            {field: 'hasEdit', title: '修改', width: 80, align: 'center', templet: data => data.hasEdit ? '是' : '否', hide:true},
            {field: 'hasDelete', title: '删除', width: 80, align: 'center', templet: data => data.hasDelete ? '是' : '否', hide:true},
            {fixed: 'right', title: '操作', toolbar: '#rowToolBar', align: 'right', width: 180}
        ]],
        onToolBarTable: tableEventCallback,
        onToolBarRow: tableEventCallback,
        done: tableDone
    };
    gtable.init(options);
});
