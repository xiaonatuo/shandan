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




/**
 * 提交保存
 */
function save() {
    let userForm = $("#userForm").serializeObject();
    form.on('submit(userForm)', function(){
        $.post(ctx + "/sys/sysUser/save", userForm, function (data) {
            if (!data.flag) {
                layer.msg(data.msg, {icon: 2, time: 2000}, function () {
                });
                return;
            }
            layer.msg("保存成功", {icon: 1, time: 2000}, function () {
                let index = parent.layer.getFrameIndex(window.name);
                parent.layer.close(index);
            });
        });
        return false; //阻止表单跳转。使用下面的post方式提交
    });

    $("#btn_submit").click();
}