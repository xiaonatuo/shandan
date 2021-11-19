/**
 *  数据字典组件
 *
 * @author Administrator
 * @since 2021/11/19
 */
layui.define(['form'], function (exports) {
    let form = layui.form;

    /**
     * 数据字典组件对象
     */
    class DictComponent {
        // 数据字典
        DICT;
        // 渲染元素的ID
        elem = '';
        // 组件的ID
        id = '';
        // 组件的name，相对于form表单来说
        name = '';
        // 渲染类型
        type = '';
        // form表单标签上lay-filter的值
        formFilter = '';
        // 数据字典类型
        dictType = '';
        // 选中的数据
        data = '';
        // 数据改变后的回调函数
        change;

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

            // 开始渲染
            this.render();
        }

        /**
         * 渲染组件
         */
        render() {
            if (!this.DICT) {
                return
            }
            let dict_list = this.DICT[this.dictType];
            let _elem = $(`#${this.elem}`);
            _elem.addClass('layui-hide');
            let template = '';
            switch (this.type) {
                case 'select':
                    template = selectDOM(this, dict_list);
                    break;
                case 'checkbox':
                    template = normalSingleNodeDOM(this, dict_list);
                    break;
                case 'radio':
                    template = normalSingleNodeDOM(this, dict_list);
                    break;
            }
            _elem.after(template);
            form.render(this.type);

            // 设置事件监听
            form.on(`${this.type}(${this.id})`, this.change);

            // 设置默认值
            this.setFormData(this.data);
        }

        setFormData(data){
            if(!this.formFilter){
                console.error('options中没有设置formFilter');
                return;
            }
            if(this.type == 'checkbox'){
                let values = data;
                if (!Array.isArray(data)) {
                    values = data.split(',');
                }
                for(let val of values){
                    $(`[lay-filter="${this.formFilter}"] input[type="checkbox"][name="${this.name}"][value="${val}"]`).attr('checked', true);
                }
            }else{
                if (Array.isArray(data)) data = data[0]
                let formData = {};
                formData[this.name] = data;
                form.val(this.formFilter, formData);
            }
            form.render(this.type);
        }
    }


    /**
     * 生成下拉框组件模板
     * @param _this
     * @param list
     * @returns {string}
     */
    function selectDOM(_this, list) {
        let nodes = [];
        for (let dict of list) {
            nodes.push(`<option value="${dict.dictCode}">${dict.dictName}</option>`)
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
        for (let dict of list) {
            nodes.push(`<input type="${_this.type}" name="${_this.name}" value="${dict.dictCode}" title="${dict.dictName}" lay-skin="primary" lay-filter="${_this.id}">`)
        }
        return nodes.join('');
    }


    /**
     * 暴露组件
     * options：{
     *     elem: '', 渲染到目标元素的ID
     *     id: '', 组件的ID
     *     name: '', 组件的name，相对于form表单来说
     *     formFilter: '', form表单标签上lay-filter的值
     *     type: '', 数据字典类型
     *     data: '', 初始选中的数据，如果type为‘checkbox’，也可以传入数组或者,拼接的包含多个值的字符串
     *     onchange:'' 数据改变后的回调函数
     * }
     * @type {{select: (function(*=): DictComponent), checkbox: (function(*=): DictComponent), radio: (function(*=): DictComponent)}}
     */
    const component = {
        select: function (options) {
            return new DictComponent('select', options);
        },
        radio: function (options) {
            return new DictComponent('radio', options);
        },
        checkbox: function (options) {
            return new DictComponent('checkbox', options);
        }
    };
    exports('dict', component);
});