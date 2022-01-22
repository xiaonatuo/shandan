let saveFlag = {done: false, ok: false};
layui.use(['form', 'layer'], function () {
    let form = layui.form;

    if (permisVo) {
        form.val('permissionsForm', permisVo);
    }

    /**
     * 自定义表单验证规则
     */
    form.verify({
        permisName: function (value) {
            if (checkPermisName(value)) {
                return '权限名称已存在';
            }
        },
        permisScope: function (value) {
            if (null == value) {
                return '请选择数据权限范围';
            }
        }
    });

    /**
     * 同步请求验证权限名称是否存在
     * @param orgNumber
     * @returns {boolean}
     */
    const checkPermisName = function (permisName){
        let hasData = false;
        let data = form.val('permissionsForm');
        if(data.id){
            return false;
        }
        $.ajax({
            url: `${ctx}/sys/permissions/list`,
            data: {permisName},
            type: 'POST',
            async: false,
            success: function (res) {
                hasData = res.data && res.data.length > 0;
                saveFlag.ok = hasData == true ? false :true;
            }
        })
        return hasData;
    }


    form.on('submit(permissionsForm)', function (elem) {
        let data = form.val('permissionsForm');
        data.hasSelect = data.hasSelect || false;
        data.hasAdd = data.hasAdd || false;
        data.hasEdit = data.hasEdit || false;
        data.hasDelete = data.hasDelete || false;
        $.post(ctx + "/sys/permissions/save", data, function (data) {
            data.msg = data.flag ? '保存成功' : data.msg;
            let icon = data.flag ? 1 : 5;
            layer.msg(data.msg, {icon, time: 1500}, function () {
                saveFlag.done = true;
                saveFlag.ok = data.flag;
            });
        });
        return false; //阻止表单跳转
    });
})


/**
 * 提交保存
 */
function save() {
    $("#btn_submit").click();
    return getSaveStatus();
}

/**
 * 每隔100毫秒获取表单保存进度，如果保存请求完成则返回保存状态
 * @returns {Promise<boolean|boolean|*>}
 */
async function getSaveStatus() {
    await Util.sleep(100);
    if (saveFlag.done) {
        return saveFlag.ok;
    }
    return getSaveStatus();
}