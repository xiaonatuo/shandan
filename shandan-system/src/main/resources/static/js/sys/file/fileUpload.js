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
layui.use(['layer', 'upload', 'element', 'form', 'laydate'], function () {
    let upload = layui.upload,
        element = layui.element,
        layer = layui.layer,
        form = layui.form,
        laydate = layui.laydate;

    let param = layui.url().search;

    let uploadListIns = upload.render({
        elem: '#chooseFile',
        elemList: $('#fileList'), //列表元素对象
        url: `${ctx}/sys/file/upload`,
        accept: 'file',
        multiple: true,
        auto: false,
        bindAction: '#fileUploadAction',
        choose: function (obj) {
            showLoading()
            let that = this;
            let files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
            let length = 0, curr = 0;
            for(let i in files){
                length++;
            }

            //读取本地文件
            obj.preview(function (index, file, result) {
                curr++;
                let tr = $(`<tr id="upload-${index}">
                                <td>${file.name}</td>
                                <td>${(file.size / 1024 / 1024).toFixed(1)}MB</td>
                                <td>
                                    <div style="width: 195px;padding-right: 5px;float: left;padding-top: 7px;">
                                        <div class="layui-progress" lay-filter="progress-file-u-${index}"><div class="layui-progress-bar" lay-percent=""></div></div>
                                    </div>
                                    <label class="success-label layui-hide">成功</label>
                                    <label class="fail-label layui-hide">失败</label>
                                </td>
                                <td style="text-align: right">
                                    <button class="layui-btn layui-btn-xs re-upload layui-hide">重传</button>
                                    <button class="layui-btn layui-btn-xs layui-btn-danger delete-file">删除</button>
                                </td>
                            </tr>`);

                //单个重传
                tr.find('.re-upload').on('click', function () {
                    obj.upload(index, file);
                });

                //删除
                tr.find('.delete-file').on('click', function () {
                    delete files[index]; //删除对应的文件
                    fileMap.delete(index);
                    tr.remove();
                    uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                });

                that.elemList.append(tr);
                element.render('progress'); //渲染新加的进度条组件

                if(curr == length){
                    closeLoading()
                }
            });
        },
        before: function(obj){
            this.data = getFormVal();
        },
        done: function (res, index, upload) { //成功的回调
            let that = this;
            if (res.flag) { //上传成功
                let tr = that.elemList.find('tr#upload-' + index), tds = tr.children();
                tds.eq(3).find('.re-upload').addClass('layui-hide');
                tds.eq(2).find('.success-label').removeClass('layui-hide');
                tds.eq(2).find('.fail-label').addClass('layui-hide');
                delete this.files[index]; //删除文件队列已经上传成功的文件
                //fileList[index] = res.data;
                fileMap.set(index, res.data);
                return;
            }
            layer.msg(res.msg);
            this.error(index, upload);
        },
        allDone: function (obj) { //多文件上传完毕后的状态回调
            if(obj.total === obj.successful){
                saveResult.success = true;
                saveResult.done = true;
                saveResult.data = this.data;

                let index = parent.layer.getFrameIndex(window.name);
                parent.layer.close(index);
                parent.layer.msg('上传成功');
            }
        },
        error: function (index, upload) { //错误回调
            let that = this;
            let tr = that.elemList.find('tr#upload-' + index), tds = tr.children();
            tds.eq(3).find('.re-upload').removeClass('layui-hide'); //显示重传
            tds.eq(2).find('.success-label').addClass('layui-hide');
            tds.eq(2).find('.fail-label').removeClass('layui-hide');
            element.progress('progress-file-u-' + index, '0%'); // 重置进度条
            saveResult.done = true;
        },
        progress: function (n, elem, e, index) { //注意：index 参数为 layui 2.6.6 新增
            element.progress('progress-file-u-' + index, n + '%'); //执行进度条。n 即为返回的进度百分比
        }
    });

    /**
     * 获取form表单数据
     * @returns {*}
     */
    function getFormVal(){
        let formVal = form.val('file-form');
        formVal.entityId = param.directoryId;
        formVal.inputDate = formVal.inputDate ? formVal.inputDate + '.000' : '';
        return formVal;
    }
//日期选择器
    laydate.render({
        elem: '#input-data',
        format: "yyyy-MM-dd HH:mm:ss",
        value: new Date(),
        isInitValue: true,
    });
});

function save(){
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