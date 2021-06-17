let editPage;
layui.use(['form','layer', 'editPage'], function () {
    editPage = layui.editPage;

    // 页面初始化
    editPage.init({
        formId:'menuForm',
        formInitData: menu,
        formSubmitUrl: `${ctx}/sys/menu/save`
    });
})


/**
 * 对父页面暴露save方法
 * @returns {*}
 */
function save() {
    return editPage.submit();
}
