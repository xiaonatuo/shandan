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
    let dirTree, metaListTable, fileUploadLayerWin, tempNode;

    let addMetadataLayerWin;
    const openAddMetadataLayer = function (directory, callback) {
        if (directory.reviewStatus == ReviewStatus.PASS) {
            addDataConfirm(open);
        } else {
            open();
        }

        function open() {
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
                                    refreshDirectoryNode(tempNode, res.data);
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
    }

    /**
     * 加载数据资源列表
     */
    const loadMetadataList = function (directory) {
        const {basicData} = directory;
        if (!basicData) return;
        let hideFunBtn = basicData.reviewStatus == ReviewStatus.SUBMITTED || basicData.reviewStatus == ReviewStatus.PASS
        metaListTable = listPage.init({
            table: {
                hideFunBtn,
                id: 'dirMetadataTable',
                toolbar: '#tableToolBar',
                searchFieldNames: 'metadataName',
                url: `${ctx}/business/metadata/list/directory?directoryId=${basicData.id}`,
                height: 'full-110',
                method: 'get',
                cols: [[
                    {field: 'id', title: 'ID', hide: true},
                    {field: 'metadataName', title: '数据名称', width: 300},
                    {field: 'metadataComment', title: '中文注释'},
                    {field: 'themeTask', title: '主题任务'},
                    {field: 'dataFrom', title: '数据来源'},
                    {field: 'createTime', title: '注册时间', width: 160, align: 'center'},
                    {
                        fixed: 'right',
                        title: '操作',
                        toolbar: `#rowToolBar`,
                        width: 100,
                        align: 'center'
                    }
                ]],
            },
        });
        metaListTable.addTableRowEvent('addLink', function (obj) {
            openAddMetadataLayer(directory.basicData, () => {
                metaListTable.reloadTable();
            })
        })
        metaListTable.addTableRowEvent('removeLink', function (obj) {
            if (basicData.reviewStatus == ReviewStatus.PASS) {
                addDataConfirm(removeData)
            } else {
                layer.confirm('是否要解除该条数据的关联？', {}, function (index) {
                    layer.close(index);
                    removeData();
                })
            }

            function removeData() {
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
                        refreshDirectoryNode(tempNode, res.data);
                    } else {
                        layer.msg('解除关联失败');
                    }
                }
            }
        })
        metaListTable.addTableRowEvent('addFile', function (obj) {
            upload(basicData,`${ctx}/sys/file/layer?directoryId=${basicData.id}`, function(res){
                if (res.success) {
                    metaListTable.reloadTable();
                    if (res.data) {
                        Util.get(`/business/directory/get/${res.data.entityId}`).then(res => refreshDirectoryNode(tempNode, res.data))
                    }
                }
            });
        });

        metaListTable.addTableRowEvent('addDirectory', function (obj) {
            upload(basicData,`${ctx}/sys/file/layer/dir?directoryId=${basicData.id}`, function(){

            });
        })

        // 查看按钮监听
        metaListTable.addTableRowEvent('details', function (obj) {
            if (obj.dataSourceId.startsWith('file_')) {
                openMaxLayerWithURL(`${ctx}/sys/file/view?fileId=${obj.id}`)
            } else {
                openMaxLayerWithURL(`${ctx}/business/metadata/details/${obj.id}`)
            }
        })
    }

    function upload(basicData, url, callback) {
        if (basicData.reviewStatus == ReviewStatus.PASS) {
            addDataConfirm(open);
        } else {
            open();
        }

        function open() {
            layer.open({
                id: 'fileUploadLayer',
                type: 2,
                area: ['850px', '600px'],
                btn: ['开始上传', '取消'],
                content: url,
                success: function (layero, index) {
                    fileUploadLayerWin = window[layero.find('iframe')[0]['name']];
                },
                yes: function (index) {
                    fileUploadLayerWin.save().then(callback);
                }
            });
        }
    }

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
            setLocation(basicData)
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
                            setDisabledButtons(['toolbar_dir_submit']);
                        case ReviewStatus.PASS:
                            setDisabledButtons(['toolbar_dir_submit']);
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
                handler: function (node, elem) {
                    const {id, basicData} = node;
                    openDirectoryEditLayer({parentId: id}, function (data) {
                        if (id == '-') {
                            dirTree.partialRefreshAdd(elem);
                        } else {
                            setTimeout(function () {
                                let $newDom = $(`#directoryTree div.dtree-nav-div.dtree-theme-item[data-id="${basicData.id}"]`)
                                dirTree.partialRefreshAdd($newDom);
                            }, 50)
                        }
                    });
                }
            },
            {
                toolbarId: "toolbar_dir_rename",
                icon: "dtreefont dtree-icon-bianji",
                title: "重命名目录",
                handler: function (node, elem) {
                    const {basicData} = node;
                    openDirectoryEditLayer(basicData, function (data) {
                        dirTree.partialRefreshEdit(elem, data);
                        dirTree.getChild(elem);
                        setLocation(data.basicData)
                    });
                }
            },
            {
                toolbarId: "toolbar_dir_delete",
                icon: "dtreefont dtree-icon-delete1",
                title: "删除目录",
                handler: function (node, elem) {
                    const {id, parentId} = node;
                    layer.confirm('是否确定删除该目录？', function (index) {
                        Util.send(`/business/directory/delete/${id}`, {}, 'delete').then(res => {
                            if (res.flag) {
                                showOkMsg('删除成功')
                                dirTree.partialRefreshDel(elem)
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
                        status: ReviewStatus.SUBMITTED,
                        metadataName: node.context || node.title || node.basicData.directoryName
                    };
                    $.post(`${ctx}/business/review/operate`, param, function (res) {
                        if (res.flag) {
                            layer.msg('提交成功');
                            let temp = Object.assign({}, node)
                            temp.basicData.reviewStatus = ReviewStatus.SUBMITTED;
                            temp.title = temp.context;
                            temp.title = formatterTitle(temp);
                            dirTree.partialRefreshEdit(elem, temp)
                            dirTree.getChild(elem);
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
            title = basicData.directoryName + ReviewStatusIcon[reviewStatus].replace('##id##', basicData.id);
        }
        return title;
    }

    /**
     * 打开目录编辑窗口
     * @param data
     */
    function openDirectoryEditLayer(data, callback) {
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
                    Util.post(`/business/directory/save`, field, true).then(res => {
                        if (res.flag) {
                            layer.close(index);
                            let data = Object.assign({}, generateDirectoryNode(res.data), field);
                            callback && callback(data);
                        } else {
                            showErrorMsg(res.msg);
                        }
                    });
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

    /**
     * 设置当前位置
     * @param dirNode
     */
    function setLocation(dirNode) {
        let path = '/ 资源目录';
        if (dirNode && dirNode.directoryPath) {
            path += dirNode.directoryPath.replaceAll('/', ' / ');
        }
        $('#currentPosition').text(path);
    }

    function generateDirectoryNode(data) {
        if (data) {
            let temp = Object.assign({}, data);
            temp.basicData = data;
            temp.title = data.title || data.directoryName

            return {
                id: temp.id,
                basicData: temp,
                title: formatterTitle(temp)
            }
        }
        return {};
    }

    /**
     * 刷新目录节点
     */
    function refreshDirectoryNode({dom, param, parentParam, childrenParam}, data) {
        dirTree.partialRefreshEdit(dom, generateDirectoryNode(data));
        dirTree.getChild(dom);
        dom.click();
    }

    function addDataConfirm(callback) {
        layer.confirm('当前资源目录已经审核通过,如继续关联,目录状态将重置为未提交，需要重新提交审核流程。<br>是否继续？', {
            area: ['400px', '220px'],
            icon: 0
        }, function (index) {
            layer.close(index);
            callback && callback()
        })
    }
})