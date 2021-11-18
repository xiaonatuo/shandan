let saveFlag = {done: false, ok: false};
layui.use(['form'], function () {
    const form = layui.form;
    initDictTypeData();
    initDictStateCheckBox();
    /**
     * 初始化数据字典类型
     */
    function initDictTypeData() {
        Util.post('/sys/dict/type/list', {}).then(res => {
            if (res.flag) {
                renderDictTypeSelect(res.data)
            } else {
                showErrorMsg('数据字典类型初始化失败')
            }
        })
    }

    /**
     * 渲染数据字典类型下拉选择框
     */
    function renderDictTypeSelect(data) {
        let htm = '<option value="">直接选择或搜索选择</option>';
        for (let type of data) {
            htm += `<option value="${type.id}">${type.name}</option>`
        }
        $('#dictTypeSelect').html(htm);
        // 开始渲染
        form.val('dictForm', dict);
        form.render('select');
    }

    function initDictStateCheckBox(){
        if(dict.dictState == null){
            dict.dictState = true;
        }
        $('#dictStateBox').attr('checked',dict.dictState)

        form.on('switch(dictStateBox)', function(data){
            form.val('dictForm', {dictState:data.elem.checked});
        });
    }



    form.on('submit(dictForm)', function (elem) {
        let data = form.val('dictForm');
        $.post(ctx + "/sys/dict/save", data, function (data) {
            data.msg = data.flag ? '保存成功' : data.msg;
            let icon = data.flag ? 1 : 5;
            layer.msg(data.msg, {icon, time: 2000}, function () {
                saveFlag.done = true;
                saveFlag.ok = data.flag;
            });
        });
        return false; //阻止表单跳转
    });
});

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