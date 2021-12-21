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
    const listPage = layui.listPage,
        globalTree = layui.globalTree;

    // 初始化
    initDirectoryTree();

    // 加载并渲染目录树
    function initDirectoryTree() {
        let dirTree = globalTree.init({
            id: 'directoryTree',
            url: `${ctx}/business/directory/tree`,
            request: {reviewStatus: ReviewStatus.PASS},
            data: [{id: '-', parentId: '', title: '资源目录', leaf: false, last: false, spread: false}],
            cache: true,
            type: 'load',
            initLevel: 1, // 默认展开一级
            scroll: '#tree-toobar-div',
            width: 'fit-content',
            done: treeInitDone,
            onClick: nodeClickEventListener,
            sendSuccess: fetchRequestSendSuccess,
        });

        /**
         * 树组件请求数据接口成功后的回调函数
         * @param res
         */
        function fetchRequestSendSuccess(res) {
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
        }

        /**
         * 树组件初始化完成回调函数
         * @param nodes
         * @param elem
         */
        function treeInitDone(nodes, elem) {
            // 模拟鼠标点击事件展开第一层目录
            elem.find('li:first>div:first>i:first').click();
            elem.find('li:first>div:first').click();
            // 搜索事件监听
            treeSearchEventListener()
        }

        /**
         * 节点点击事件监听
         * @param node
         */
        function nodeClickEventListener(node) {
            let {basicData} = node.param;
            basicData = basicData || {id: '-'};
            //设置当前位置
            setCurrentPosition(basicData);
            // 初始化数据资源表格
            initMetadataTable(basicData.id)
            //初始化文件搜索表格
            initFileSearchTable(basicData.id);
        }

        /**
         * 设置当前位置
         * @param basicData
         */
        function setCurrentPosition(basicData) {
            let directoryPath = ' / 资源目录'
            if (basicData.directoryPath) {
                directoryPath += basicData.directoryPath.replaceAll('/', ' / ');
            }
            $('.current-position label').text(directoryPath);
        }

        /**
         * 树组件搜索事件监听
         */
        function treeSearchEventListener() {
            $('#directoryTreeSearchBox').on('keyup', function () {
                setTimeout(() => {
                    dirTree.fuzzySearch(this.value);
                }, 500)
            })
        }
    }

    /**
     * 加载数据资源列表
     */
    function initMetadataTable(id) {
        let metaListTable = listPage.init({
            table: {
                id: 'dirMetadataTable',
                toolbar: '#tableToolBar',
                searchFieldNames: 'metadataName',
                url: `${ctx}/business/metadata/list/directory?directoryId=${id}`,
                height: 'full-110',
                method: 'get',
                cols: [[
                    {field: 'id', title: 'ID', hide: true},
                    {field: 'metadataName', title: '资源名称', width: 300},
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
                openMaxLayerWithURL(`${ctx}/search/meta/${obj.id}`)
            }
        })
    }

    function initFileSearchTable(id) {
        let metaListTable = listPage.init({
            table: {
                id: 'dirFileTable',
                toolbar: '#fileTableToolBar',
                searchInput: 'fileSearchKeyInput',
                searchFieldNames: 'search',
                url: `${ctx}/search/full/file?metaId=${id}`,
                height: 'full-110',
                limit: 30,
                method: 'get',
                cols: [[
                    {field: 'id', title: 'ID', hide: true},
                    {field: 'fileName', title: '文件名称', width: 300},
                    {field: 'source', title: '文件来源'},
                    {field: 'taskCode', title: '任务代号'},
                    {field: 'taskNature', title: '任务性质'},
                    {field: 'troopCode', title: '部队代号'},
                    {field: 'missileNumber', title: '导弹编号'},
                    {field: 'equipmentModel', title: '装备型号'},
                    {field: 'targetNumber', title: '目标/靶标类型'},
                    {field: 'entryStaff', title: '录入人员'},
                    {field: 'inputDate', title: '收文时间', width: 160, align: 'center'},
                    {fixed: 'right', title: '操作', toolbar: '#rowToolBar', width: 100, align: 'center'}
                ]],
                done: function (response) {
                    console.info(response);
                }
            },
        });

        // 查看按钮监听
        metaListTable.addTableRowEvent('details', function (obj) {
            if (obj.dataSourceId.startsWith('file_')) {
                const datasourceId = obj.dataSourceId.split('_')
                openMaxLayerWithURL(`${ctx}/sys/file/view?entityId=${datasourceId[1]}`)
            } else {
                openMaxLayerWithURL(`${ctx}/search/meta/${obj.id}`)
            }
        })
    }
})