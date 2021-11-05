/**
 * 通用数据表格组件
 *
 * @author GuoXin
 * @since 2021-05-19
 */
layui.define(['table'], function (exports) {
    let table = layui.table;

    let options = {
        title: '数据表格',
        url:'',
        method: 'POST',
        toolbar: '#tableToolBar',
        //请求前参数处理
        request: {
            pageName: 'current' //页码的参数名称，默认：page
            , limitName: 'size' //每页数据量的参数名，默认：limit
        },
        response: {
            statusName: 'flag' //规定数据状态的字段名称，默认：code
            , statusCode: true //规定成功的状态码，默认：0
            , msgName: 'msg' //规定状态信息的字段名称，默认：msg
            , countName: 'total' //规定数据总数的字段名称，默认：count
            , dataName: 'records' //规定数据列表的字段名称，默认：data
        },
        //响应后数据处理
        parseData: function (res) { //res 即为原始返回的数据
            //console.info(res)
            let data = res.data || {};
            return {
                "flag": res.flag, //解析接口状态
                "msg": res.msg, //解析提示文本
                "records": data.records || res.records || data, //解析数据长度
                "total": data.total || data.length //解析数据列表
            };
        },
        defaultToolbar: ['', '', ''],
        page: true,
        height: 'full-84',
        cellMinWidth: 80,
        cols: [[{}]],
        done: function (res) {},//加载完成
        onToolBarTable: function (obj) {}, // 表格工具事件
        onToolBarRow: function (obj) {}, //行工具事件
        onRow: function(obj){}, // 行点击事件
        onRowDouble: function(obj){}, // 行双击事件
        onRadio: function(obj){}, //单选框选中事件
        onChecked: function(obj){}, //复选框选中事件
        onEdit: function(obj){} // 行内编辑事件
    };

    const render = function (opt) {
        this.options = $.extend({}, options);
        let ops = $.extend(true, this.options, opt);
        ops.elem = `#${ops.id}`;
        let tableObj = table.render(ops);
        table.on(`toolbar(${ops.id})`, function(obj){
            ops.onToolBarTable && ops.onToolBarTable(obj);
        });
        table.on(`tool(${ops.id})`, function(obj){
            ops.onToolBarRow && ops.onToolBarRow(obj);
        });
        table.on(`row(${ops.id})`, function(obj) {
            opt.onRow && opt.onRow(obj);
        });
        table.on(`radio(${ops.id})`, function(obj) {
            opt.onRadio && opt.onRadio(obj);
        });
        table.on(`checkbox(${ops.id})`, function(obj) {
            opt.onChecked && opt.onChecked(obj);
        });
        table.on(`edit(${ops.id})`, function(obj) {
            opt.onEdit && opt.onEdit(obj);
        });
        table.on(`rowDouble(${ops.id})`, function(obj) {
            opt.onRowDouble && opt.onRowDouble(obj);
        });
        return tableObj;
    }

    const gtable = $.extend({}, table);

    /**
     * 表格初始化
     * @param opt
     * @returns {*} 返回原始table组件对象
     */
    gtable.init = function (opt) {
        options = Object.assign(options, opt);

        if (options.id.startsWith('#')) {
            options.id = options.id.substring(1)
        }
        return render(options);
    };
    /**
     * 表格重载
     * @param opt
     */
    gtable.reload = function (opt) {
        table.reload(options.id, opt);
    };

    /**
     * 获取选中数据
     * @returns {*}
     */
    gtable.getCheckStatus = function(){
        return table.checkStatus(options.id);
    }

    /**
     * 获取当前表格当前页数据
     * @returns {*}
     */
    gtable.getData = function(){
        return table.getData(options.id);
    }

    exports('gtable', gtable);
})