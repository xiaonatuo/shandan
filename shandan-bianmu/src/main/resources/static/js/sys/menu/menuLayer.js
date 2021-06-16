layui.use(['layer', 'menuTree'], function(){
    let layer = layui.layer;
    let menuTree = layui.menuTree;

    menuTree.init({
        checkbar: true,
        done: function(nodes, elem){
            // 选中数据
            if(userMenuIds.length > 0){
                menuTree.chooseDataInit(userMenuIds);
            }
        },
        // 复选框回调配置
        checkbarFun: {
            // 选中前回调
            chooseBefore: function (obj) {
                return true;
            },
            // 选中后回调
            chooseDone: function(obj) {

            }
        }
    })
})