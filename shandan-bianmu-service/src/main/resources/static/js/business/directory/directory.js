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
layui.use(['layer', 'listPage', 'globalTree', 'laytpl', 'gtable', 'form', 'dict'], function () {
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
            return {"DIRECTORY": "结构目录", "METADATA": "资源目录"}
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
                height: 'full-110',
                method: 'get',
                cols: [[
                    {field: 'id', title: 'ID', hide: true},
                    {field: 'metadataName', title: '数据表'},
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
                area: ['850px', '600px'],
                btn: ['开始上传', '取消'],
                content: `${ctx}/sys/file/layer?directoryId=${basicData.id}`,
                success: function (layero, index) {
                    fileUploadLayerWin = window[layero.find('iframe')[0]['name']];
                    //layer.iframeAuto(index)
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
                layui.dict.render();
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
                for (let {columnName, comment} of res.data) {
                    columns.push({field: columnName, title: comment || columnName, minWidth: 150});
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
                height: 'full-105',
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

    let tempNode;
    // 加载并渲染目录树
    let treeChildrenUrl = `${ctx}/business/directory/tree`
    let treeOps = {
        id: 'directoryTree',
        url: treeChildrenUrl,
        data: [{id: '-', parentId: '', title: '资源目录', leaf: false, last: false, spread: false}],
        cache: true,
        type: 'load',
        initLevel: 1, // 默认展开一级
        scroll: '#tree-toobar-div',
        width: 'fit-content',
        toolbar: true,
        toolbarShow: [], //置空默认菜单项
        onDbClick: function ({dom}) {
            //dirTree.clickSpread(dom);
            dom.find('cite').click();
        },
        sendSuccess: function (res) {
            if (res.flag) {
                res.data.forEach(item => {
                    dirCache.set(item.id, item);
                })
                initLiHoverEvent();

                async function initLiHoverEvent() {
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
        },
        done: function (nodes, elem) {
            console.info(nodes);
            // 模拟鼠标点击事件展开第一层目录
            $('i.dtree-icon-jia[data-id="-"]').click();
        },
        onClick: function (node) {
            tempNode = node;
            currentTreeNode = node.dom;
            const {basicData} = node.param;
            if (!basicData) return false;
            if (basicData.directoryPath) {
                let path = basicData.directoryPath.replaceAll('/', ' / ');
                $('#currentPosition').text(path);
            }
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
            // 显示右键菜单之前的回调，用于设置显示哪些菜单
            loadToolbarBefore: function (buttons, param, $div) {
                console.info(param);
                const {id, parentId, context, basicData} = param;
                if (basicData) {
                    switch (basicData.reviewStatus) {
                        case ReviewStatus.SUBMITTED:
                        case ReviewStatus.PASS:
                            setDisabledButtons(['toolbar_dir_add', 'toolbar_dir_rename', 'toolbar_dir_delete', 'toolbar_dir_link', 'toolbar_dir_submit']);
                            break;
                        case ReviewStatus.UN_SUBMIT:
                        case ReviewStatus.FAIL:
                            setDisabledButtons();
                            break
                        case ReviewStatus.REJECTED:
                            setDisabledButtons(['toolbar_dir_add', 'toolbar_dir_rename', 'toolbar_dir_link', 'toolbar_dir_submit']);
                    }
                } else {
                    setDisabledButtons(['toolbar_dir_rename', 'toolbar_dir_delete', 'toolbar_dir_link', 'toolbar_dir_submit']);
                }

                /**
                 * 设置需要禁用的按钮
                 * @param buttonIds 按钮ID数组
                 */
                function setDisabledButtons(buttonIds = []) {
                    for (let btnName of buttonIds) {
                        let $btn = $(buttons[btnName]);
                        let $a = $btn.find('a').addClass('layui-disabled');
                        $btn.html($a[0]);
                        buttons[btnName] = $btn[0];
                    }
                }

                return buttons;
            }
        },
        toolbarExt: [
            {
                toolbarId: "toolbar_dir_add",
                icon: "dtreefont dtree-icon-weibiaoti5",
                title: "新建目录",
                handler: function (node) {
                    const {id} = node;
                    openDirectoryEditLayer({parentId: id})
                }
            },
            {
                toolbarId: "toolbar_dir_rename",
                icon: "dtreefont dtree-icon-bianji",
                title: "重命名目录",
                handler: function (node) {
                    const {basicData} = node;
                    openDirectoryEditLayer(basicData)
                }
            },
            {
                toolbarId: "toolbar_dir_delete",
                icon: "dtreefont dtree-icon-delete1",
                title: "删除目录",
                handler: function (node) {
                    const {id, parentId} = node;
                    layer.confirm('是否确定删除该目录？', function (index) {
                        Util.send(`/business/directory/delete/${id}`, {}, 'delete').then(res => {
                            if (res.flag) {
                                let $dom = $(`div[data-id='${parentId}'][dtree-id='directoryTree']`);
                                dirTree.getChild($dom);
                                layer.close(index);
                            } else {
                                showErrorMsg(res.msg);
                            }
                        }).catch(() => showErrorMsg());
                    });
                }
            },
            {
                toolbarId: "toolbar_dir_link",
                icon: "layui-icon layui-icon-link",
                title: "关联数据资源",
                handler: function (node, elem) {
                    const {basicData, id, parentId, context} = node;
                    openAddMetadataLayer(basicData, function () {
                        dirTree.partialRefreshAdd(elem);
                    })
                }
            },
            {
                toolbarId: "toolbar_dir_submit",
                icon: "layui-icon layui-icon-release",
                title: "提交审核", handler: function (node, elem) {
                    console.info(node, elem);
                    let param = {
                        entityId: node.id,
                        entityType: ReviewEntityType.DIRECTORY,
                        status: ReviewStatus.SUBMITTED
                    };

                    $.post(`${ctx}/business/review/operate`, param, function (res) {
                        if (res.flag) {
                            layer.msg('提交成功');
                            let tempNode = Object.assign({}, node)
                            tempNode.basicData.reviewStatus = ReviewStatus.PASS;
                            tempNode.title = tempNode.context;
                            tempNode.title = formatterTitle(tempNode);
                            tempNode.iconClass = 'dtree-icon-fenzhijigou';
                            dirTree.partialRefreshEdit(elem, tempNode)
                            metaListTable.reloadTable();
                        } else {
                            layer.msg('提交失败,' + res.msg);
                        }
                    });
                }
            },
        ],
        formatter: {
            title: function (data) { // 文字过滤，返回null,"",undefined之后，都不会改变原有的内容返回。
                return formatterTitle(data);
            }
        }
    }
    dirTree = globalTree.init(treeOps);

    function formatterTitle(data) {
        let {title, basicData} = data;
        if (basicData && basicData.directoryType === ReviewEntityType.METADATA) {
            const reviewStatus = data.basicData.reviewStatus;
            title += ReviewStatusIcon[reviewStatus].replace('##id##', basicData.id);
        }
        return title;
    }

    /**
     * 打开目录编辑窗口
     * @param data
     */
    function openDirectoryEditLayer(data) {
        const isEdit = data.id;
        layer.open({
            type: 1,
            title: data.id ? '重命名目录' : '新建目录',
            area: ['300px', '180px'],
            btn: ['保存', '取消'],
            content: `<div class="layui-form" lay-filter="directoryEditForm">
                        <div class="layui-form-item" style="margin-bottom: 0">
                            <div class="layui-inline" style="width: 90%; margin:15px">
                                <input type="hidden" name="id" value="${data.id || ''}">
                                <input type="hidden" name="parentId" value="${data.parentId}" />
                                <input type="hidden" name="directoryType" value="DIRECTORY" />
                                <input class="layui-input" type="text" name="directoryName" autocomplete="off" value="${data.directoryName || ''}" placeholder="请输入目录名称" lay-verify="required">
                                <a lay-submit lay-filter="directoryEditForm" id="directoryEditFormSubmit" style=""></a>
                            </div>
                        </div>
                    </div>`,
            success: function (layerObj, index) {
                //form.render();
            },
            yes: function (index) {
                form.on('submit(directoryEditForm)', function ({elem, field}) {
                    if (field.directoryName == tempNode.param.context) {
                        layer.close(index);
                    } else {
                        Util.post(`/business/directory/save`, field, true).then(res => {
                            if (res.flag) {
                                let $dom = isEdit ? $(`div[data-id='${field.parentId}'][dtree-id='directoryTree']`) : tempNode.dom;
                                dirTree.getChild($dom);
                                layer.close(index);
                            } else {
                                showErrorMsg(res.msg);
                            }
                        });
                    }
                    return false;
                });
                $('#directoryEditFormSubmit').click();
            }
        })
    }

    let player;

    /**
     * 预览文件
     * @param file
     */
    function viewFile(file) {
        const fileSuffix = file.fileSuffix.toLowerCase();
        laytpl($("#filePropertiesTemplate").html()).render(file, function (html) {
            $("#fileProperties").html(html);
        })
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
    /*form.on('select(reviewStatusSelect)', function ({elem, othis, value}) {
        reviewStatusSearch = value;
        const treeOps = {request: {}}
        if (value) {
            treeOps.request = {reviewStatus: value};
        }
        globalTree.reload(treeOps)
    });*/
    $('#directoryTreeSearchBox').on('keyup', function () {
        setTimeout(() => {
            dirTree.fuzzySearch(this.value);
        }, 500)
    })
})