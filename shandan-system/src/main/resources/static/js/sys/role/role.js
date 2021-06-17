// let tableIns;
// let tree;
layui.use(['form', 'menuTree', 'layer', 'gtable'], function () {
    let layer = layui.layer,
        gtable = layui.gtable,
        form = layui.form,
        menuTree = layui.menuTree;

    // 用于临时存储查询条件
    let searchText = '';
    let roleData = {};

    /**
     * 重置form表单数据
     */
    const restForm = function () {
        $('#roleForm')[0].reset();
        roleData = {};
        menuTree.reload({done: () => menuTree.chooseDataInit('')});
        form.render();
    }

    /**
     * 查询并渲染当前角色的菜单
     * @param roleId
     */
    const renderMenuData = function (roleId) {
        $.get(`${ctx}/sys/role/menus/${roleId}`, function (res) {
            if (res.flag) {
                menuTree.reload({
                    done: function () {
                        menuTree.chooseDataInit(res.data);
                    }
                })
            } else {
                layer.msg('角色菜单关系查询失败', {icon: 5})
            }
        });
    }

    /**
     * 查询并渲染当前角色的权限数据
     * @param roleId
     */
    const renderPermisData = function (roleId) {
        // 使用jquery方法后 layui的form表单渲染失败
        document.getElementsByName('permis').forEach(function(element){
            element.checked = false;
        });
        form.render('checkbox');
        $.get(`${ctx}/sys/role/permis/${roleId}`, function (res) {
            if (res.flag) {
                for (let rp of res.data) {
                    let element = document.getElementById(`${rp.permisId}`);
                    element.checked = true;
                    form.render('checkbox');
                }
            }
        });
    }

    /**
     * 删除角色
     * @param id
     * @param callback
     */
    const deleteRole = function (id, callback) {
        $.delete(`${ctx}/sys/role/delete/${id}`, {}, function (res) {
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
                restForm();
                break;
            case 'query':
                searchText = $('#searchKeyInput').val().trim();
                let queryOps = {page: {current: 1}}
                if (searchText) {
                    queryOps.where = {roleName: searchText}
                }
                gtable.reload(queryOps);
                break;
            case 'edit':
                roleData = rowData;
                form.val('roleForm', rowData)
                renderMenuData(rowData.roleId);
                renderPermisData(rowData.roleId);
                break;
            case 'delete':
                layer.confirm('确定要删除该菜单吗？', function (index) {
                    deleteRole(rowData.roleId, () => layer.close(index));
                });
                break;
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
        id: 'roleTable',
        url: ctx + '/sys/role/page',
        toolbar: '#tableToolBar',
        title: '角色列表',
        cols: [[
            {field: 'roleId', title: 'ID', hide: true},
            {field: 'roleName', title: '角色名称', width: '25%'},
            {field: 'roleRemark', title: '角色描述'},
            {fixed: 'right', title: '操作', toolbar: '#rowToolBar', align: 'center', width: '25%'}
        ]],
        onToolBarTable: tableEventCallback,
        onToolBarRow: tableEventCallback,
        done: tableDone
    };
    gtable.init(options);

    /**
     * 菜单树选项
     */
    let menuTreeOptions = {
        checkbar: true,
        done: function (nodes, elem) {
        },
        // 复选框回调配置
        checkbarFun: {
            // 选中前回调
            chooseBefore: function (obj) {
                if (!roleData.roleId) {
                    layer.msg('请先点击左侧数据编辑按钮后再进行该操作');
                    return false;
                }
                return true;
            },
            // 选中后回调
            chooseDone: function (obj) {
                let checkedIds = obj.map(o => o.id);
                let data = {roleId: roleData.roleId, menuIds: checkedIds.join(',')}
                $.post(`${ctx}/sys/role/save/menus`, data, function (res) {
                    if (!res.flag) {
                        layer.msg("保存失败")
                    }
                });
            }
        }
    }
    menuTree.init(menuTreeOptions);


    /**
     * 监听表单提交事件
     */
    form.on('submit(roleForm)', function (elem) {
        let data = form.val('roleForm');
        $.post(ctx + "/sys/role/save", data, function (res) {
            if (res.flag) {
                gtable.reload();
                restForm();
            }
            layer.msg(res.flag ? '保存成功' : '保存失败', {icon: res.flag ? 1 : 5, time: 2000});
        });
        return false;
    });

    form.on('checkbox(permisConfigForm)', function (data) {
        if (!roleData.roleId) {
            layer.msg('请先点击左侧数据编辑按钮后再进行该操作');
            data.elem.checked = false;
            form.render('checkbox');
            return;
        }
        const params = {roleId: roleData.roleId, permisId: data.value}
        $.post(`${ctx}/sys/role/save/permis`, params, function (res) {
            if (!res.flag) {
                layer.msg('保存失败', function () {
                    data.elem.checked = false;
                    form.render('checkbox');
                });
            }
        })
    });


});
