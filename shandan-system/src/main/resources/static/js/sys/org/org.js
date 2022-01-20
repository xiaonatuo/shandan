layui.use(['layer', 'gtable', 'orgTree'], function () {
    let orgTree = layui.orgTree;
    let gtable = layui.gtable;

    // 用于临时存储查询条件
    let searchText = '';

    /**
     * 检查是否有子部门
     * @param orgParentId
     * @returns {boolean}
     */
    const checkChildOrg = function(orgParentId){
        let flag = false;
        $.ajax({
            url: `${ctx}/sys/org/list`,
            data: {orgParentId},
            type: 'POST',
            async: false,
            success: function (res) {
                flag = res.data && res.data.length
            }
        });
        return flag;
    }

    /**
     * 删除部门
     * @param id
     * @param callback
     */
    const deleteOrg = function(id, callback){
        $.delete(`${ctx}/sys/org/delete/${id}`, {}, function (res) {
            res.msg = res.flag ? '删除成功' : res.msg;
            let icon = res.flag ? 1 : 5;
            layer.msg(res.msg, {icon, time: 2000}, function () {
                if(res.flag){
                    orgTree.reload();
                    gtable.reload();
                }
            });
            callback && callback();
        });
    }

    /**
     * 打开部门编辑窗口
     * @param parentId 父部门
     * @param orgId 当前部门
     */
    let editLayerWin;
    const openEditLayer = function (parentId = '', orgId = '') {
        layer.open({
            id: 'orgEdit',
            type: 2,
            title: '编辑部门',
            area:['800px', '545px'],
            content: ctx + `/sys/org/edit?orgParentId=${parentId}&id=${orgId}`,
            btn: ['确定', '取消'],
            success: function (layero, index,data) {
                editLayerWin = window[layero.find('iframe')[0]['name']]
            },
            yes: function(index){
                editLayerWin && editLayerWin.save().then(ok => {
                    console.log(ok);
                    if (ok) {
                        orgTree.reload();
                        gtable.reload();
                        layer.close(index);
                    }
                });
            },
            end: function(){}
        });
    }

    /**
     * 数据表格点击事件
     */
    const tableEventCallback = function(obj){
        let rowData = obj.data;
        switch (obj.event) {
            case 'add':
                let treeNodeParam = orgTree.getNowParam();
                if (treeNodeParam && treeNodeParam.id) {
                    openEditLayer(treeNodeParam.id)
                } else {
                    layer.msg('请先选择左侧上级部门')
                }
                break;
            case 'query':
                searchText = $('#searchKeyInput').val().trim();
                let queryOps = {page: {current: 1}}
                if(searchText){
                    queryOps.where={orgName: searchText}
                }
                gtable.reload(queryOps);
                break;
            case 'edit':
                openEditLayer('', rowData.id);
                break;
            case 'delete':
                layer.confirm('确定要删除该部门吗？', function (index) {
                    if(checkChildOrg(rowData.id)){
                        layer.close(index);
                        layer.confirm('该部门包含子部门是否确定删除？', function (num) {
                            deleteOrg(rowData.id, () => layer.close(num));
                        })
                    }else{
                        deleteOrg(rowData.id, () => layer.close(index));
                    }
                });
                break;
        }
    }

    //加载部门树
    orgTree.init({
        // 点击树节点回显数据到表单
        onClick: function (node) {
            const param = node.param;
            let queryOps = {where: {orgParentId: param.id}, page: {page: 1}};
            gtable.reload(queryOps);
        }
    });

    // 加载部门列表
    let options = {
        id: 'orgTable',
        url: ctx + '/sys/org/page',
        toolbar: '#tableToolBar',
        title: '用户列表',
        cols: [[
            {field: 'id', title: 'ID', hide: true}
            , {field: 'orgNumber', title: '部门编号'}
            , {field: 'orgName', title: '部门名称'}
            , {field: 'orgShortName', title: '部门简称'}
            , {field: 'orgParentName', title: '上级部门'}
            , {field: 'leaderName', title: '部门负责人'}
            , {fixed: 'right', title: '操作', toolbar: '#rowToolBar', width: 150, align:'center'}
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