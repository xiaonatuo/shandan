/**
 * <p>
 *  directory
 * </p>
 *
 * @author Administrator
 * @since 2021/6/1
 */
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
    let fileUploadLayerWin;

    //目录树右键添加和编辑菜单弹框显示的其他内容
    const dirAddLayer = [{
        label: "目录类型", name: "directoryType", type: "select", optionsData: function () {
            return {"0": "结构目录", "1": "元数据目录"}
        }
    }];
    const dirEditLayer = $.extend(dirAddLayer, [])

    let addMetadataLayerWin;
    const openAddMetadataLayer = function (directory, callback) {
        layer.open({
            id: 'addMetadataLayer',
            type: 2,
            area: ['800px', '800px'],
            btn: ['确定', '取消'],
            content: `${ctx}/business/metadata/layer/choose?directoryId=${directory.id}`,
            success: function (layero) {
                addMetadataLayerWin = window[layero.find('iframe')[0]['name']];
            },
            yes: function (index) {
                addMetadataLayerWin && addMetadataLayerWin.ok().then(datas => {
                    console.info(datas);
                    if (datas && Array.isArray(datas) && datas.length > 0) {
                        const ids = datas.map(data => data.id).join(',');
                        $.post(`${ctx}/business/directory/save/metadata`, {
                            directoryId: directory.id,
                            metadataIds: ids
                        }, function (res) {
                            if (res.flag) {
                                layer.msg('保存成功');
                                layer.close(index);
                                callback && callback();
                            } else {
                                layer.msg('保存失败')
                            }
                        })
                    } else {
                        layer.msg('没有选择任何数据')
                    }
                });
            }
        });
    }

    /**
     * 加载元数据列表
     */
    const loadMetadataList = function (directory) {
        const {basicData} = directory;
        if (!basicData) return;
        let operate = {}
        if(basicData.reviewStatus == ReviewStatus.UN_SUBMIT || basicData.reviewStatus == ReviewStatus.FAIL){
            operate = {fixed: 'right', title: '操作', toolbar: '#rowToolBar'}
        }

        metaListTable = listPage.init({
            table: {
                hideFunBtn: basicData.directoryType == 'DIRECTORY' || basicData.reviewStatus == ReviewStatus.SUBMITTED || basicData.reviewStatus == ReviewStatus.PASS,
                id: 'dirMetadataTable',
                toolbar: '#tableToolBar',
                searchFieldNames: 'metadataName',
                url: `${ctx}/business/metadata/list/directory?directoryId=${basicData.id}`,
                method: 'get',
                cols: [[
                    {field: 'id', title: 'ID', hide: true},
                    {field: 'metadataName', title: '元数据表名'},
                    {field: 'metadataComment', title: '中文注释'},
                    {field: 'themeTask', title: '主题任务'},
                    {field: 'dataFrom', title: '数据来源'},
                    {field: 'collectionTime', title: '采集时间'},
                    operate
                ]],
            },
        });
        metaListTable.addTableRowEvent('addLink', function (obj) {
            openAddMetadataLayer(directory.basicData, () => {
                dirTree.partialRefreshAdd(currentTreeNode);
                metaListTable.reloadTable();
            })
        })
        metaListTable.addTableRowEvent('removeLink', function (obj) {
            layer.confirm('是否要解除该条数据的关联？', {}, function (index) {
                const data = {directoryId: basicData.id, metadataId: obj.id};
                $.post(`${ctx}/business/directory/remove/metadata`, data, function (res) {
                    if (res.flag) {
                        dirTree.partialRefreshAdd(currentTreeNode);
                        metaListTable.reloadTable();
                    } else {
                        layer.msg('解除关联失败')
                    }
                    layer.close(index);
                })
            })
        })
        metaListTable.addTableRowEvent('addFile', function(obj){
            layer.open({
                id: 'fileUploadLayer',
                type: 2,
                area:['800px','762px'],
                btn:['上传并保存','取消'],
                content: `${ctx}/sys/file/layer?directoryId=${basicData.id}`,
                success: function (layero,index) {
                    fileUploadLayerWin = window[layero.find('iframe')[0]['name']];
                    layer.iframeAuto(index)
                },
                yes: function (index) {
                    fileUploadLayerWin.save();
                }
            });
        });
    }

    /**
     * 加载元数据详情
     */
    const loadMetadataDetails = function (metadata) {
        const {basicData} = metadata;
        if (!basicData) {
            return
        }
        $('#metadataCardBody ul:first li:first').click();
        if(!basicData.dataSourceId){
            $('#metadataCardBody ul:first li.db-source').hide()
            $('#metadataCardBody ul:first li.file-source').show()
        }else{
            $('#metadataCardBody ul:first li.db-source').show()
            $('#metadataCardBody ul:first li.file-source').hide()
        }
        // 查询元数据基础和详细信息
        $.get(`${ctx}/business/metadata/get/${basicData.id}`, function (res) {
            laytpl($("#metadataBasicTemplate").html()).render(res, function (html) {
                $("#metadataBasicTab").html(html);
            })
        });

        // 查询元数据字段信息
        gtable.init({
            id: 'metadataColumnTable',
            url: `${ctx}/business/metadata/columns?id=${basicData.id}`,
            method: 'get',
            toolbar: '',
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

        // 查询元数据的示例数据
        $.get(`${ctx}/business/metadata/columns?id=${basicData.id}`, function (res) {
            let columns = [];
            if (res.flag) {
                for (let {columnName} of res.data) {
                    columns.push({field: columnName, title: columnName});
                }
            }
            const tableOptions = {
                id: 'metadataExampleTable',
                url: `${ctx}/business/metadata/example/data?metadataId=${basicData.id}`,
                method: 'get',
                cols: [columns],
                page: false,
                toolbar: false,
                height: 'full-135',
            }
            layui.listPage.init({
                table: tableOptions
            })
        });

        // 查询文件列表
        const fileListTable = layui.listPage.init({
            table:{
                id: 'fileListTable',
                url: `${ctx}/sys/file/list`,
                where:{entityId: basicData.id},
                cols: [[
                    {field:'fileName', title:'文件名称'},
                    {field: 'fileSize', title: '文件大小(MB)'},
                    {field: 'fileType', title: '文件类型'},
                    {field: 'right', title:'操作', toolbar:'#fileListTableToolBar', width:150 }
                ]],
                page: false,
                toolbar: false,
                height: 'full-135'
            },
        });
        fileListTable.addTableRowEvent('download', function(obj){
            window.open(`${ctx}/sys/file/download/${obj.id}`)
        });
        let fileViewLayerWin;
        fileListTable.addTableRowEvent('file-view', function(){
            layer.open({
                id: 'fileviewLayer',
                title: basicData.metadataName,
                type: 2,
                content: `${ctx}/sys/file/view?entityId=${basicData.id}`,
                success: function (layero,index) {
                    fileViewLayerWin = window[layero.find('iframe')[0]['name']];
                    layer.full(index);
                },
            });
        })
    }

    // 加载并渲染目录树
    let treeChildrenUrl = `${ctx}/business/directory/tree/children`
    let treeOps = {
        id: 'directoryTree',
        url: treeChildrenUrl,
        data: [{id: '-', parentId: '', title: '根目录', leaf: false, last: false, spread: false}],
        cache: false,
        initLevel: 3, // 默认展开一级
        toolbar: true,
        toolbarStyle: {title: "目录", area: ["600px", "350px"]},
        toolbarShow: ["add", "edit", "delete"],
        toolbarBtn: [dirAddLayer, dirEditLayer],
        done: function (nodes, elem) {
            // 模拟鼠标点击事件展开第一层目录
            $('i.dtree-icon-jia[data-id="-"]').click();
        },
        onClick: function (node) {
            currentTreeNode = node.dom;
            const {basicData} = node.param;
            toggleRightCard(basicData && basicData.metadataName, () => loadMetadataDetails(node.param), () => loadMetadataList(node.param));
        },
        toolbarFun: {
            editTreeLoad: function (node) { // 目录树右键编辑菜单显示弹窗后的回调
                dirTree.changeTreeNodeDone({directoryType: node.basicData.directoryType});
            },
            addTreeNode: function (node, elem) { // 目录树右键新增菜单-点击保存后的回调
                const data = {
                    parentId: node.parentId,
                    directoryName: node.addNodeName,
                    directoryType: node.directoryType
                };
                $.post(`${ctx}/business/directory/save`, data, function (res) {
                    if (res.flag) {
                        dirTree.changeTreeNodeAdd("refresh");
                    } else {
                        layer.msg('保存失败');
                    }
                })
            },
            editTreeNode: function (node, elem) { // 目录树右键编辑菜单-点击确定后的回调
                const data = {
                    id: node.id,
                    directoryName: node.editNodeName,
                    directoryType: node.directoryType
                };
                $.post(`${ctx}/business/directory/save`, data, function (res) {
                    if (res.flag) {
                        dirTree.changeTreeNodeEdit(true);
                    } else {
                        dirTree.changeTreeNodeEdit(false);
                        layer.msg('保存失败');
                    }
                })
            },
            delTreeNode: function (node, elem) {  // 目录树右键删除菜单-点击确定后的回调
                $.delete(`${ctx}/business/directory/delete/${node.id}`, {}, function (res) {
                    if (res.flag) {
                        dirTree.changeTreeNodeDel(true);
                    } else {
                        layer.msg('删除失败');
                    }
                });
            },
            // 显示右键菜单之前的回调，用于设置显示哪些菜单
            loadToolbarBefore: function (buttons, param, $div) {
                const {basicData, id, parentId, context} = param;
                // 根目录只显示添加
                if (id == '-' || parentId == -1 || context == '根目录') {
                    return {addToolbar: buttons.addToolbar};
                }
                if(basicData){
                    // 元数据
                    if(basicData.metadataName){
                        return {};
                    }else{ // 目录
                        const status = basicData.reviewStatus;

                        // 目录类型为结构目录时
                        if(basicData.directoryType == ReviewEntityType.DIRECTORY ) {
                            buttons.reviewToolbar = undefined;
                            buttons.addMetadataToolbar = undefined;
                        }
                        // 审核状态为已提交或者审核通过时，不显示菜单
                        if(status == ReviewStatus.SUBMITTED || status == ReviewStatus.PASS) {
                            return {};
                        }
                    }
                }
                return buttons;
            }
        },
        toolbarExt: [
            {
                toolbarId: "addMetadataToolbar",
                icon: "dtree-icon-roundcheck",
                title: "关联元数据",
                handler: function (node, elem) {
                    const {basicData, id, parentId, context} = node;
                    openAddMetadataLayer(basicData, function () {
                        dirTree.partialRefreshAdd(elem);
                    })
                }
            },
            {
                toolbarId: "reviewToolbar", icon: "dtree-icon-roundcheck", title: "提交审核", handler: function (node) {
                    let param = {
                        entityId: node.id,
                        entityType: ReviewEntityType.DIRECTORY,
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
                }
            },
        ]
    }
    dirTree = globalTree.init(treeOps);

    // 目录树筛选条件
    form.on('select(reviewStatusSelect)', function({elem, othis, value}){
        reviewStatusSearch = value;
        const treeOps = {request:{}}
        if(value){
            treeOps.request = {reviewStatus: value};
        }
        console.info(treeOps);
        globalTree.reload(treeOps)
    });
})