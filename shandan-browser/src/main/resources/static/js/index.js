/**
 * <p>
 *  index.html
 * </p>
 *
 * @author Administrator
 * @since 2021/6/17
 */
layui.use(['layer', 'globalTree', 'form', 'element', 'laydate', 'dropdown', 'laypage'], function () {
    const tree = layui.globalTree,
        layer = layui.layer,
        form = layui.form,
        element = layui.element,
        laydate = layui.laydate,
        dropdown = layui.dropdown,
        laypage = layui.laypage;

    const result = [];
    // 排序
    let order = {field: '', order: 'asc'};

    initConditionBlock()
    initDirTree();
    initSortingComponent();
    renderPageComponent();
    beginSearch();

    // 监听统计报表按钮点击事件
    $('#btn-report').on('click', function () {
        /*if(result.length <= 0){
            layer.msg('当前没有数据可用，请查询到数据后再点击')
            return;
        }*/
        console.info($('#layer-content-report').html())
        layer.open({
            //id: 'layer-content-report',
            type: 1,
            title: '统计报表',
            area: ['800px', '600px'],
            content: $('#layer-content-report').html(),
            success: function (layero, index) {
                layer.full(index);
            }
        })
    })

    /**
     * 渲染时间选择组件
     */
    laydate.render({
        elem: '#input-date-begin'
        , type: 'date'
        , range: '至'
    });

    /**
     * 初始化目录树
     */
    function initDirTree() {
        const dirTree = tree.init({
            id: 'directory-tree',
            url: `${ctx}/browser/dir/tree`,
            data: [{id: '-', parentId: '', title: '根目录', leaf: false, last: false, spread: false}],
            cache: false,
            done: function (nodes, elem) {
                // 模拟鼠标点击事件展开第一层目录
                $('i.dtree-icon-jia[data-id="-"]').click();
            },
            onClick: function (obj) {
                console.info(obj);
                const node = obj.param;
                const formVal = form.val('search-form');
                if (formVal.directoryId === node.id) {
                    formVal.directoryId = '';
                    dirTree.cancelNavThis(obj.dom)
                } else {
                    formVal.directoryId = node.id
                }
                form.val('search-form', formVal);
                console.info(formVal);
            }
        });
    }

    /**
     * 搜索条件模块初始化
     */
    function initConditionBlock() {
        // 监听更多条件按钮点击事件
        $('#condition-btn').click(function () {
            $('#condition-div').slideToggle('fast', 'linear', conditionSlideToggle)
        });

        // 监听标签点击事件
        element.on('tab(condition-tab)', function (data) {
            // 点击后移除选中样式
            $('#condition-tab ul li').removeClass('layui-this')
        });

        // 监听条件标签删除事件
        element.on('tabDelete(condition-tab)', function (data) {
            let key = $(this).parent().data('key');
            let formVal = form.val('search-form');
            formVal[key] = '';
            form.val('search-form', formVal);
            beginSearch();
        });

        // 搜索按钮点击事件监听
        $('#begin-search-btn').on('click', ()=>renderConditionTabByForm());

        // 监听重置按钮
        $('#condition-clear-btn').on('click', function () {
            let formVal = form.val('search-form') || {};
            for (let key in formVal) {
                if (key.startsWith('logic-')) {
                    formVal[key] = 'eq';
                } else {
                    formVal[key] = '';
                }
            }
            console.info(formVal);
            form.val('search-form', formVal);
            renderConditionTabByForm();
        });
    }

    let orderTemp;

    /**
     * 初始化排序组件
     */
    function initSortingComponent() {
        // 监听排序按钮点击事件
        $('#btn-sorting').on('click', function () {
            layer.open({
                type: 4,
                content: [$('#sorting-layer').html(), $(this)],
                closeBtn: false,
                shade: 0.1,
                resize: false,
                fixed: false,
                shadeClose: true,
                offset: 'b',
                tips: [3, '#ffffff'],
                success: function () {
                    orderTemp = $.extend({}, order); //临时存储，弹出层销毁时判断是否需要重新刷新数据
                    // 根据order渲染排序状态
                    if (order.field) {
                        $('#sorting-field li').each(function () {
                            const field = $(this).children(':eq(1)').data('field');
                            if (field === order.field) {
                                $(this).find(`a[data-sort="${order.order}"]`).addClass('on');
                            }
                        })
                    }

                    // 监听升序降序按钮点击事件
                    $('#sorting-field a.btn-sorting').on('click', function () {
                        const field = $(this).parent().data('field')
                        const sort = $(this).data('sort');
                        const on = $(this).hasClass('on');
                        $('#sorting-field a.btn-sorting').removeClass('on')
                        order = {field: '', order: 'asc'}
                        if (!on) {
                            order = {field, order: sort}
                            $(this).addClass('on');
                        }
                    })
                },
                end: function () {
                    // 判断排序是否发生变化
                    if (orderTemp && !(orderTemp.field === order.field && orderTemp.order === order.order)) {
                        // TODO 刷新数据列表
                        console.info('假装刷新了列表')
                    }
                }
            })
        })

        // 监听菜单点击事件, 设置为点击后li不显示选中样式
        dropdown.on('click(sorting-field)', function (data) {
            // 点击后移除选中样式
            $('#sorting-field li').removeClass('layui-menu-item-checked')
        });

    }


    /**
     * 条件下拉框显示隐藏的回调
     */
    function conditionSlideToggle() {
        const display = $('#condition-div').css('display');
        // 隐藏时，根据搜索表单的值渲染条件标签，并根据条件检索
        if (display === 'none') {
            renderConditionTabByForm();
        }
    }

    /**
     * 根据条件表单数据渲染条件标签
     */
    function renderConditionTabByForm() {
        const formVal = form.val('search-form') || {}
        let htm = '';
        for (let key in formVal) {
            // 如果key是条件下拉框，则跳过
            if (key.startsWith('logic-')) continue;

            let val = formVal[key];
            if (val.trim()) {
                let title = $(`#condition-div .layui-form-item input[name='${key}']`).parent().prev().text();
                let condition = '';
                switch (formVal[`logic-${key}`]) {
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

    function renderPageComponent(data) {
        data = data || {total: 0, size: 20}
        laypage.render({
            elem: 'page-component',
            curr: data.page,
            limit: data.size,
            count: data.total, //数据总数，从服务端得到
            layout: ['prev', 'page', 'next', 'limit', 'count', 'refresh'],
            jump: function (obj, first) {
                //首次不执行
                if (!first) {
                    beginSearch(obj.curr, obj.limit);
                }
            }
        });
    }

    /**
     * 开始检索
     */
    function beginSearch(page = 1, size = 20) {
        const formVal = form.val('search-form') || {};
        // 如果搜索条件下拉框处于显示状态则隐藏
        if ($('#condition-div').css('display') !== 'none') {
            $('#condition-div').slideToggle('fast');
        }

        let data = {conditions: [], page, size};
        for (let key in formVal) {
            // 如果key是条件下拉框，则跳过
            if (key.startsWith('logic-')) continue;
            //if (!formVal[key]) continue;
            data.conditions.push({
                field: key,
                logic: formVal[`logic-${key}`],
                value: formVal[key]
            });
        }
        $.post(`${ctx}/search/full`, data, function (res) {
            if (res.flag) {
                // 渲染列表
                renderResultList(res.data.records);
                // 渲染分页
                renderPageComponent(res.data)
            }
        });
    }

    /**
     * 渲染结果列表
     * @param list
     */
    function renderResultList(list){
        console.info(list);
        $('#result-list-content').html('');
        let htm = '';
        for(let item of list){
            let title = '';
            if(item.fileName){
                title = item.fileName + item.fileSuffix;
            }
            let text = item.text;

            htm += `<div class="result-item">
                        <p class="result-item-title">${title}</p>
                        <p class="result-item-content">
                            ${item.commonText}
                        </p>
                        <p class="result-item-content">
                            ${text}
                        </p>
                    </div>`;
        }
        $('#result-list-content').html(htm);
    }
});