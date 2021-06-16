let userTable;
let tree;
layui.use(['element', 'form', 'table', 'layer', 'laydate', 'tree', 'gtable', 'orgTree'], function () {
    let table = layui.table;
    let form = layui.form;//select、单选、复选等依赖form
    let element = layui.element; //导航的hover效果、二级菜单等功能，需要依赖element模块
    tree = layui.tree;
    let orgTree = layui.orgTree;
    let layer = layui.layer;
    let gtable = layui.gtable;

    /**
     * 加载用户数据列表
     */
    let opstions = {
        id: 'userTable',
        url: ctx + '/sys/sysUser/page',
        toolbar: '#tableToolBar',
        title: '用户列表',
        cols: [[
            {field: 'userId', title: 'ID', hide: true}
            , {field: 'userName', title: '用户名称'}
            , {field: 'loginName', title: '登录名'}
            , {field: 'valid', title: '是否允许登录系统', hide: true}
            , {field: 'limitMultiLogin', title: '是否允许多人在线', hide: true}
            , {field: 'limitedIp', title: '限制允许登录的IP集合', hide: true}
            , {field: 'expiredTime', title: '账号失效时间', hide: true}
            , {field: 'lastChangePwdTime', title: '最近修改密码时间', hide: true}
            , {field: 'createTime', title: '创建时间', hide: true}
            , {field: 'modifyTime', title: '更新时间', hide: true}
            , {fixed: 'right', title: '操作', toolbar: '#userTableBarDemo'}
        ]],
        onToolBarTable: function (obj) {
            switch (obj.event) {
                case 'addData':
                    let param = orgTree.getNowParam();
                    if (param && param.id) {
                        openEditLayer(param.id, param.context);
                    } else {
                        layer.msg("请先在左侧选中要新增的组织机构！");
                    }
                    break;
                case 'query':
                    let queryByLoginName = $("#queryByLoginName").val();
                    let query = {
                        page: {
                            current: 1 //重新从第 1 页开始
                        }
                        , done: function (res, curr, count) {
                            //完成后重置where，解决下一次请求携带旧数据
                            // this.where = {};
                        }
                    };
                    if (!queryByLoginName) {
                        queryByLoginName = "";
                    }
                    //设定异步数据接口的额外参数
                    query.where = {loginName: queryByLoginName};
                    userTable.reload(query);
                    $("#queryByLoginName").val(queryByLoginName);
                    break;
                case 'reload':

                    break;
            }
        },
        onToolBarRow: function (obj) {
            let data = obj.data;
            switch (obj.event){
                case 'del':
                    layer.confirm('确认删除吗？', function (index) {
                        //向服务端发送删除指令
                        $.delete(ctx + "/sys/sysUser/delete/" + data.userId, {}, function (data) {
                            userTable.reload();
                            layer.close(index);
                        })
                    });
                    break;
                case 'edit':
                    openEditLayer("", "", data.userId);
                    break;
                case 'role':
                    openRoleLayer(data.userId);
                    break;
            }
        }
    };
    gtable.init(opstions);

    /**
     * 选择角色弹框
     * @param userId
     */
    function openRoleLayer(userId){
        let roleLayer;
        layer.open({
            id: 'roleLayer',
            type: 2,
            title: '选择角色',
            area:['300px', '330px'],
            content: ctx + `/sys/role/layer?userId=${userId}`,
            btn: ['确定'],
            success: function (layero, index) {
                roleLayer = window[layero.find('iframe')[0]['name']]
            },
            yes: function(index){
                //roleLayer && roleLayer.save();
                layer.close(index)
            },
            end: function(){
                //userTable.reload();
            }
        });
    }

    function openEditLayer(orgId, orgName, userId){
        if(!userId) {userId = ''}
        let editLayerWin;
        layer.open({
            id: 'userEdit',
            type: 2,
            title: '添加用户',
            area:['800px', '545px'],
            content: ctx + `/sys/sysUser/edit?orgId=${orgId}&orgName=${orgName}&userId=${userId}`,
            btn: ['确定', '取消'],
            success: function (layero, index) {
                editLayerWin = window[layero.find('iframe')[0]['name']]
            },
            yes: function(){
                editLayerWin && editLayerWin.save();
            },
            end: function(){
                gtable.reload();
            }
        });
    }

    /**
     * 加载机构树
     */
    function loadOrgTree() {
        orgTree.init({
            id: `orgTree`,
            done: function (nodes, elem){},
            onClick: function(obj){
                let query = {
                    page: {
                        curr: 1 //重新从第 1 页开始
                    }
                };
                //设定异步数据接口的额外参数
                query.where = {orgId: obj.param.id};
                gtable.reload(query);
            }
        })
    }
    loadOrgTree();
});


/**
 * 重置密码
 */
function resetPassword() {
    let userForm = $("#userForm").serializeObject();

    layer.confirm('确认重置该用户的密码吗？', function (index) {
        $.post(ctx + "/sys/sysUser/resetPassword", userForm, function (data) {
            if (data.flag) {
                layer.msg("密码重置成功，请尽快通知用户登录系统修改密码！", {icon: 1, time: 2000}, function () {
                });
            }
            layer.close(index);
        });
    });
}

