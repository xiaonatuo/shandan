layui.use(['element'], function () {
    let element = layui.element;

    // 右侧用户导航栏不显示点击状态样式
    element.on('nav(user-nav)', function (elem) {
        elem.parent().removeClass('layui-this');
    });

    /**
     * 水平导航栏监听
     */
    element.on('nav(navbar-top)', function (elem) {
        let url = $(elem).data('url');
        $("#containerFrame").attr("src", url)
    });
    $('#navbar-top li:first a').click();
});