let saveFlag = {};
layui.use(['element', 'form', 'table', 'layer', 'laydate', 'tree', 'util', 'dtree'], function () {
    let layDate = layui.laydate,
        form = layui.form;

    if (org) {
        form.val('orgForm', org);
    }

    /**
     * 同步请求验证部门编码是否存在
     * @param orgNumber
     * @returns {boolean}
     */
    const checkOrgNumber = function (orgNumber){
        let hasData = false;
        let data = form.val('orgForm');
        if(data.id){
            return false;
        }
        $.ajax({
            url: `${ctx}/sys/org/list`,
            data: {orgNumber},
            type: 'POST',
            async: false,
            success: function (res) {
                hasData = res.data && res.data.length > 0;
                saveFlag = hasData == true ? false :true;
            }
        })
        return hasData;
    }

    /**
     * 自定义表单验证规则
     */
    form.verify({
        orgNumber: function (value) {
            if (checkOrgNumber(value)) {
                return '部门编码已存在';
            }
        }
    });

    form.on('submit(orgForm)', function (elem) {
        let data = form.val('orgForm');
        $.post(ctx + "/sys/org/save", data, function (data) {
            data.msg = data.flag ? '保存成功' : data.msg;
            let icon = data.flag ? 1 : 5;
            layer.msg(data.msg, {icon, time: 2000}, function () {
                saveFlag = data.flag;
            });
        });
        return false; //阻止表单跳转
    });

    //日期选择器
    layDate.render({
        elem: '#expiredTimeDate',
        format: "yyyy-MM-dd HH:mm:ss"
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
    return saveFlag;
}

