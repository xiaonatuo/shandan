/**
 * <p>
 *  通用编辑页面组件
 * </p>
 *
 * @author Administrator
 * @since 2021/5/24
 */
layui.define(['form', 'layer'], function (exports) {
    const form = layui.form,
        layer = layui.layer;
    let saveFlag = {done: false, ok: false};

    let options = {
        formId: '',
        formInitData: {},
        formInitUrl: '',
        formInitDone: undefined, // 初始化完成后的回调函数
        formSubmitUrl: '',
        formSubmitBtnId: 'btn_submit',
        formSubmitInvoke: undefined, // form表单提交时调用的函数
    };

    const setResult = function (res) {
        res.msg = res.flag ? '保存成功' : res.msg;
        let icon = res.flag ? 1 : 5;
        layer.msg(res.msg, {icon, time: 2000}, function () {
            saveFlag.done = true;
            saveFlag.ok = res.flag;
        });
    };

    const render = function() {
        form.val(options.formId, options.formInitData);
        if(options.formInitUrl){
            $.get(options.formInitUrl, function(res){
                if(res.flag){
                    form.val(options.formId, res.data);
                }else{
                    layer.msg('数据查询失败')
                }
                options.formInitDone && options.formInitDone(res.data)
            });
        }else if(options.formInitDone){
            options.formInitDone();
        }

        // 监听表单提交事件，阻止表单的方式提交，并使用post方法提交
        form.on(`submit(${options.formId})`, function (elem) {
            if(options.formSubmitInvoke){
                const recall = options.formSubmitInvoke();
                if(recall instanceof Promise){
                    recall.then(function(res){
                        setResult(res);
                    })
                }
            }else{
                let data = form.val(options.formId);
                $.post(options.formSubmitUrl, data, function (res) {
                    setResult(res);
                });
            }
            return false; //阻止表单跳转
        });
    }

    /**
     * 每隔100毫秒获取表单保存进度，如果保存请求完成则返回保存状态
     * @returns {Promise<boolean|boolean|*>}
     */
    const getSaveStatus = async function () {
        await commonUtil.sleep(100);
        if (saveFlag.done) {
            return saveFlag.ok;
        }
        return getSaveStatus();

    }

    const editPage = {};
    editPage.init = function(ops){
        options = Object.assign(options, ops);
        render();
    }

    /**
     * 提交表单
     * @returns {Promise<boolean|*>}
     */
    editPage.submit = function(){
        $(`#${options.formSubmitBtnId}`).click();
        return getSaveStatus();
    }

    /**
     * 获取表单数据
     */
    editPage.getData = function(){
        return form.val(`${options.formId}`);
    }

    exports('editPage', editPage);
});