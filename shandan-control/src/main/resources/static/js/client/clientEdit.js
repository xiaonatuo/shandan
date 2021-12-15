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
        formId: 'clientForm',
        formInitUrl: `${ctx}/sys/oauth/client/get/${requestParam.id}`,
        formSubmitUrl: `${ctx}/sys/oauth/client/save`
    });




});

/**
 * 对父页面暴露save方法
 * @returns {*}
 */
function save(){
    return editPage.submit();
}