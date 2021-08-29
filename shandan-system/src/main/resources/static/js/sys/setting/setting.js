layui.use(['form', 'gtable', 'dropdown'], function () {
    let form = layui.form;//select、单选、复选等依赖form

    let modifyData = [];
    const table = layui.gtable.init({
        id: 'sys-setting-table',
        url: `${ctx}/sys/sysSetting/list`,
        method: 'post',
        toolbar: '#tableToolBar',
        height: 'full-125',
        page: false,
        cols: [[
            {field: 'id', title: '系统标识'},
            {field: 'sysName', title: '系统名称', edit: 'text'},
            {field: 'sysAddress', title: '系统访问地址', edit: 'text'},
            {field: 'sysLogo', title: '系统logo', edit: 'text', hide: true},
            {field: 'userInitPassword', title: '初始密码'},
            {field: 'modifyTime', title: '上一次修改时间'}
        ]],
        done: function () {
            dataClearBtnEventsBind();
        },
        onToolBarTable: function ({event, config}) {
            if (event == 'save') {
                save();
            }
        }

    })

    layui.gtable.on('edit(sys-setting-table)', function ({data}) {
        let exists = false;
        for (let i = 0; i < modifyData.length; i++) {
            if (modifyData[i].id == data.id) {
                modifyData[i] = data;
                exists = true;
                break;
            }
        }
        if (!exists) {
            modifyData.push(data)
        }

    });

    /**
     * 保存配置
     * @returns {Promise<void>}
     */
    async function save() {
        if (modifyData.length != 0) {
            let ok = false;
            for (let item of modifyData) {
                await Util.post(`/sys/sysSetting/save`, item).then(res => ok = res.flag).catch(err => {
                })
            }
            if (ok) {
                layer.msg('保存成功', {icon: 1, time: 1500})
            } else {
                layer.msg('保存失败', {icon: 5, time: 2000})
            }
        } else {
            layer.msg('未修改任何数据')
        }
    }

    /**
     * 数据清除按钮事件绑定
     */
    function dataClearBtnEventsBind() {
        layui.dropdown.render({
            elem: '#clear-btn',
            data: [{title: '清除', id: 'clear-dir'}, {title: '销毁', id: 'clear-all'}],
            click: function ({id}) {
                let tipMsg = '警告：该操作将删除所有分类编目数据，<br>且该操作不可逆，是否继续';
                if (id == 'clear-all') {
                    tipMsg = '警告：该操作将删除系统所有数据，<br>且该操作不可逆，是否继续';
                }
                layer.confirm(tipMsg, {icon: 7, area: ['350px', '200px']}, function (c_index) {
                    layer.close(c_index);
                    layer.prompt({title: '请输入超级管理员密码，以确认操作', formType: 1}, function (password, index) {
                        // 验证密码
                        Util.post(`/sys/sysSetting/pwd/verify`, {password}).then(res => { // todo 验证请求
                            if (res.flag) {
                                layer.close(index);
                                Util.post(`/sys/sysSetting/data/clear`, {type: id}).then(res => { // todo 清除请求
                                    if (res.flag) {
                                        layer.alert('操作成功');
                                    } else {
                                        layer.alert(res.msg);
                                    }
                                }).catch(error => showErrorMsg())
                            } else {
                                showErrorMsg('密码验证失败');
                            }
                        }).catch(error => showErrorMsg());
                    });
                });
            }
        });
    }

});