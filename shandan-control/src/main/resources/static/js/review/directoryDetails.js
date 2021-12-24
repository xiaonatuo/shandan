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
layui.use(['layer', 'listPage', 'globalTree', 'laytpl', 'gtable', 'form'], function () {
    const layer = layui.layer,
        listPage = layui.listPage,
        globalTree = layui.globalTree,
        laytpl = layui.laytpl,
        gtable = layui.gtable
    form = layui.form;
    // 目录树
    let dirTree;
    let currentTreeNode;
    let metaListTable;
    let reviewStatusSearch = '';

    /**
     * 加载数据资源列表
     */
    const loadMetadataList = function (directory) {
        const {basicData} = directory;
        if (!basicData) return;
        let operate = {}
        if (basicData.reviewStatus == ReviewStatus.UN_SUBMIT || basicData.reviewStatus == ReviewStatus.FAIL) {
            operate = {fixed: 'right', title: '操作', toolbar: '#rowToolBar'}
        }

        metaListTable = listPage.init({
            table: {
                hideFunBtn: basicData.directoryType == 'DIRECTORY' || basicData.reviewStatus == ReviewStatus.SUBMITTED || basicData.reviewStatus == ReviewStatus.PASS,
                id: 'dirMetadataTable',
                toolbar: '#tableToolBar',
                height: 'full-105',
                searchFieldNames: 'metadataName',
                url: `${ctx}/business/metadata/list/directory?directoryId=${basicData.id}`,
                method: 'get',
                cols: [[
                    {field: 'id', title: 'ID', hide: true},
                    {field: 'metadataName', title: '数据名称'},
                    {field: 'metadataComment', title: '注释'},
                    {field: 'themeTask', title: '主题任务'},
                    {field: 'dataFrom', title: '数据来源'},
                    {field: 'collectionTime', title: '采集时间'},
                    operate
                ]],
                done: function () {
                    //审核按钮组点击事件监听
                    $('#reviewBtnBox button').on('click', function () {
                        let status = $(this).data('status');
                        window.parent.ReviewComponent.openReviewLayer(data.id, status, ()=>{
                            window.location.reload();
                        });

                    });
                }
            },
        });
    }

    /**
     * 加载数据资源详情
     */
    const loadMetadataDetails = function (metadata) {
        const {basicData} = metadata;
        if (!basicData) {
            return
        }
        $('#metadataCardBody ul:first li:first').click();
        if (!basicData.dataSourceId) {
            $('#metadataCardBody ul:first li.db-source').hide()
            $('#metadataCardBody ul:first li.file-source').show()
        } else {
            $('#metadataCardBody ul:first li.db-source').show()
            $('#metadataCardBody ul:first li.file-source').hide()
        }
        // 查询数据资源基础和详细信息
        $.get(`${ctx}/business/metadata/get/${basicData.id}`, function (res) {
            laytpl($("#metadataBasicTemplate").html()).render(res, function (html) {
                $("#metadataBasicTab").html(html);
            })
        });

        // 查询数据资源字段信息
        gtable.init({
            id: 'metadataColumnTable',
            url: `${ctx}/business/metadata/columns?id=${basicData.id}`,
            method: 'get',
            toolbar: '',
            height: 'full-105',
            page: false,
            cols: [[
                {field: 'tableName', title: 'tableName', hide: true},
                {field: 'columnName', title: '列名'},
                {field: 'comment', title: '注释'},
                {field: 'dataType', title: '数据类型'},
                {field: 'dataLength', title: '列长度'},
                {field: 'dataScale', title: '数值刻度', width: 90},
                {field: 'dataPrecision', title: '数值精度', width: 90},
                {field: 'nullAble', title: '允许为NULL'},
                {field: 'dataDefault', title: '默认值'},
            ]],
            // done: (obj) => console.info(obj)
        });

        // 查询数据资源的示例数据
        $.get(`${ctx}/business/metadata/columns?id=${basicData.id}`, function (res) {
            let columns = [];
            if (res.flag) {
                for (let {columnName} of res.data) {
                    columns.push({field: columnName, title: columnName, minWidth: 150});
                }
            }
            const tableOptions = {
                id: 'metadataExampleTable',
                url: `${ctx}/business/metadata/example/data?metadataId=${basicData.id}`,
                method: 'get',
                cols: [columns],
                page: false,
                toolbar: true,
                defaultToolbar: ['filter'],
                height: 'full-110',
            }
            layui.listPage.init({
                table: tableOptions
            })
        });

    }

    // 加载并渲染目录树
    let treeOps = {
        id: 'directoryTree',
        data: [{id: '-', parentId: '', title: '资源目录', leaf: false, last: false, spread: true,children: treeData}],
        cache: true,
        initLevel: 3, // 默认展开一级
        onDbClick: function ({dom}) {
            dirTree.clickSpread(dom);
        },
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
            $(`div.dtree-theme-item[data-id="${data.id}"]`).click();
        },
        onClick: function (node) {
            currentTreeNode = node.dom;
            const {basicData} = node.param;
            let showType = 'directory';
            let callback = () => {
                loadMetadataList(node.param)
            }
            if (basicData) {
                if (basicData.metadataName) {
                    showType = 'metadata';
                    callback = () => {
                        loadMetadataDetails(node.param)
                    }
                } else if (basicData.entityId) {
                    showType = 'file';
                    callback = () => {
                        viewFile(basicData);
                    }
                }
            }
            toggleRightCard(showType, callback);

            // 先移除事件，否则会和节点点击事件重叠
            $('#directoryTree cite i.icon-fail').off('click');
            $('#directoryTree cite i.icon-fail').on('click', function ({target}) {
                const id = $(target).data('id');
                const elemId = `tips-rw-${id}`;
                $.get(`${ctx}/business/review/get/entity?entityId=${id}`, {}, function (res) {
                    if (res.flag) {
                        const reviewData = res.data;
                        layer.tips(reviewData.reviewOpinion, `#${elemId}`, {tips: [3]});
                    }
                })
            })
        },
        formatter: {
            title: function (data) { // 文字过滤，返回null,"",undefined之后，都不会改变原有的内容返回。
                let {title, basicData} = data;
                if (basicData && basicData.directoryType === ReviewEntityType.METADATA) {
                    const reviewStatus = data.basicData.reviewStatus;
                    title += ReviewStatusIcon[reviewStatus].replace('##id##', basicData.id);
                }
                return title;
            }
        }
    }
    dirTree = globalTree.init(treeOps);

})