/**
 * <p>
 *  index.html
 * </p>
 *
 * @author Administrator
 * @since 2021/6/17
 */
layui.use(['layer', 'globalTree', 'form', 'element', 'laydate'], function () {
    const tree = layui.globalTree,
        layer = layui.layer,
        form = layui.form,
        element = layui.element,
        laydate = layui.laydate;

    initConditionBlock()
    initDirTree();

    /**
     * 初始化目录树
     */
    function initDirTree(){
        tree.init({
            id: 'directory-tree',
            url: `${ctx}/browser/dir/tree`,
            data: [{id: '-', parentId: '', title: '根目录', leaf: false, last: false, spread: false}],
            cache: false,
            done: function (nodes, elem) {
                // 模拟鼠标点击事件展开第一层目录
                $('i.dtree-icon-jia[data-id="-"]').click();
            },
        });
    }

    /**
     * 搜索条件模块初始化
     */
    function initConditionBlock(){
        // 监听更多条件按钮点击事件
        $('#condition-btn').click(function () {
            $('#condition-div').slideToggle('fast', 'linear', conditionSlideToggle)
        });

        // 监听标签点击事件
        element.on('tab(condition-tab)', function(data){
            // 点击后移除选中样式
            $('#condition-tab ul li').removeClass('layui-this')
        });

        // 监听条件标签删除事件
        element.on('tabDelete(condition-tab)', function(data){
            let key = $(this).parent().data('key');
            let formVal = form.val('search-form');
            formVal[key] = '';
            form.val('search-form', formVal);
            beginSearch();
        });

        // 搜索按钮点击事件监听
        $('#begin-search-btn').on('click', beginSearch);

        // 监听重置按钮
        $('#condition-clear-btn').on('click', function(){
            let formVal = form.val('search-form') || {};
            for(let key in formVal){
                if(key.startsWith('condition-')){
                    formVal[key] = 'eq';
                }else{
                    formVal[key] = '';
                }
            }
            console.info(formVal);
            form.val('search-form', formVal);
            renderConditionTabByForm();
        });

        /**
         * 渲染时间选择组件
         */
        laydate.render({
            elem: '#input-date-begin'
            ,type: 'date'
            ,range: '至'
        });
    }

    /**
     * 条件下拉框显示隐藏的回调
     */
    function conditionSlideToggle(){
        const display = $('#condition-div').css('display');
        // 隐藏时，根据搜索表单的值渲染条件标签，并根据条件检索
        if(display === 'none'){
            renderConditionTabByForm();
        }
    }

    /**
     * 根据条件表单数据渲染条件标签
     */
    function renderConditionTabByForm(){
        const formVal = form.val('search-form') || {}
        let htm = '';
        for (let key in formVal) {
            // 如果key是条件下拉框，则跳过
            if(key.startsWith('condition-')) continue;

            let val = formVal[key];
            if(val.trim()){
                let title = $(`#condition-div .layui-form-item input[name='${key}']`).parent().prev().text();
                let condition = '';
                switch (formVal[`condition-${key}`]) {
                    case 'eq':
                        condition = '等于';
                        break;
                    case 'nq':
                        condition = '不等于';
                        break;
                    case 'gt':
                        condition = '大于';
                        break;
                    case 'lt':
                        condition = '小于';
                        break;
                    case 'like':
                        condition = '包含';
                }
                htm += `<li data-key="${key}" style="color:#009688"><label style="color:#333">${title}</label><label style="color:gray;margin-left:5px">${condition}</label>: ${val}</li>`
            }
        }
        $('#condition-tab ul').html(htm);
        element.render('tab')
        // 开始检索
        beginSearch();
    }

    /**
     * 开始检索
     */
    function beginSearch(){
        console.info('开始条件查询数据:', form.val('search-form'))
        // 如果搜索条件下拉框处于显示状态则隐藏
        if($('#condition-div').css('display') !== 'none'){
            $('#condition-div').slideToggle('fast');
        }
    }
});