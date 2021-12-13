layui.use(['form', 'layer'], function () {
    let form = layui.form;
    let layer = layui.layer;

    form.on("checkbox", function (elem) {
        let data = {clientId: $(elem.elem).data('clientId'), userId: $(elem.elem).data('userId')};
        $.post(`${ctx}/sys/userClient/save`, data, function (res) {
            if (!res.flag) {
                layer.msg('保存失败')
            } else {
                layer.msg('保存成功')
            }
        });
    });
})