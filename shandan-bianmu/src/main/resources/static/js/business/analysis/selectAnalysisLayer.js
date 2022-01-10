/**
 * <p>
 *  selectAnalysisLayer.js
 * </p>
 *
 * @author Administrator
 * @since 2022/1/6
 */
let chooseData = [];
layui.use(['layer', 'listPage','table'], function () {
    const layer = layui.layer,
        listPage = layui.listPage;
    let table = layui.table;
    // 获取请求参数
    //const requestParam = layui.url().search;
    listPage.init({
        table:{
            id: 'dbTablesTable',
            url: `${ctx}/business/metadata/page`,
            page:{limit: 50},
            searchFieldNames: ['metadataName'],
            where:{reviewStatus: "PASS"},
            height:'full-20',
            cols:[[
                {type: 'radio', title: '选择', width: 50},
                {field: 'metadataName', title: '数据名称'},
                {field: 'metadataComment', title: '注释'},
            ]],
            done: function(res, curr, count) {
                // 监听单选单击事件,其中my_table_1是table的lay-filter的属性值
                table.on('row(dbTablesTable)',function(obj){
                    //其中，obj.data是单选后当前行的数据
                    chooseData = obj;
                });
            },
        }
    });
});

async function ok(){
    return chooseData;
}