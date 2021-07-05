/**
 * 通用树组件封装
 *
 * @author GuoXin
 * @since 2021-05-19
 */
layui.define(['jquery', 'layer', 'dtree'], function (exports) {
    let dtree = layui.dtree;
    let tree;
    let dtreeObj;

    const GlobalTree = function (options) {
        this.id = 'tree';
        this.url = '';
        this.data = undefined;
        this.method = 'GET';
        this.type = 'load';
        this.width = '100%';
        this.ficon = ["1", "-1"];
        this.initLevel = 2;
        this.record = true;
        this.dataStyle = 'layuiStyle';
        this.accordion = false;
        this.defaultRequest = {nodeId: 'id'};
        this.rootNode = {id: '', title: '', parentId: '0', children: []};
        this.response = {
            statusName: 'flag',
            statusCode: true,
            message: 'msg',
            treeId: 'id',
            rootName: 'data',
            parentId: 'parentId',
            title: "title",
            checkArr: 'checked'
        };
        this.done = undefined; // function (nodes, elem) {};
        this.onClick = undefined; // function (node) {}
        this.onDbClick = undefined;
        this.render(options);
    }

    /**
     * 设置参数并渲染组件
     * @param options
     */
    GlobalTree.prototype.render = function (options = {}) {
        const _this = $.extend(true, this, options);
        _this.request = options.request || {};
        const _done = _this.done;
        _this.done = function (nodes, elem) {
            // 节点单击事件
            dtree.on(`node('${_this.id}')`, function (node) {
                _this.onClick && _this.onClick(node);

            });
            // 节点双击事件
            dtree.on(`nodedblclick('${_this.id}')` ,function(obj){
                _this.onDbClick && _this.onDbClick(obj);
            });
            _done && _done(nodes, elem);
        };

        _this.elem = `#${_this.id}`;

        dtreeObj = dtree.render(_this);
    }

    /**
     * 重新加载
     * @param options
     */
    GlobalTree.prototype.reload = function (options) {
        this.render(options);
    }

    /**
     * 获取当前选中值
     * @returns {|*}
     */
    GlobalTree.prototype.getNowParam = function () {
        return dtree.getNowParam(this.id);
    }

    /**
     * 展开指定节点
     * @param dom 节点dom对象
     */
    GlobalTree.prototype.clickSpread = function (dom) {
        dtreeObj.clickSpread(dom)
    }

    /**
     * 选中指定的树节点
     * @param ids id集合，可以是数组，也可以是,拼接的的字符串
     */
    GlobalTree.prototype.chooseDataInit = function (ids) {
        if (Array.isArray(ids)) {
            ids = ids.join(',')
        }
        dtree.chooseDataInit(this.id, ids);
    }

    GlobalTree.prototype.changeTreeNodeAdd = function (op) {
        dtreeObj.changeTreeNodeAdd(op)
    }

    GlobalTree.prototype.changeTreeNodeDel = function (p) {
        dtreeObj.changeTreeNodeDel(p);
    }

    GlobalTree.prototype.changeTreeNodeEdit = function (p) {
        dtreeObj.changeTreeNodeEdit(p);
    }

    GlobalTree.prototype.changeTreeNodeDone = function (p) {
        dtreeObj.changeTreeNodeDone(p);
    }

    GlobalTree.prototype.partialRefreshAdd = function (dom, obj) {
        dtreeObj.partialRefreshAdd(dom, obj);
    }

    GlobalTree.prototype.partialRefreshDel = function (dom) {
        dtreeObj.partialRefreshDel(dom);
    }
    GlobalTree.prototype.clickSpread = function(dom){
        dtreeObj.clickSpread(dom);
    }
    GlobalTree.prototype.cancelNavThis = function(dom){
        dtreeObj.cancelNavThis(dom);
    }

    // 为了可继承dtree的内部方法，将dtree拷贝到一个新的对象
    let globalTree = $.extend({}, dtree);

    /**
     * 初始化树组件
     * @param options
     */
    globalTree.init = function (options) {
        tree = new GlobalTree(options);
        return tree;
    }

    /**
     * 重新加载树组件
     * @param options
     */
    globalTree.reload = function (options) {
        tree.reload(options);
    }

    /**
     * 获取当前选中值
     * @returns {|*}
     */
    globalTree.getNowParam = function () {
        return tree.getNowParam();
    }

    /**
     * 选中指定的树节点
     * @param ids id集合，可以是数组，也可以是,拼接的的字符串
     */
    globalTree.chooseDataInit = function (ids) {
        tree.chooseDataInit(ids);
    }

    /**
     * 将数组转换为树形组件数据
     * @param arr
     */
    globalTree.parseTreeData = function (arr = []) {
        let treeList = [];
        if (Array.isArray(arr)) {
            // 找根节点
            for (let data of arr) {
                if (!arr.find(a => a.id == data.parentId)) {
                    treeList.push(data);
                }
            }

            //递归设置子节点
            setChildren(treeList, arr);
        }
        return treeList;
    }

    exports('globalTree', globalTree)
});


/**
 * 递归设置子级目录
 * @param parentList
 * @param arr
 */
function setChildren(parentList, arr) {
    for (let parent of parentList) {
        parent.children = arr.filter(child => child.parentId == parent.id);
        setChildren(parent.children, arr);
    }
}