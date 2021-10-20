/**
 * <p>
 *  datasource.js
 * </p>
 *
 * @author Administrator
 * @since 2021/5/24
 */
let chooseData = [];
layui.use(['layer', 'listPage'], function () {
    const layer = layui.layer;

    const listPage = layui.listPage.init({
        deleteUrl: `${ctx}/business/metadata/delete`,
        table: {
            id: 'metadataTable',
            searchFieldNames: 'metadataName',
            url: `${ctx}/business/metadata/page`,
            where:{reviewStatus: 'PASS'},
            cols: [[
                {field: 'id', title: 'ID', hide: true},
                {field: '',type: 'checkbox'},
                {field: 'metadataName', title: '数据表'},
                {field: 'metadataComment', title: '中文注释'},
                {field: 'themeTask', title: '主题任务'},
                {field: 'dataFrom', title: '数据来源'},
                {field: 'dataType', title: '数据类型'},
                {field: 'collectionTime', title: '采集时间'},
            ]],
            onChecked: function () {
                chooseData = listPage.getCheckStatus().data;
            }
        }
    });
});

/**
 * 暴露给父页面的回调
 * @returns {Promise<*[]>}
 */
async function ok(){
    return chooseData;
}