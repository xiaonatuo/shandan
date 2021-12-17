/**
 * <p>
 *  directory
 * </p>
 *
 * @author Administrator
 * @since 2021/6/1
 */
// 目录树数据缓存
const dirCache = new Map();
layui.use(['layer', 'listPage', 'globalTree', 'gtable', 'form'], function () {
    const layer = layui.layer,
        listPage = layui.listPage,
        globalTree = layui.globalTree,
        form = layui.form;
    // 目录树
    let dirTree;
    let metaListTable;


    /**
     * 加载数据资源列表
     */
    const loadMetadataList = function (directory) {
        const {basicData} = directory;
        if (!basicData) return;

        metaListTable = listPage.init({
            table: {
                hideFunBtn: basicData.directoryType == 'DIRECTORY' || basicData.reviewStatus == ReviewStatus.SUBMITTED || basicData.reviewStatus == ReviewStatus.PASS,
                id: 'dirMetadataTable',
                toolbar: '#tableToolBar',
                searchFieldNames: 'metadataName',
                url: `${ctx}/business/metadata/list/directory?directoryId=${basicData.id}`,
                height: 'full-110',
                method: 'get',
                cols: [[
                    {field: 'id', title: 'ID', hide: true},
                    {field: 'metadataName', title: '数据表', width: 300},
                    {field: 'metadataComment', title: '中文注释'},
                    {field: 'themeTask', title: '主题任务'},
                    {field: 'dataFrom', title: '数据来源'},
                    {field: 'createTime', title: '注册时间', width: 160, align: 'center'},
                    {fixed: 'right', title: '操作', toolbar: '#rowToolBar', width: 100, align: 'center'}
                ]],
            },
        });

        // 查看按钮监听
        metaListTable.addTableRowEvent('details', function (obj) {
            if (obj.dataSourceId.startsWith('file_')) {
                const datasourceId = obj.dataSourceId.split('_')
                openMaxLayerWithURL(`${ctx}/sys/file/view?entityId=${datasourceId[1]}`)
            } else {
                openMaxLayerWithURL(`${ctx}/business/metadata/details/${obj.id}`)
            }
        })
    }


    let tempNode;
    // 加载并渲染目录树
    let treeOps = {
        id: 'directoryTree',
        url: `${ctx}/business/directory/tree`,
        data: [{id: '-', parentId: '', title: '资源目录', leaf: false, last: false, spread: false}],
        cache: true,
        type: 'load',
        initLevel: 1, // 默认展开一级
        scroll: '#tree-toobar-div',
        width: 'fit-content',
        sendSuccess: function (res) {
            if (res.flag) {
                res.data.forEach(item => {
                    dirCache.set(item.id, item);
                })

                setTimeout(() => {
                    $('.dtree-nav-div.dtree-theme-item').each((index, elem) => {
                        let data = $(elem).data('basic');
                        if (data) {
                            if (typeof data === 'string') {
                                data = JSON.parse(data);
                            }
                            let title = data.metadataComment || data.directoryName || data.fileName + data.fileSuffix;
                            $(elem).attr('title', title)
                        }
                    });
                }, 200)
            }
        },
        done: function (nodes, elem) {
            // 模拟鼠标点击事件展开第一层目录
            elem.find('li:first>div:first>i:first').click();
        },
        onClick: function (node) {
            tempNode = node;
            const {basicData} = node.param;
            if (!basicData) return false;
            if (basicData.directoryPath) {
                let path = basicData.directoryPath.replaceAll('/', ' / ');
                $('#currentPosition').text(path);
            }
            loadMetadataList(node.param)
        },

    }
    dirTree = globalTree.init(treeOps);

    $('#directoryTreeSearchBox').on('keyup', function () {
        setTimeout(() => {
            dirTree.fuzzySearch(this.value);
        }, 500)
    })
})