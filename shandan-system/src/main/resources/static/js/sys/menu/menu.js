let tree = {};
layui.use(['layer', 'gtable', 'menuTree'], function () {
    let gtable = layui.gtable,
        layer = layui.layer,
        menuTree = layui.menuTree;

    // 用于临时存储查询条件
    let searchText = '';

    /**
     * 检查是否有子菜单
     * @param menuId
     * @returns {boolean}
     */
    const checkChildMenu = async function (menuId) {
        console.info(menuId);
        let flag = false;
        await $.ajax({
            url: `${ctx}/sys/menu/list`,
            data: {menuParentId: menuId},
            type: 'POST',
            async: false,
            success: function (res) {
                flag = res.data && res.data.length > 0
            }
        });
        return flag;
    }

    /**
     * 删除菜单
     * @param id
     * @param callback
     */
    const deleteMenu = function (id, callback) {
        $.delete(`${ctx}/sys/menu/delete/${id}`, {}, function (res) {
            res.msg = res.flag ? '删除成功' : '删除失败';
            let icon = res.flag ? 1 : 5;
            layer.msg(res.msg, {icon, time: 2000}, function () {
                if (res.flag) {
                    menuTree.reload();
                    gtable.reload();
                }
            });
            callback && callback();
        });
    }

    /**
     * 打开菜单编辑窗口
     * @param parentId 父菜单
     * @param menuId 当前菜单
     */
    let editLayerWin;
    const openEditLayer = function (parentId = '', menuId = '') {
        layer.open({
            id: 'menuEdit',
            type: 2,
            title: '编辑菜单',
            area: ['600px', '380px'],
            content: ctx + `/sys/menu/edit?menuParentId=${parentId}&menuId=${menuId}`,
            btn: ['确定', '取消'],
            success: function (layero, index) {
                editLayerWin = window[layero.find('iframe')[0]['name']]
            },
            yes: function (index) {
                editLayerWin && editLayerWin.save().then(ok => {
                    if (ok) {
                        menuTree.reload();
                        gtable.reload();
                        layer.close(index);
                    }
                });
            },
            end: function () {
            }
        });
    }

    menuTree.init({
        // 点击树节点
        onClick: function (node) {
            const param = node.param;
            let queryOps = {where: {menuParentId: param.id}, page: {page: 1}};
            gtable.reload(queryOps);
        }
    });
    /**
     * 数据表格点击事件
     */
    const tableEventCallback = function (obj) {
        let rowData = obj.data;
        switch (obj.event) {
            case 'add':
                let treeNodeParam = menuTree.getNowParam();
                if (treeNodeParam && treeNodeParam.id) {
                    openEditLayer(treeNodeParam.id)
                } else {
                    layer.msg('请先选择左侧上级菜单')
                }
                break;
            case 'query':
                searchText = $('#searchKeyInput').val().trim();
                let queryOps = {page: {current: 1}}
                if (searchText) {
                    queryOps.where = {menuName: searchText}
                }
                gtable.reload(queryOps);
                break;
            case 'edit':
                openEditLayer('', rowData.menuId);
                break;
            case 'delete':
                checkChildMenu(rowData.menuId).then(flag => {
                    if (flag) {
                        layer.confirm('该菜单包含子菜单是否确定删除？', function (index) {
                            deleteMenu(rowData.menuId, () => layer.close(index));
                        })
                    } else {
                        layer.confirm('确定要删除该菜单吗？', function (index) {
                            deleteMenu(rowData.menuId, () => layer.close(index));
                        })
                    }
                });
                break;
        }
    }

    // 加载部门列表
    let options = {
        id: 'menuTable',
        url: ctx + '/sys/menu/page',
        toolbar: '#tableToolBar',
        title: '用户列表',
        cols: [[
            {field: 'menuId', title: 'ID', hide: true}
            , {field: 'menuName', title: '菜单名称'}
            , {field: 'menuPath', title: '菜单路径'}
            , {field: 'sortWeight', title: '同级排序权重'}
            , {fixed: 'right', title: '操作', toolbar: '#rowToolBar'}
        ]],
        onToolBarTable: tableEventCallback,
        onToolBarRow: tableEventCallback,
        done: function (res) {
            //console.info('数据表格加载完成', res);
            $('#searchKeyInput').val(searchText);
        }

    };
    gtable.init(options);
});
