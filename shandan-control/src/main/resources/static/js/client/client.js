/**
 * <p>
 *  client.js
 * </p>
 *
 * @author Administrator
 * @since 2021/12/11
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
            title: '客户端编辑',
            area:['600px', '550px'],
            content: `${ctx}/sys/oauth/client/edit?id=${id}`,
            btn: ['确定', '取消'],
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
        $.delete(`${ctx}/sys/oauth/client/delete/${id}`, {}, function (res) {
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
                    queryOps.where={id: searchText}
                }
                if(''==searchText){
                    queryOps.where={id: null}
                }
                gtable.reload(queryOps);
                break;
            case 'edit':
                openEditLayer(rowData.id);
                break;
            case 'delete':
                layer.confirm('确定要删除该条数据吗？', function (index) {
                    deleteRow(rowData.id, ()=>{layer.close(index)});
                });
                break;
        }
    }

    const dsTableOptions = {
        id: 'dsTable',
        url: `${ctx}/sys/oauth/client/page`,
        method: 'post',
        cols:[[
            {field: 'id', title: '客户端名称',width: 150},
            {field: 'clientSecret', title: '访问密匙'},
            //{field: 'resourceIds', title: '资源ID集合'},
            //{field: 'scope', title: '权限范围'},
            {
                field: 'scope',
                title: '权限范围',
                width: 80,
                align: 'center',
                templet: data => data.scope == 'all' ? '全部' : '部分'
            },
            {field: 'authorizedGrantTypes', title: '访问模式'},
            {field: 'webServerRedirectUri', title: '客户端访问地址'},
            {field: 'authorities', title: '权限'},
            {field: 'accessTokenValidity', title: '访问令牌时效', width: 100},
            {field: 'refreshTokenValidity', title: '刷新令牌时效', width: 100,},
            //{field: 'additionalInformation', title: ''},
            //{field: 'autoapprove', title: '许可'},
            {
                field: 'autoapprove',
                title: '状态',
                width: 80,
                align: 'center',
                templet: data => data.autoapprove ? '启用' : '停用'
            },
            //{field: 'webClientLogoutUri', title: '数据库模式'},
            {fixed: 'right', align:'center', title: '操作', toolbar: '#rowToolBar', width: 150}
        ]],
        onToolBarTable: tableEventCallback,
        onToolBarRow: tableEventCallback,
        done: function (res) {
            $('#searchKeyInput').val(searchText);
        }
    }
    gtable.init(dsTableOptions);
});