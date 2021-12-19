const LOGIC_JOIN = 'logic_join', FIELD_NAME = 'field_name', LOGIC_JUDGEMENT = 'logic_judgement',
    FIELD_VALUE = 'filed_value';
// 达梦数据库日期时间数据类型集合
const DATE_TYPE = ['DATE', 'DATETIME', 'TIMESTAMP', 'DATETIME WITH TIME ZONE', 'TIME', 'TIME WITH TIME ZONE', 'TIMESTAMP WITH TIME ZONE', 'TIMESTAMP WITH LOCAL TIME ZONE'];
layui.use(['dropdown', 'gtable', 'laydate'], function () {
    const dropdown = layui.dropdown, gtable = layui.gtable, laydate = layui.laydate;

    const $condition_list = $('#condition-list');
    let result_table;

    // 初始化条件抽屉
    initConditionDrawer();
    // 初始化数据表格
    initTable();

    let where = {};
    $('#btn_query').on('click', function () {
        let condition = getConditionItemValue();
        if (result_table) {
            where.conditions = condition
            result_table.reload({where});
        }
    });

    /**
     * 初始化数据表格
     */
    function initTable() {
        buildResultTableCols().then(cols => {
            result_table = gtable.init({
                id: 'meta-result-table',
                title: metadata.metadataName + (metadata.metadataComment ? `(${metadata.metadataComment})`: ''),
                url: `${ctx}/search/metadata/condition/${metadata.id}`,
                height: 'full-20',
                autoSort: false,
                cols: [cols],
                toolbar: '#table-title',
                defaultToolbar: ['filter', 'print', 'exports', {
                    title: '统计报表',
                    layEvent: 'tong-ji',
                    icon: 'layui-icon-chart' //图标类名
                }]
            })
            // 排序事件回调
            result_table.on('sort', function (obj) {
                where.sort = {
                    field: obj.field,
                    sort: obj.type
                }
                result_table.reload({initSort: obj,where})
            });
            // 统计报表自定义
            result_table.on('toolbar', function ({event}) {
                if (event == 'tong-ji') {
                    openStatisticalReport()
                }
            });
        });
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
            $FIELD_NAME_ELEM.attr('title', data.title);
            $FIELD_NAME_ELEM.text(data.title);

            //如果是日期时间类型则渲染日期时间组件
            if (DATE_TYPE.includes(data.dataType)) {
                let input_id = $FIELD_NAME_ELEM.next().next().attr('id');
                laydate.render({elem: `#${input_id}`, type: 'datetime'});
            } else {
                //如果不是时间类型，重新渲染input标签，因为laydate组件渲染后无法销毁
                let $elem = $FIELD_NAME_ELEM.next().next(), $elem_parent = $elem.parent();
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
            $condition_list.find(`li:first a[name="${LOGIC_JOIN}"]`).addClass("layui-hide").data('value', 'and');
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
                logicJoin = $(`#${LOGIC_JOIN}_${index}`).data('value'),
                logicJudgement = $(`#${LOGIC_JUDGEMENT}_${index}`).data('value');

            let $field_name = $(`#${FIELD_NAME}_${index}`),
                fieldName = $field_name.data('value'),
                dataType = $field_name.data('dataType'),
                table = $field_name.data('table');
            let fieldValue = $li.find(`input[name="${FIELD_VALUE}"]`).val();
            conditions.push({logicJoin, fieldName, logicJudgement, fieldValue, dataType, table})
        });
        return conditions;
    }

    /**
     *
     * @returns {Promise<unknown>}
     */
    function buildResultTableCols() {
        return new Promise((resolve, reject) => {
            if (columns.length == 0) {
                console.error("数据资源表的字段信息异常");
                reject && reject();
            } else {
                let cols = columns.map(col => {
                    let {columnName, comment} = col;
                    return {
                        title: comment || columnName,
                        field: columnName,
                        sort: true
                    }
                })
                resolve && resolve(cols);
            }
        });
    }

    function openStatisticalReport(){
        
    }
});