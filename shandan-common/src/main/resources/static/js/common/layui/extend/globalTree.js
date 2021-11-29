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
        this.width = 'auto';
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
        this.none = '没有找到数据';
        //this.render(options);
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
        return dtreeObj;
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
    GlobalTree.prototype.getNowParam2 = function () {
        return dtree.getNowParam(this.id);
    }


    /**
     * 选中指定的树节点
     * @param ids id集合，可以是数组，也可以是,拼接的的字符串
     */
    GlobalTree.prototype.chooseDataInit2 = function (ids) {
        if (Array.isArray(ids)) {
            ids = ids.join(',')
        }
        dtree.chooseDataInit(this.id, ids);
    }

    GlobalTree.prototype.getParentParam2 = function(id){
        dtree.getParentParam(this.id, id);
    };

    GlobalTree.prototype.initNoAllCheck2 = function () {
        dtree.initNoAllCheck(this.id)
    }

    // 为了可继承dtree的内部方法，将dtree拷贝到一个新的对象
    let globalTree = $.extend({}, dtree);

    /**
     * 初始化树组件
     * @param options
     */
    globalTree.init = function (options) {
        tree = new GlobalTree();
        let dtreeObj = tree.render(options)
        return $.extend(tree, dtreeObj);
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
        return tree.getNowParam2();
    }

    /**
     * 选中指定的树节点
     * @param ids id集合，可以是数组，也可以是,拼接的的字符串
     */
    globalTree.chooseDataInit = function (ids) {
        tree.chooseDataInit2(ids);
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