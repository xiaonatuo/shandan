// 组件使用示例
// 必须先声明使用该组件
/*layui.use(['dict'], function () {
    // 方式一：在html中添加如下标签元素
    //<div dict-component="select" dict-type="data_type" dict-name="dataType" id="dict-data-type"></div>
    // 属性说明：
    // dict-component: 组件类型：select、radio、checkbox
    // dict-type: 数据字典类型
    // dict-name: 数据字典名称，相对于form表单来说，作用相当于input标签中的name
    // id：组件的ID，同时该值会用于渲染之后该组件对于layui的lay-filter属性
    // 绑定数据：layui.dict.setData(id, data)
    // 绑定onchange事件回调函数：layui.dict.onchange(id, callback)

    // 方式二：使用js初始化
    let dict = layui.dict;

    let dictOption = {
        elem: '', //渲染到目标元素的ID选择器或者元素DOM
        id: '', //组件的ID，同时该值会用于渲染之后该组件对于layui的lay-filter属性
        name: '', //数据字典名称，相对于form表单来说，作用相当于input标签中的name
        formFilter: '', //所在form表单标签上lay-filter的值
        type: '', //数据字典类型
        data: '', //初始选中的数据，如果type为‘checkbox’，也可以传入数组或者,拼接的包含多个值的字符串
        onchange: '' //数据改变后的回调函数
    };
    dict.select(dictOption);
});*/

/**
 *  数据字典组件
 *
 * @author Administrator
 * @since 2021/11/19
 */
layui.define(['form'], function (exports) {
    let form = layui.form;
    const _cache = new Map();

    /**
     * 数据字典组件对象
     */
    class DictComponent {

        DICT;// 数据字典
        elem = '';// 渲染元素的ID
        id = '';// 组件的ID
        name = '';// 组件的name，相对于form表单来说
        type = '';// 渲染类型
        formFilter = '';// form表单标签上lay-filter的值
        dictType = '';// 数据字典类型
        data = '';// 选中的数据
        change;// 数据改变后的回调函数
        readonly;// 是否只读

        constructor(type, options) {
            if (!type) {
                console.error('component type is null!');
                return
            }
            if (!options) {
                console.error('component options is null!');
                return
            }
            if (!options.id) {
                console.error('component options.id is null!');
                return
            }
            if (!options.type) {
                console.error('component options.type is null!');
                return
            }
            this.id = options.id;
            this.elem = options.elem;
            this.name = options.name;
            this.type = type;
            this.formFilter = options.formFilter;
            this.data = options.data;
            this.dictType = options.type;
            this.DICT = _Store.get(STORE_DICT_KEY);
            this.change = options.onchange || function () {
            };
            this.readonly = options.readonly;

            // 开始渲染
            this.render();
        }

        /**
         * 渲染组件
         */
        render() {
            let dict_list;
            if (this.DICT) {
                dict_list = this.DICT[this.dictType] || [];
            } else {
                dict_list = [];
            }

            let _elem;
            if (typeof this.elem == 'string') {
                // 如果为string类型，则认为是ID选择器，判断是否有#，没有则加上
                _elem = this.elem.startsWith('#') ? $(this.elem) : $(`#${this.elem}`);
            } else {
                // 如果不是jquery对象，则包装为jquery对象
                _elem = this.elem instanceof jQuery ? this.elem : $(this.elem);
            }
            _elem.addClass('layui-hide');
            let template = '';
            switch (this.type) {
                case 'select':
                    template = selectDOM(this, dict_list);
                    break;
                case 'radio':
                case 'checkbox':
                    template = normalSingleNodeDOM(this, dict_list);
                    break;
            }
            _elem.after(template);
            form.render(this.type);

            this.onchange();

            // 设置默认值
            this.setData(this.data);

            // 设置只读
            if (this.readonly) {
                setTimeout(() => {
                    // 先添加用于覆盖layui样式的内部样式到head中
                    $('head').append(`
                        <style>
                            /* 所有只读组件鼠标样式*/
                            .readonly_cursor:hover{cursor: auto}
                            /* 单选按钮的只读样式 */
                            .layui-form-radio.radio-readonly i{color: #c2c2c2}
                            .layui-form-radio.radio-readonly div{color: #000000}
                            .layui-form-radio.radio-readonly:hover i{color: #c2c2c2 !important;}
                            .layui-form-radio.radio-readonly:hover div{color: #000000 !important;}
                            /* 复选框的只读样式 */
                            .layui-form-checkbox.radio-readonly:hover i{border-color: #d2d2d2;background-color: #fff}
                            .layui-form-checked.radio-readonly i{border-color: #b3b3b3 !important;background-color: #b3b3b3 !important;}
                            /* 下拉框的只读样式 */
                            .layui-form-select.select-readonly .layui-input:focus{border-color: #eee !important;}
                            .layui-form-select.select-readonly input{color: #757575}
                            .layui-form-select.select-readonly i{display: none}
                        </style>`);

                    switch (this.type) {
                        case "radio":
                        case "checkbox":
                            let elements = _elem.parent().find(`input[type="${this.type}"][name="${this.name}"]`);
                            $.each(elements, function (index) {
                                let lay_elem = $(this).next();
                                // 移除click事件监听
                                lay_elem.off('click').addClass('radio-readonly').css('cursor', 'auto');
                            });
                            break;
                        case "select":
                            let lay_elem = _elem.parent().find(`select[name="${this.name}"]`).next();
                            lay_elem.addClass('select-readonly').find('.layui-select-title').off('click').children().css('cursor', 'auto');
                    }
                }, 50);
            }

            _cache.set(this.id, this);
        }

        /**
         * 设置组件数据
         * @param data
         */
        setData(data) {
            if (!data) return;
            if (!this.formFilter) {
                console.error('options中没有设置formFilter');
                return;
            }
            if (typeof data == 'object') {
                data = data[this.name] || '';
            }

            if (this.type == 'checkbox') {
                let values = data;
                let elements = $(`[lay-filter="${this.formFilter}"] input[type="checkbox"][name="${this.name}"]`);
                if (!Array.isArray(data)) {
                    values = data.split(',');
                }

                for (let val of values) {
                    for (let index = 0; index < elements.length; index++) {
                        let elem = elements[index];
                        if (elem.value == val) {
                            $(elem).attr('checked', 'checked');
                        } else {
                            $(elem).attr('checked', null);
                        }
                    }
                }

            } else {
                if (Array.isArray(data)) data = data[0]
                let formData = {};
                formData[this.name] = data;
                form.val(this.formFilter, formData);
            }

            form.render(this.type);
        }

        /**
         * onchange事件回调函数绑定
         * @param callback
         */
        onchange(callback) {
            if (callback) {
                this.change = callback;
            }
            form.on(`${this.type}(${this.id})`, this.change);
        }
    }


    /**
     * 生成下拉框组件模板
     * @param _this
     * @param list
     * @returns {string}
     */
    function selectDOM(_this, list) {

        let nodes = [`<option value="">请选择</option>`];
        for (let dict of list) {
            let disabled = '';
            if (!dict.dictState) {
                disabled = 'disabled';
            }
            nodes.push(`<option ${disabled} value="${dict.dictCode}">${dict.dictName}</option>`)
        }
        let nodes_html = nodes.join('');

        return `<select id="${_this.id}" name="${_this.name}" lay-filter="${_this.id}">${nodes_html}</select>`;
    }

    /**
     * 生成普通单节点组件模板
     * @param _this
     * @param list
     * @returns {string}
     */
    function normalSingleNodeDOM(_this, list) {
        let nodes = [];

        /*let readonly = false;
        if (_this.readonly && (_this.readonly == 'readonly' || _this.readonly == 'true')) {
            readonly = true;
        }*/

        for (let dict of list) {
            let disabled = '';
            if (!dict.dictState) {
                disabled = 'disabled';
            }
            nodes.push(`<input type="${_this.type}" ${disabled}  name="${_this.name}" value="${dict.dictCode}" title="${dict.dictName}" lay-skin="primary" lay-filter="${_this.id}">`)
        }
        return nodes.join('');
    }

    /**
     * 自动渲染
     */
    $(function () {
        let elements = $(`[dict-component]`);
        elements.each(function (index) {
            let _elem = $(this);
            let elem = this,
                id = _elem.attr('id'),
                name = _elem.attr('dict-name'),
                type = _elem.attr('dict-type'),
                dictType = _elem.attr('dict-component'),
                readonly = _elem.attr('readonly'),
                formFilter = getFormElementFilter(_elem);

            let option = {elem, id, name, type, formFilter, readonly};
            new DictComponent(dictType, option)
        });
    });

    /**
     * 获取元素所在form表单的layui过滤器名称
     * @param $elem
     * @returns {string|*|jQuery}
     */
    function getFormElementFilter($elem) {
        for (let index = 0; index < $elem.parents().length; index++) {
            let elem = $elem.parents()[index];
            if (elem.tagName === 'FORM' || $(elem).hasClass('layui-form')) {
                return $(elem).attr('lay-filter');
            }
        }
        return '';
    }


    /**
     * 暴露组件
     * options：{
     *     elem: '', 渲染到目标元素的ID选择器或者元素DOM
     *     id: '', 组件的ID，同时该值会用于渲染之后该组件对于layui的lay-filter属性
     *     name: '', 组件的name，相对于form表单来说
     *     formFilter: '', 所在form表单标签上lay-filter的值
     *     type: '', 数据字典类型
     *     data: '', 初始选中的数据，如果type为‘checkbox’，也可以传入数组或者,拼接的包含多个值的字符串
     *     onchange:'' 数据改变后的回调函数
     * }
     * @type {{select: (function(*=): DictComponent), checkbox: (function(*=): DictComponent), radio: (function(*=): DictComponent)}}
     */
    const component = {
        /**
         * 渲染下拉选择框组件
         * @param options
         * @returns {DictComponent}
         */
        select: function (options) {
            return new DictComponent('select', options);
        },

        /**
         * 渲染单选按钮组件
         * @param options
         * @returns {DictComponent}
         */
        radio: function (options) {
            return new DictComponent('radio', options);
        },

        /**
         * 渲染复选框组件
         * @param options
         * @returns {DictComponent}
         */
        checkbox: function (options) {
            return new DictComponent('checkbox', options);
        },

        /**
         * 为组件绑定onchange事件的回调函数
         * @param id 组件的ID
         * @param callback onchange事件的回调函数
         */
        onchange: function (id, callback) {
            let dict = _cache.get(id);
            if (dict) {
                dict.onchange(callback);
            } else {
                console.error('组件未定义：' + id);
            }
        },

        /**
         * 为组件绑定数据
         * @param id 组件的ID
         * @param data 要绑定的数据
         */
        setData: function (id, data) {
            let dict = _cache.get(id);
            if (dict) {
                _cache.get(id).setData(data);
            } else {
                console.error('组件未定义：' + id);
            }
        }

    };
    exports('dict', component);
});