/**
 * <p>
 *  列表页面通用组件
 * </p>
 *
 * @author Administrator
 * @since 2021/5/25
 */
layui.define(['layer', 'gtable', 'globalTree'], function (exports) {
    const layer = layui.layer,
        gtable = layui.gtable,
        globalTree = layui.globalTree;

    let editLayerWin;
    let searchText = '';

    // 默认事件
    let events = {
        query: function (data, _this) {
            searchText = $(`#${_this.table.searchInput}`).val().trim();

            let fieldNames = _this.table.searchFieldNames;
            if (fieldNames) {
                if (Array.isArray(fieldNames)) {
                    for (let field of fieldNames) {
                        _this.table.where[field] = searchText;
                    }
                } else {
                    _this.table.where[fieldNames] = searchText;
                }
            } else {
                _this.table.where.name = searchText
            }
            _this.reloadTable(_this)
        },
        add: function (data, _this) {
            if (_this.table.btnAdd) {
                _this.table.btnAdd(data);
            } else {
                _this.openEditLayer(_this)
            }
        },
        edit: function (data, _this) {
            if (_this.table.btnEdit) {
                _this.table.btnEdit(data);
            } else {
                _this.openEditLayer(_this, data[_this.dataIDField]);
            }
        },
        delete: function (data, _this) {
            if (_this.table.btnDelete) {
                _this.table.btnDelete(data);
            } else {
                layer.confirm('确定要删除该数据吗？', function (index) {
                    _this.deleteRow(data[_this.dataIDField], () => {
                        gtable.reload();
                        layer.close(index)
                    });
                });
            }
        }
    };

    /**
     * 列表页面对象
     * @constructor
     */
    const ListPage = function (options) {
        this.deleteUrl = options.deleteUrl || ''; // 删除数据接口
        this.deleteParams = options.deleteParams || undefined; // 数据删除参数，如果不传，默认为url参数形式
        this.dataIDField = options.dataIDField || 'id'; // 数据主键字段
        // 编辑页面弹窗属性
        this.editPage = {
            id: 'menuEdit', // 编辑页面ID
            title: '编辑窗口', // 编辑页面标题
            area: ['800px', '565px'], // 编辑页面尺寸
            content: ``, // 编辑页面url
            btn: ['保存', '取消'],
            end: undefined, // 销毁时的回调函数
        };
        this.editPage = $.extend(this.editPage, options.editPage);

        // 数据表格属性
        this.table = {
            id: '', // 表格ID
            url: '', // 表格数据请求url
            method: 'post', // 请求方法
            searchInput: 'searchKeyInput',
            searchFieldNames: undefined, // [] 搜索框查询的字段名数组
            queryParam: undefined, // function(where){} 设置请求参数回调
            cols: [[]], // 数据表头定义
            height:'full-74',
            done: undefined, // function (obj) {}, // 表格加载完成回调
            onToolBarTable: undefined, // function (obj) {}, // 表格头部工具栏监听回调
            onToolBarRow: undefined, //function (obj) {}, // 表格行内工具栏监听回调
            btnAdd: undefined, //function(obj){}, // 内置新增按钮回调
            btnEdit: undefined, //function(obj){}, // 内置编辑按钮回调
            btnDelete: undefined, //function(obj){}, // 内置删除按钮回调
            btnQuery: undefined, //function(obj){}, // 内置查询按钮回调
        };
        this.table = $.extend(this.table, options.table);

        if (typeof options != 'undefined') {
            this.initTable(options);
        }
    };

    /**
     * 初始化数据表格
     * @param options
     */
    ListPage.prototype.initTable = function (options) {
        if (typeof options.table == 'undefined'
            || Object.keys(options.table).length === 0) {
            return;
        }
        this.table = $.extend(this.table, options.table);
        const _this = this;
        if (!this.table.onToolBarTable) {
            this.table.onToolBarTable = function (obj) {
                _this.toolBarCallback(obj);
            }
        }
        if (!this.table.onToolBarRow) {
            this.table.onToolBarRow = function (obj) {
                _this.toolBarCallback(obj);
            }
        }
        let table = $.extend({}, this.table);
        if (table.done) {
            this.table.done = function (obj) {
                $(`#${table.searchInput}`).val(searchText);
                table.done(obj);
            }
        } else {
            this.table.done = function (obj) {
                $(`#${table.searchInput}`).val(searchText);
            }
        }
        let _table = gtable.init(this.table);
        this.table = $.extend(true, this.table, _table.config);
    }

    ListPage.prototype.reloadTable = function (options) {
        options = options || {table: {}};
        const tableOps = $.extend(this.table, options.table);
        this.initTable({table:tableOps});
    }

    /**
     * 表格按钮点击事件
     * @param obj
     */
    ListPage.prototype.toolBarCallback = function (obj) {
        for (let key in events) {
            if (key === obj.event) {
                events[key] && events[key](obj.data, this);
            }
        }
    };

    /**
     * 删除数据
     * @param dataId
     * @param callback
     */
    ListPage.prototype.deleteRow = function (dataId, callback) {
        if (!this.deleteUrl) {
            console.error('ListPage.deleteUrl is null');
            return;
        }
        let url = `${this.deleteUrl}/${dataId}`;
        let params = this.deleteParams || {};
        if (this.deleteParams) {
            url = this.deleteUrl;
        }
        $.delete(url, params, function (res) {
            res.msg = res.flag ? '删除成功' : res.msg;
            let icon = res.flag ? 1 : 5;
            layer.msg(res.msg, {icon, time: 2000}, function () {
                if (res.flag) {
                    callback && callback();
                }
            });
        });
    }

    /**
     * 打开编辑页面弹窗
     * @param options
     * @param dataId
     */
    ListPage.prototype.openEditLayer = function (options, dataId = '') {
        if (typeof options.editPage == 'undefined'
            || Object.keys(options.editPage).length === 0) {
            return;
        }
        this.editPage = $.extend(this.editPage, options.editPage);

        const _content = this.editPage.content;
        let _this = this;
        this.editPage.type = 2;
        this.editPage.success = function (layero) {
            editLayerWin = window[layero.find('iframe')[0]['name']];
            _this.editPage.content = _content;
        }
        this.editPage.yes = function (index) {
            editLayerWin && editLayerWin.save().then(ok => {
                if (ok) {
                    gtable.reload();
                    layer.close(index);
                }
            });
        }

        this.editPage.content = `${options.editPage.content}?id=${dataId}`
        layer.open(this.editPage);
    }

    /**
     * 添加行级事件
     * @param layFilter 按钮的layui过滤器名称
     * @param callback 回调函数，函数包含listPage组件对象和所在行数据
     */
    ListPage.prototype.addTableRowEvent = function (layFilter, callback) {
        events[layFilter] = callback;
    }

    /**
     * 获取表格选中数据
     * @returns {*}
     */
    ListPage.prototype.getCheckStatus = function () {
        return gtable.getCheckStatus();
    }

    // 对外暴露接口
    exports('listPage', {
        /**
         * 初始化页面
         * @param options
         * @returns {ListPage}
         */
        init: function (options) {
            return new ListPage(options);
        },

        /**
         * 获取表格当前选中数据行
         * @returns {*}
         */
        getTableCheckStatus: function () {
            return gtable.getCheckStatus();
        }
    });
})