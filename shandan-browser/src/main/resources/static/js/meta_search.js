const LOGIC_JOIN = 'logic_join', FIELD_NAME = 'field_name', LOGIC_JUDGEMENT = 'logic_judgement',
    FIELD_VALUE = 'filed_value';
// 达梦数据库日期时间数据类型集合
const DATE_TYPE = ['DATE', 'DATETIME', 'TIMESTAMP', 'DATETIME WITH TIME ZONE', 'TIME', 'TIME WITH TIME ZONE', 'TIMESTAMP WITH TIME ZONE', 'TIMESTAMP WITH LOCAL TIME ZONE'];
layui.use(['dropdown', 'gtable', 'laydate'], function () {
    const dropdown = layui.dropdown, gtable = layui.gtable, laydate = layui.laydate;

    const $condition_list = $('#condition-list');

    // 初始化条件抽屉
    initConditionDrawer();
    // 初始化数据表格
    initTable();

    $('#btn_query').on('click', function () {
        let condition = getConditionItemValue();
        console.info(condition);
    });

    /**
     * 初始化数据表格
     */
    function initTable() {

    }

    /**
     * 初始化条件抽屉组件
     */
    function initConditionDrawer() {
        let $condition_box = $('#condition-box');
        // 设置抽屉显示隐藏动画以及相关按钮逻辑
        let cb_width = $condition_box.width(), cb_padding = $condition_box.css('padding');
        $('#condition-btn').on('click', function () {
            let isHide = ($(this).data('type') == 'hide');
            $condition_box.animate({width: isHide ? 0 : cb_width, padding: isHide ? '0 0' : cb_padding})
            $condition_box.find('.layui-card:first').animate({left: (isHide ? -cb_width : 0) + 'px'})
            $(this).data('type', isHide ? 'show' : 'hide')
            $(this).text(isHide ? '展开条件' : '收起条件')
        })
        //添加条件按钮事件监听
        $(`a[name='condition-add-btn']`).on('click', addConditionItem);
    }

    /**
     * 添加条件项
     */
    function addConditionItem() {
        let $li_list = $condition_list.children('li'), index = 0, is_first = ($li_list.length == 0);

        if (!is_first) {
            index = $li_list.filter(':last').data('id') + 1;
        }

        // 定义各按钮的ID属性
        let li_id = `li_${index}`, logic_join_id = `${LOGIC_JOIN}_${index}`, field_name_id = `${FIELD_NAME}_${index}`,
            logic_judgement_id = `${LOGIC_JUDGEMENT}_${index}`, field_value_id = `${FIELD_VALUE}_${index}`,
            btn_delete_id = `btn_delete_${index}`;

        const condition_item_template = `
            <li id="${li_id}" data-id="${index}">
                <div>
                    <a id="${logic_join_id}" name="${LOGIC_JOIN}" data-value="and" class="layui-btn layui-btn-xs layui-btn-primary my-btn ${is_first ? 'layui-hide' : ''}" ${is_first ? 'disabled="disabled"' : ''}  style="color: #666">并且</a>
                    <a id="${field_name_id}" name="${FIELD_NAME}" data-value="" class="layui-btn layui-btn-xs layui-btn-primary my-btn" title="">&lt;选择字段&gt;</a>
                    <a id="${logic_judgement_id}" name="${LOGIC_JUDGEMENT}" data-value="=" class="layui-btn layui-btn-xs layui-btn-primary my-btn" style="color: red">等于</a>
                    <input id="${field_value_id}" name="${FIELD_VALUE}" type="text" value="" placeholder="输入值" style="width: 200px"/>
                </div>
                <a id="${btn_delete_id}" data-id="${index}" name="condition_btn_del" class="layui-btn layui-btn-xs layui-btn-primary my-btn" style="color: #666">移除</a>
            </li>`
        $condition_list.find('p.condition-none').addClass('layui-hide');
        $condition_list.append(condition_item_template);

        //条件项内的按钮渲染
        renderConditionItemBtns();
    }

    /**
     * 渲染条件项内的按钮组件
     */
    function renderConditionItemBtns() {
        let $LOGIC_JOIN_ELEM = $condition_list.find(`li div a[name="${LOGIC_JOIN}"]:last`),
            $FIELD_NAME_ELEM = $condition_list.find(`li div a[name="${FIELD_NAME}"]:last`),
            $LOGIC_JUDGEMENT_ELEM = $condition_list.find(`li div a[name="${LOGIC_JUDGEMENT}"]:last`);

        // 选择条件拼接逻辑下拉菜单
        renderDropdown($LOGIC_JOIN_ELEM, [
            {title: '并且', value: 'and'},
            {title: '或者', value: 'or'}
        ], function ({title, value}) {
            $LOGIC_JOIN_ELEM.data('value', value);
            $LOGIC_JOIN_ELEM.text(title);
        });

        // 选择条件判断逻辑下拉菜单
        renderDropdown($LOGIC_JUDGEMENT_ELEM, [
            {title: '等于', value: '='},
            {title: '不等于', value: '!='},
            {title: '大于', value: '>'},
            {title: '小于', value: '<'},
            {title: '大于等于', value: '>='},
            {title: '小于等于', value: '<='},
            {title: '包含', value: 'like_all'},
            {title: '开始于', value: 'like_right'},
            {title: '结束于', value: 'like_left'},
        ], function ({title, value}) {
            $LOGIC_JUDGEMENT_ELEM.data('value', value);
            $LOGIC_JUDGEMENT_ELEM.text(title);
        });

        // 表字段数据转换
        let columns_n = columns.map(col => {
            return {
                title: col.comment || col.columnName,
                value: col.columnName,
                table: col.tableName,
                dataType: col.dataType
            }
        })
        // 选择字段下拉菜单
        renderDropdown($FIELD_NAME_ELEM, columns_n, function (data) {
            $FIELD_NAME_ELEM.data('value', data.value);
            $FIELD_NAME_ELEM.data('table', data.table);
            $FIELD_NAME_ELEM.data('dataType', data.dataType);
            $FIELD_NAME_ELEM.text(data.title);

            //如果是日期时间类型则渲染日期时间组件
            if (DATE_TYPE.includes(data.dataType)) {
                let input_id = $FIELD_NAME_ELEM.next().next().attr('id');
                laydate.render({elem: `#${input_id}`, type: 'datetime'});
            }else{
                //如果不是时间类型，重新渲染input标签，因为laydate组件渲染后无法销毁
                let $elem = $FIELD_NAME_ELEM.next().next(),$elem_parent = $elem.parent();
                $elem.remove();
                $elem_parent.append($elem[0].outerHTML);
            }
        });

        // 条件项移除按钮点击事件
        bindRemoveBtnEventListener();
    }

    /**
     * 渲染下拉菜单
     * @param $elem 触发的元素
     * @param data 菜单数据
     * @param click 菜单项点击回调函数
     */
    function renderDropdown($elem, data, click) {
        let id = $elem.attr('id');
        dropdown.render({elem: `#${id}`, data, click})
    }

    /**
     * 绑定移除按钮点击事件监听
     */
    function bindRemoveBtnEventListener() {
        $(`a[name="condition_btn_del"]`).on('click', function () {
            let id = $(this).data('id');
            $condition_list.find(`li[id="li_${id}"]`).remove();
            if ($condition_list.find('li').length == 0) {
                $condition_list.find('p.condition-none').removeClass('layui-hide');
            }
        });
    }

    /**
     * 获取条件项
     */
    function getConditionItemValue() {
        let conditions = [];
        $condition_list.find('li').each(function () {
            let $li = $(this), index = $li.data('id'),
                logic_join = $(`#${LOGIC_JOIN}_${index}`).data('value'),
                logic_judgement = $(`#${LOGIC_JUDGEMENT}_${index}`).data('value');

            let $field_name = $(`#${FIELD_NAME}_${index}`),
                field_name = $field_name.data('value'),
                data_type = $field_name.data('dataType'),
                table = $field_name.data('table');
            let field_value = $li.find(`input[name="${FIELD_VALUE}"]`).val();
            conditions.push({logic_join, field_name, logic_judgement, field_value, data_type, table})
        });
        return conditions;
    }
});