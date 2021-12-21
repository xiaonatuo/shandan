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
        form = layui.form;
    // 目录树
    let dirTree;
    let metaListTable;
    let fileUploadLayerWin;


    let addMetadataLayerWin;
    const openAddMetadataLayer = function (directory, callback) {
        layer.open({
            id: 'addMetadataLayer',
            type: 2,
            area: ['800px', '600px'],
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
     * 加载数据资源列表
     */
    const loadMetadataList = function (directory) {
        const {basicData} = directory;
        if (!basicData) return;

        metaListTable = listPage.init({
            table: {
                hideFunBtn: basicData.reviewStatus == ReviewStatus.SUBMITTED || basicData.reviewStatus == ReviewStatus.PASS,
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
        metaListTable.addTableRowEvent('addLink', function (obj) {
            openAddMetadataLayer(directory.basicData, () => {
                metaListTable.reloadTable();
            })
        })
        metaListTable.addTableRowEvent('removeLink', function (obj) {
            layer.confirm('是否要解除该条数据的关联？', {}, function (index) {

                if (obj.dataSourceId.startsWith('file_')) {
                    $.post(`${ctx}/business/directory/remove/file`, {fileId: obj.id}, callback)
                } else {
                    $.post(`${ctx}/business/directory/remove/metadata`, {
                        directoryId: basicData.id,
                        metadataId: obj.id
                    }, callback)
                }

                function callback(res) {
                    if (res.flag) {
                        metaListTable.reloadTable();
                    } else {
                        layer.msg('解除关联失败');
                    }
                    layer.close(index);
                }
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
                },
                yes: function (index) {
                    fileUploadLayerWin.save().then(ok => {
                        ok && metaListTable.reloadTable();
                    });
                }
            });
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
                const {basicData} = param;
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
                        //dirTree.partialRefreshAdd(elem);
                        metaListTable.reloadTable();
                    })
                }
            },
            {
                toolbarId: "toolbar_dir_submit",
                icon: "layui-icon layui-icon-release",
                title: "提交审核", handler: function (node, elem) {
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
                            dirTree.partialRefreshEdit(elem, tempNode)
                            //metaListTable.reloadTable();
                            loadMetadataList(node)
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
        if (basicData) {
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

    $('#directoryTreeSearchBox').on('keyup', function () {
        setTimeout(() => {
            dirTree.fuzzySearch(this.value);
        }, 500)
    })
})