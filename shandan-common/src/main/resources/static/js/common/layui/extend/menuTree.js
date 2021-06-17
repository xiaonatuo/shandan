/**
 * 菜单树组件封装
 *
 * @author GuoXin
 * @since 2021-05-19
 */
layui.define(['globalTree'], function (exports) {
    let globalTree = layui.globalTree;

    let options = {
        id: 'menuTree',
        url: `${ctx}/sys/menu/listByTier`,
        method: 'POST',
        response: {treeId: 'menuId', title: 'menuName', parentId: 'menuParentId'},
        done: function (nodes, elem) {},
        onClick: function (node) {}
    }

    // 继承globalTree的公共方法
    let menuTree = Object.assign({}, globalTree);
    menuTree.init = function (opt){
        options = Object.assign(options, opt);
        globalTree.init(options);
    }
    exports('menuTree', menuTree)
});