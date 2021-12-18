const LOGIC_JOIN = 'logic_join', FIELD_NAME = 'field_name', LOGIC_JUDGEMENT='logic_judgement', FIELD_VALUE='filed_value'
layui.use(['dropdown'], function () {
    const dropdown = layui.dropdown;
    // 初始化条件抽屉
    initConditionDrawer();


    /**
     * 初始化条件抽屉组件
     */
    function initConditionDrawer() {
        // 设置抽屉显示隐藏动画以及相关按钮逻辑
        let cb_width = $('#condition-box').width(), cb_padding = $('#condition-box').css('padding');
        $('#condition-btn').on('click', function () {
            let isHide = ($(this).data('type') == 'hide');
            $('#condition-box').animate({width: isHide ? 0 : cb_width, padding: isHide ? '0 0' : cb_padding})
            $('#condition-box').find('.layui-card:first').animate({left: (isHide ? -cb_width : 0) + 'px'})
            $(this).data('type', isHide ? 'show' : 'hide')
            $(this).text(isHide ? '展开条件' : '收起条件')
        })
        //添加条件按钮事件监听
        conditionBtnEventListener();
    }

    /**
     * 添加条件按钮时间监听
     */
    function conditionBtnEventListener() {
        $(`a[name='condition-add-btn']`).on('click', function () {
            const clickType = $(this).data('type');
            if (clickType == 'single-filed') {
                addConditionItem();
            } else if (clickType == 'subquery') {

            }
        });

    }

    /**
     * 添加条件项
     */
    function addConditionItem() {
        let $condition_list = $('#condition-list');
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
                    <a id="${logic_join_id}" name="${LOGIC_JOIN}" class="layui-btn layui-btn-xs layui-btn-primary my-btn ${is_first ? 'layui-hide' : ''}" ${is_first ? 'disabled="disabled"' : ''}  style="color: #666">并且</a>
                    <a id="${field_name_id}" name="${FIELD_NAME}" class="layui-btn layui-btn-xs layui-btn-primary my-btn" title="">&lt;选择字段&gt;</a>
                    <a id="${logic_judgement_id}" name="${LOGIC_JUDGEMENT}" class="layui-btn layui-btn-xs layui-btn-primary my-btn" style="color: red">等于</a>
                    <input id="${field_value_id}" name="${FIELD_VALUE}" type="text" value="" placeholder="输入值" style="width: 200px"/>
                </div>
                <a id="${btn_delete_id}" data-id="${index}" name="condition_btn_del" class="layui-btn layui-btn-xs layui-btn-primary my-btn" style="color: #666">移除</a>
            </li>
            `
        $condition_list.find('p.condition-none').addClass('layui-hide');
        $condition_list.append(condition_item_template);

        //条件项内的按钮渲染
        renderConditionItemBtns();
        // 移除按钮点击事件
        bindRemoveBtnEventListener()
    }

    /**
     * 渲染条件项内的按钮组件
     */
    function renderConditionItemBtns(){
        let $condition_list = $('#condition-list'),
            $LOGIC_JOIN_ELEM = $condition_list.find(`li div a[name="${LOGIC_JOIN}"]`),
            $FIELD_NAME_ELEM = $condition_list.find(`li div a[name="${FIELD_NAME}"]`),
            $LOGIC_JUDGEMENT_ELEM = $condition_list.find(`li div a[name="${LOGIC_JUDGEMENT}"]`);

        $LOGIC_JOIN_ELEM.each(function(){
            let $this = $(this), id=$this.attr('id');
            console.info(id);
            dropdown.render({
                elem: `#${id}`,
                data: [
                    {title: '并且', value: 'and'},
                    {title: '或者', value: 'or'}
                ]
            })
        })
    }

    /**
     * 绑定移除按钮点击事件监听
     */
    function bindRemoveBtnEventListener(){
        $(`a[name="condition_btn_del"]`).on('click', function () {
            let id = $(this).data('id');
            $('#condition-list').find(`li[id="li_${id}"]`).remove();
            if($('#condition-list li').length == 0){
                $('#condition-list').find('p.condition-none').removeClass('layui-hide');
            }
        });
    }
});