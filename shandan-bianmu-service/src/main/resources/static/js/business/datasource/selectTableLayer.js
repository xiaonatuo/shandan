/**
 * <p>
 *  datasource.js
 * </p>
 *
 * @author Administrator
 * @since 2021/5/24
 */
const checkedTableMap = new Map();
layui.use(['layer', 'listPage'], function () {
    const layer = layui.layer,
        listPage = layui.listPage;
    // 获取请求参数
    const requestParam = layui.url().search;

    listPage.init({
        table:{
            id: 'dbTablesTable',
            url: `${ctx}/business/datasource/table/list`,
            page:{limit: 50},
            where:{datasourceId: requestParam.datasourceId},
            searchFieldNames: ['tableName'],
            height:'full-20',
            cols:[[
                {type: 'checkbox', title: '选择', width: 50},
                {field: 'tableName', title: '数据表'},
                {field: 'tableComment', title: '表注释'},
            ]],
            onChecked: function(obj){
                const checkDatas = listPage.getTableCheckStatus();
                checkedTableMap.clear();
                for(let data of checkDatas.data){
                    checkedTableMap.set(data.tableName, data);
                }
            },
        }
    });
});

async function ok(){
    return checkedTableMap;
}