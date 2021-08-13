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
    let fileUploadLayerWin;

    //目录树右键添加和编辑菜单弹框显示的其他内容
    const dirAddLayer = [{
        label: "目录类型", name: "directoryType", type: "select", optionsData: function () {
            return {"DIRECTORY": "结构目录", "METADATA": "元数据目录"}
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
        if (basicData.reviewStatus == ReviewStatus.UN_SUBMIT || basicData.reviewStatus == ReviewStatus.FAIL) {
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
        metaListTable.addTableRowEvent('addFile', function (obj) {
            layer.open({
                id: 'fileUploadLayer',
                type: 2,
                area: ['800px', '600px'],
                btn: ['开始上传', '取消'],
                content: `${ctx}/sys/file/layer?directoryId=${basicData.id}`,
                success: function (layero, index) {
                    fileUploadLayerWin = window[layero.find('iframe')[0]['name']];
                    layer.iframeAuto(index)
                },
                yes: function (index) {
                    fileUploadLayerWin.save().then(ok => {
                        ok && dirTree.partialRefreshAdd(currentTreeNode);
                    });
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
        if (!basicData.dataSourceId) {
            $('#metadataCardBody ul:first li.db-source').hide()
            $('#metadataCardBody ul:first li.file-source').show()
        } else {
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
            height: 'full-136',
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
                height: 'full-135',
            }
            layui.listPage.init({
                table: tableOptions
            })
        });

        // 查询文件列表
        const fileListTable = layui.listPage.init({
            table: {
                id: 'fileListTable',
                url: `${ctx}/sys/file/list`,
                where: {entityId: basicData.id},
                cols: [[
                    {field: 'fileName', title: '文件名称'},
                    {field: 'fileSize', title: '文件大小(MB)'},
                    {field: 'fileType', title: '文件类型'},
                    {field: 'right', title: '操作', toolbar: '#fileListTableToolBar', width: 150}
                ]],
                page: false,
                toolbar: false,
                height: 'full-135'
            },
        });
        fileListTable.addTableRowEvent('download', function (obj) {
            window.open(`${ctx}/sys/file/download/${obj.id}`)
        });
        let fileViewLayerWin;
        fileListTable.addTableRowEvent('file-view', function () {
            layer.open({
                id: 'fileviewLayer',
                title: basicData.metadataName,
                type: 2,
                content: `${ctx}/sys/file/view?entityId=${basicData.id}`,
                success: function (layero, index) {
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
        onDbClick: function ({dom}) {
            dirTree.clickSpread(dom);
        },
        sendSuccess: function (res) {
            if (res.flag) {
                res.data.forEach(item => {
                    dirCache.set(item.id, item);
                })
                initLiHoverEvent();
                async function initLiHoverEvent() {
                    setTimeout(() => {
                        $('.dtree-nav-div.dtree-theme-item').each((index, elem)=>{
                            let data = $(elem).data('basic');
                            if (data) {
                                if(typeof data === 'string'){data = JSON.parse(data);}
                                let title = data.metadataComment || data.directoryName || data.fileName + data.fileSuffix;
                                $(elem).attr('title', title)
                            }
                        });
                    }, 200)
                }
            }
        },
        done: function (nodes, elem) {
            // 模拟鼠标点击事件展开第一层目录
            $('i.dtree-icon-jia[data-id="-"]').click();
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
        toolbarFun: {
            editTreeLoad: function (node) { // 目录树右键编辑菜单显示弹窗后的回调
                $('.layui-layer .dtree-toolbar-tool .layui-form-item:first').addClass('layui-hide');
                const {directoryName, directoryType} = node.basicData;
                dirTree.changeTreeNodeDone({directoryName, directoryType, editNodeName: directoryName});
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
                        let self = dirCache.get(node.parentId)
                        self.basicData.hasChild = true;
                        dirCache.set(node.parentId, self);
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
                        dirCache.delete(node.id);
                        dirTree.changeTreeNodeDel(true);
                        // 需要判断父级目录是否还保存子目录
                        // 遍历缓存数据，如果数据中不包含parentId为node.parentId的数据，则证明没有子目录，需要更新父目录hasChild字段
                        let parent = dirCache.get(node.parentId)
                        let hasChild = false;
                        dirCache.forEach((value, key) => {
                            if (value.parentId === node.parentId) {
                                hasChild = true
                            }
                        })
                        parent.basicData.hasChild = hasChild;
                        dirCache.set(node.parentId, parent);
                    } else {
                        layer.msg('删除失败');
                    }
                });
            },
            // 显示右键菜单之前的回调，用于设置显示哪些菜单
            loadToolbarBefore: function (buttons, param, $div) {
                const {id, parentId, context} = param;
                let btns = {};
                // 根目录只显示添加
                if (id === '-' || parentId === -1 || context === '根目录') {
                    btns = {addToolbar: buttons.addToolbar};
                } else {
                    const basicData = dirCache.get(id).basicData;
                    if (basicData) {
                        // 元数据 或者文件
                        if (basicData.metadataName || basicData.entityId) {
                            const parent = dirCache.get(parentId).basicData;
                            let status = parent.reviewStatus;
                            // 审核状态为已提交或者审核通过时，不显示菜单
                            if (status === ReviewStatus.SUBMITTED || status === ReviewStatus.PASS) {
                                btns = {noneToolbar: buttons.noneToolbar};
                            } else {
                                btns = {removeLinkToolbar: buttons.removeLinkToolbar};
                            }
                        } else { // 目录
                            let status = basicData.reviewStatus; // 目录审核状态

                            // 目录类型为结构目录时
                            if (basicData.directoryType === ReviewEntityType.DIRECTORY) {
                                // 显示新增目录
                                btns.addToolbar = buttons.addToolbar;
                                if (!basicData.hasChild) {
                                    btns.editToolbar = buttons.editToolbar;
                                    btns.delToolbar = buttons.delToolbar;
                                }
                            } else { // 目录类型为元数据时， 判断对象为父目录
                                // 审核状态为已提交或者审核通过时，不显示菜单
                                if (status === ReviewStatus.SUBMITTED || status === ReviewStatus.PASS) {
                                    btns = {noneToolbar: buttons.noneToolbar};
                                } else {
                                    btns.editToolbar = buttons.editToolbar;
                                    btns.delToolbar = buttons.delToolbar;
                                    btns.addMetadataToolbar = buttons.addMetadataToolbar;
                                    btns.reviewToolbar = buttons.reviewToolbar;
                                }
                            }
                        }
                    } else {
                        btns = {noneToolbar: buttons.noneToolbar}
                    }
                }
                return btns;
            }
        },
        toolbarExt: [
            {
                toolbarId: "addMetadataToolbar",
                icon: "layui-icon layui-icon-link",
                title: "关联元数据",
                handler: function (node, elem) {
                    const {basicData, id, parentId, context} = node;
                    openAddMetadataLayer(basicData, function () {
                        dirTree.partialRefreshAdd(elem);
                    })
                }
            },
            {
                toolbarId: "reviewToolbar",
                icon: "layui-icon layui-icon-release",
                title: "提交审核", handler: function (node) {
                    let param = {
                        entityId: node.id,
                        entityType: ReviewEntityType.DIRECTORY,
                        status: ReviewStatus.SUBMITTED
                    };
                    $.post(`${ctx}/business/review/operate`, param, function (res) {
                        if (res.flag) {
                            layer.msg('提交成功');
                            metaListTable.reloadTable();
                        } else {
                            layer.msg('提交失败,' + res.msg);
                        }
                    });
                }
            },
            {
                toolbarId: "removeLinkToolbar",
                icon: "layui-icon layui-icon-unlink",
                title: "解除关联", handler: function (node, dom) {
                    layer.confirm('是否要解除该条数据的关联？', {}, function (index) {
                        const {basicData, parentId} = node;
                        let data, url;
                        if (basicData.metadataName) {
                            url = `${ctx}/business/directory/remove/metadata`
                            data = {directoryId: parentId, metadataId: basicData.id};
                        } else {
                            url = `${ctx}/business/directory/remove/file`
                            data = {fileId: basicData.id};
                        }
                        $.post(url, data, function (res) {
                            if (res.flag) {
                                dirTree.partialRefreshDel(dom);
                            } else {
                                layer.msg('解除关联失败')
                            }
                            layer.close(index);
                        })
                    })
                }
            },
            {
                toolbarId: "noneToolbar",
                icon: "",
                title: "无可操作选项", handler: function (node) {
                }
            },
        ],
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

    let player;

    /**
     * 预览文件
     * @param file
     */
    function viewFile(file) {
        const fileSuffix = file.fileSuffix.toLowerCase();

        const filePath = `${ctx}/upload/${file.path}`;
        if (viewType.image.includes(fileSuffix)) {
            let htm = `<img id="image-viewer" src="${filePath}" style="max-height: ${imageHeight}px; max-width: ${imageWidth}px"/>`
            $('#file-viewer-image').html(htm)
            showFileViewer('image');
        } else if (viewType.video.includes(fileSuffix) || viewType.audio.includes(fileSuffix)) {
            if (!player) {
                initVideoPlayer();
            }
            player.src(filePath);
            showFileViewer('video');
        } else if (viewType.pdf.includes(fileSuffix)) {
            $('#pdfViewer').attr('src', filePath);
            showFileViewer('pdf');
        } else if (viewType.text.includes(fileSuffix)) {
            $('#txtViewer').attr('src', filePath);
            showFileViewer('text');
        } else {
            let htm = `<p>该文件不支持预览，可下载后查看。</p>
                        <a href="javascript:void(0)"  id="download-file" data-id="${file.id}" style="color: blue; text-decoration: underline;">下载文件</a>`;
            $('#file-viewer-other').html(htm)
            $('#download-file').off('click')
            $('#download-file').on('click', function ({target}) {
                let fileId = $(target).data('id')
                window.open(`${ctx}/sys/file/download/${fileId}`)
            })
            showFileViewer('other');
        }
    }

    /**
     * 视频播放器初始化
     */
    function initVideoPlayer() {
        player = videojs('videoPlayer');
        player.ready(function () {
            //this.play();
        });
    }

    // 目录树筛选条件
    form.on('select(reviewStatusSelect)', function ({elem, othis, value}) {
        reviewStatusSearch = value;
        const treeOps = {request: {}}
        if (value) {
            treeOps.request = {reviewStatus: value};
        }
        globalTree.reload(treeOps)
    });
})