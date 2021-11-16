const ViewType = {
    org: '部门视图',
    year: '年度视图',
    troop: '部队编号视图',
    equipment: '武器型号视图',
    task: '任务代号视图',
}
layui.use(['form', 'dropdown', 'orgTree'], function () {
    layui.form.on('select(viewTypeSelector)', function ({value}) {
        $('#views-content').html('<p style="text-align: center">正在加载。。。</p>');
        Util.get(`/views/${value}`).then(res => {
            if (res.flag) {
                switch (value) {
                    case 'org':
                        initOrgView(res.data)
                        break;
                    case 'year':
                    case 'troop':
                    case 'equipment':
                    case 'task':
                        initNormalView(res.data)
                }
            }
        })
    });

    /**
     * 初始化年度视图
     * @param data
     */
    function initNormalView(data) {
        let viewHtml = '';
        if(data.views && data.views.length > 0){
            data.views.sort((a, b) =>b.title == '其他' ? -1 : 1);
            for (let view of data.views) {
                viewHtml += `
                <li><div class="layui-menu-body-title">
                    <a href="javascript:void(0);" tppabs="" data-name="${view.id}">
                        <span>${view.title}</span>
                        <span class="layui-font-12 layui-font-gray"></span>
                    </a>
                </div></li>`
            }
            let htm = `<ul class="layui-menu layui-menu-lg">${viewHtml}</ul>`;
            $('#views-content').html(htm);

            $('#views-content ul .layui-menu-body-title a').on('click', function () {
                let viewName = $(this).data('name');

            });
        }else{
            $('#views-content').html('<p style="text-align: center">暂无数据</p>');
        }
    }

    /**
     * 初始化部门视图
     * @param data
     */
    function initOrgView(data) {
        layui.orgTree.init({
            id: `views-content`,
            done: function (nodes, elem) {
            },
            onClick: function (obj) {

            }
        })
    }

});