/**
 * <p>
 *  文件上传
 * </p>
 *
 * @author Administrator
 * @since 2021/6/7
 */
let fileMap = new Map();
let saveResult = {
    done: false,
    success: false,
    data: null
};
layui.use(['layer', 'uploader', 'element', 'form', 'laydate'], function () {
    let form = layui.form,
        laydate = layui.laydate;

    let param = layui.url().search || {};
    bindDatetimePlugins();

    let uploader = layui.uploader.render({
        url: `${ctx}/sys/file/upload/chunk`,//上传文件服务器地址，必填
        fileCheckUrl: `${ctx}/sys/file/upload/check`,//文件校验地址
        fileBoxEle: "#uploader-table",//上传容器
        chooseFolder: true,
        getFormData: getFormVal,
        uploadFinished: function () {
            saveResult.done = true;
            saveResult.success = true;

            let index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
            parent.layer.msg('上传成功');
        }
    });

    $('#fileUploadAction').on('click', function () {
        uploader.uploadToServer();
    });

    /**
     * 获取form表单数据
     * @returns {*}
     */
    function getFormVal() {
        let formVal = form.val('file-form');
        formVal.entityId = param.directoryId;
        formVal.inputDate = formVal.inputDate ? formVal.inputDate + '.000' : '';
        return formVal;
    }


    /**
     * 绑定日期时间插件
     */
    function bindDatetimePlugins() {
        //日期选择器
        laydate.render({
            elem: '#input-data',
            format: "yyyy-MM-dd HH:mm:ss",
            value: new Date(),
            isInitValue: true,
        });
    }
});

function save() {
    $('#fileUploadAction').click();
    return getSaveStatus();
}

/**
 * 每隔100毫秒获取表单保存进度，如果保存请求完成则返回保存状态
 * @returns {Promise<boolean|boolean|*>}
 */
async function getSaveStatus() {
    await commonUtil.sleep(100);
    if (saveResult.done) {
        return saveResult;
    }
    return getSaveStatus();

}