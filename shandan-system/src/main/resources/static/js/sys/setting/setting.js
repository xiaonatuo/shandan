layui.use(['form', 'gtable'], function () {
    let form = layui.form;//select、单选、复选等依赖form

    let modifyData = [];
    const table = layui.gtable.init({
        id: 'sys-setting-table',
        url: `${ctx}/sys/sysSetting/list`,
        method: 'post',
        toolbar: '#tableToolBar',
        height: '216',
        page: false,
        cols: [[
            {field: 'id', title: '系统标识'},
            {field: 'sysName', title: '系统名称', edit: 'text'},
            {field: 'sysAddress', title: '系统访问地址', edit: 'text'},
            {field: 'sysLogo', title: '系统logo', edit: 'text'},
            {field: 'userInitPassword', title: '初始密码'},
            {field: 'modifyTime', title: '上一次修改时间'}
        ]],
        onToolBarTable: function ({event, config}) {
            if (event == 'save') {
                save();
            } else if (event == 'clear') {

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
                await Util.post(`${ctx}/sys/sysSetting/save`, item).then(res => ok = res.flag).catch(err => {})
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
});

/**
 * 提交保存
 */
function sysFormSave() {
    let serializeObject = $("#sysForm").serializeObject();

}