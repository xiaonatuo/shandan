let form;
layui.use(['element', 'form', 'table', 'layer', 'laydate', 'tree', 'util', 'dtree'], function () {
    let layDate = layui.laydate;
    form = layui.form;

    //日期选择器
    layDate.render({
        elem: '#expiredTimeDate',
        format: "yyyy-MM-dd HH:mm:ss"
    });
})