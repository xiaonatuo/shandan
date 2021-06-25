/**
 * 部门树组件封装
 *
 * @author GuoXin
 * @since 2021-05-19
 */
layui.define(['globalTree'], function (exports) {
    let globalTree = layui.globalTree;

    let options = {
        id: 'orgTree',
        url: `${ctx}/sys/org/tree`,
        response: {treeId: 'id', title: 'orgName',  parentId: 'orgParentId'},
        done: function (nodes, elem) {},
        onClick: function (node) {}
    }

    let orgTree = Object.assign({}, globalTree);
    orgTree.init = function (opt){
        options = Object.assign(options, opt);
        globalTree.init(options);
    }
    exports('orgTree', orgTree)
});