/**
 * <p>
 *  datasource.js
 * </p>
 *
 * @author Administrator
 * @since 2021/5/24
 */
layui.use(['layer', 'listPage', 'form'], function () {
    const layer = layui.layer;
    const form = layui.form;

    let reviewStatusSearch = '';

    const listPage = layui.listPage.init({
        deleteUrl: `${ctx}/business/metadata/delete`,
        table: {
            id: 'metadataTable',
            searchFieldNames:'metadataName',
            url: `${ctx}/business/metadata/page`,
            cols: [[
                {field: 'id', title: 'ID', hide: true},
                {field: 'metadataName', title: '元数据表名'},
                {field: 'metadataComment', title: '中文注释'},
                {field: 'themeTask', title: '主题任务'},
                {field: 'dataFrom', title: '数据来源', width:100},
                {field: 'collectionTime', title: '采集时间', width:180, align: 'center'},
                {field: 'reviewStatus', title: '状态', width:120, align:'center', templet: (data) => {
                        let colorClass = 'layui-bg-blue';
                        switch (data.reviewStatus) {
                            case ReviewStatus.SUBMITTED:
                                colorClass = 'layui-bg-orange';
                                break;
                            case ReviewStatus.PASS:
                                colorClass = 'layui-bg-green';
                                break;
                            case ReviewStatus.FAIL:
                                colorClass = 'layui-bg-badge';
                        }
                        return `<span class="layui-badge ${colorClass}">${ReviewStatusMsg[data.reviewStatus]}</span>`
                    }},
                {fixed: 'right', title: '操作', toolbar: '#rowToolBar', width:300, align:'right'}
            ]],
            done: data => {
                form.val('metadataSearchForm',{reviewStatusSelect:reviewStatusSearch});
                form.on('select(reviewStatusSelect)', function({elem, othis, value}){
                    reviewStatusSearch = value;
                    const table = {where:{current: 1}}
                    if(value){
                        table.where.reviewStatus = value;
                    }

                    const searchText = $('#searchKeyInput').val();
                    if(searchText){
                        table.where.metadataName = searchText;
                    }

                    listPage.reloadTable({table})
                })
            }
        },
        editPage: {
            content: `${ctx}/business/metadata/edit`,
            area: ['800px', '800px']
        }
    });
    listPage.addTableRowEvent('exampleData', function(data){
        layer.open({
            title: '示例数据',
            type: 2,
            area:['900px', '600px'],
            content: `${ctx}/business/metadata/example?metadataId=${data.id}`
        })
    });
    listPage.addTableRowEvent('submitReview', function (data) {
        let param = {
            entityId: data.id,
            entityType: ReviewEntityType.METADATA,
            status: ReviewStatus.SUBMITTED
        };
        $.post(`${ctx}/business/review/operate`, param, function(res){
            if(res.flag){
                layer.msg('提交成功');
                listPage.reloadTable();
            }else{
                layer.msg('提交失败,' + res.msg);
            }
        });
    });
});