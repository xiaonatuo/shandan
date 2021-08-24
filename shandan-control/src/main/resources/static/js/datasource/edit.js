/**
 * <p>
 *  edit.js
 * </p>
 *
 * @author Administrator
 * @since 2021/5/24
 */
let editPage;
let layer;
layui.use(['form', 'layer', 'editPage'], function () {
    editPage = layui.editPage;
    layer = layui.layer;
    // 获取请求参数
    const requestParam = layui.url().search;

    editPage.init({
        formId: 'datasourceForm',
        formInitUrl: `${ctx}/control/datasource/get/${requestParam.id}`,
        formSubmitUrl: `${ctx}/control/datasource/save`
    });
});

/**
 * 对父页面暴露save方法
 * @returns {*}
 */
function save(){
    return editPage.submit();
}

/**
 * 测试连接
 */
function connectTest(){
    let data = editPage.getData();
    $.post(`${ctx}/control/datasource/connection/test`, data, function (res) {
        if(res.flag){
            layer.msg('连接成功');
        }else{
            layer.msg(res.msg);
        }
    });
}