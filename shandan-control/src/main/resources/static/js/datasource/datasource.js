/**
 * <p>
 *  datasource.js
 * </p>
 *
 * @author Administrator
 * @since 2021/5/24
 */
layui.use(['layer', 'gtable'], function () {
    const layer = layui.layer,
        gtable = layui.gtable;
    // 用于临时存储查询条件
    let searchText = '';

    /**
     * 打开编辑窗口
     * @param id
     */
    let editLayerWin;
    const openEditLayer = function ( id = '') {
        layer.open({
            id: 'menuEdit',
            type: 2,
            title: '数据源编辑',
            area:['800px', '565px'],
            content: `${ctx}/control/datasource/edit?id=${id}`,
            btn: ['确定', '取消', '连接测试'],
            success: function (layero, index) {
                editLayerWin = window[layero.find('iframe')[0]['name']]
            },
            yes: function(index){
                editLayerWin && editLayerWin.save().then(ok => {
                    if (ok) {
                        gtable.reload();
                        layer.close(index);
                    }
                });
            },
            btn3: function () {
                editLayerWin && editLayerWin.connectTest();
                return false;
            },
            end: function(){}
        });
    }

    const deleteRow = function(id, callback){
        $.delete(`${ctx}/control/datasource/delete/${id}`, {}, function (res) {
            res.msg = res.flag ? '删除成功' : res.msg;
            let icon = res.flag ? 1 : 5;
            layer.msg(res.msg, {icon, time: 2000}, function () {
                if(res.flag){
                    gtable.reload();
                }
            });
            callback && callback();
        });
    }

    /**
     * 数据表格点击事件
     */
    const tableEventCallback = function(obj){
        let rowData = obj.data;
        switch (obj.event) {
            case 'add':
                openEditLayer();
                break;
            case 'query':
                searchText = $('#searchKeyInput').val().trim();
                let queryOps = {page: {current: 1}}
                if(searchText){
                    queryOps.where={name: searchText}
                }
                gtable.reload(queryOps);
                break;
            case 'edit':
                openEditLayer(rowData.id);
                break;
            case 'delete':
                layer.confirm('确定要删除该数据源吗？', function (index) {
                    deleteRow(rowData.id, ()=>{layer.close(index)});
                });
                break;
        }
    }

    const dsTableOptions = {
        id: 'dsTable',
        url: `${ctx}/control/datasource/page`,
        method: 'post',
        cols:[[
            {field: 'id', title: 'ID', hide: true},
            {field: 'name', title: '数据源名称'},
            {field: 'dbType', title: '数据库类型'},
            {field: 'host', title: '主机地址'},
            {field: 'port', title: '主机端口'},
            {field: 'jdbcSchema', title: '数据库模式'},
            {fixed: 'right', align:'center', title: '操作', toolbar: '#rowToolBar', width: 150}
        ]],
        onToolBarTable: tableEventCallback,
        onToolBarRow: tableEventCallback,
        done: function (res) {
            //console.info('数据表格加载完成', res);
            $('#searchKeyInput').val(searchText);
        }
    }
    gtable.init(dsTableOptions);
});