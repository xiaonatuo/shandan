layui.use(['form', 'layer'], function(){
    let form = layui.form;
    let layer = layui.layer;

    form.on("checkbox", function (elem) {
        let data = {roleId: $(elem.elem).data('roleId'), userId: $(elem.elem).data('userId')};
        $.post(`${ctx}/sys/userRole/save`, data, function (res) {
            if(!res.flag){
                layer.msg('保存失败')
            }
        });
    });
})