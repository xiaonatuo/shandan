/**
 * <p>
 *  edit.js
 * </p>
 *
 * @author Administrator
 * @since 2021/5/24
 */
let editPage;
let layer;
layui.use(['form', 'layer', 'editPage', 'laytpl', 'laydate', 'element', 'table'], function () {
    let form = layui.form,
        laytpl = layui.laytpl,
        laydate = layui.laydate,
        table = layui.table;
    editPage = layui.editPage;
    layer = layui.layer;
    // 获取请求参数
    const requestParam = layui.url().search;

    // 元数据表map，由子页面回传
    let metadataTableMap = new Map();

    // 初始化编辑表单
    editPage.init({
        formId: 'metadataForm',
        formInitUrl: requestParam.id ? `${ctx}/business/metadata/get/${requestParam.id}` : '',
        formSubmitInvoke: async function () { // 自定义表单提交时的方法
            let metadataBasic = form.val('metadataForm');
            metadataBasic.metadataDetailsList = [];
            for (let metadata of metadataTableMap.values()) {
                metadataBasic.metadataDetailsList.push(metadata);
            }

            let response;
            await $.ajax({
                url: `${ctx}/business/metadata/save/full`,
                type: 'post',
                data: metadataBasic,
                async: false,
                success: res => response = res,
                error: () => response = {flag: false, msg: '保存失败'}
            })

            return response
        },
        formInitDone: function (data) {
            // 表单初始化完成后，查询数据源列表，并渲染选择数据源下拉框
            $.post(`${ctx}/business/datasource/list`, {}, function (res) {
                $('#dataSourcesSelect').html(`<option value="" selected>请选择数据源</option>`)
                if (res.flag) {
                    for (let source of res.data) {
                        const selected = data && data.dataSourceId == source.id ? 'selected' : '';
                        $('#dataSourcesSelect').append(`<option value="${source.id}" ${selected}>${source.name}</option>`)
                    }
                } else {
                    layer.msg('数据源查询失败');
                }
                form.render('select');

                form.on('select(dataSourcesSelect)',function(obj){
                    form.val('metadataForm', {'dataFrom': obj.othis.find('.layui-this:first').text()});
                })
            })

            // 根据元数据详情表渲染元数据标签组件
            if (data && data.metadataDetailsList && data.metadataDetailsList.length > 0) {
                data.metadataDetailsList.forEach(meta => {
                    if (meta.columnList) { // 需要选中的列
                        meta.columnList.forEach(col => col.selected = meta.foreignColumn == col.columnName ? 'selected' : '')
                    }
                    // 将数据添加到元数据map中去，用来渲染组件
                    metadataTableMap.set(meta.tableName, meta)
                });
                renderMetadataTablesTab(data);
            }
        }
    });

    /**
     * 渲染元数据表标签组件
     */
    const renderMetadataTablesTab = function (initData) {
        if (metadataTableMap.size == 0) return;
        laytpl($("#checkedTableTemplate").html()).render(metadataTableMap, function (html) {
            $("#metadataTablesTab").html(html);
        })
        form.render();

        // 删除元数据表标签，只能再渲染完成后再绑定事件监听，否则无效
        $('#metadataTablesTab .table-tab i').click(function (elem) {
            const tableName = $(this).data('id');
            metadataTableMap.delete(tableName);
            renderMetadataTablesTab();
        });

        // 添加元数据表的下拉框的事件监听
        metadataSelectorEventListener();
        // 设置主表单选按钮的监听
        renderMasterTableRadio();
        // 监听字段配置按钮
        filedConfigBtnListener();
    }


    /**
     * 打开选择数据表的弹窗
     */
    $("#btn_selectTable").click(function () {
        const datasourceId = $('#dataSourcesSelect').val();
        let layerWin;
        if (datasourceId) {
            layer.open({
                id: 'selectTable',
                type: 2,
                area: ['550px', '680px'],
                content: `${ctx}/business/datasource/selectTableLayer?datasourceId=${datasourceId}`,
                btn: ['确定', '取消'],
                success: function (layerObj) {
                    layerWin = window[layerObj.find('iframe')[0]['name']]
                },
                yes: function (index) {
                    layerWin.ok().then((data) => {
                        metadataTableMap = data;
                        /*metadataTableMap.clear()
                        data.forEach((value, key)=>metadataTableMap.set(key, value))*/
                        renderMetadataTablesTab();
                    })
                    layer.close(index);
                }
            });
        } else {
            layer.msg('请先选择数据源');
        }
    });

    /**
     * 元数据下拉框事件监听
     */
    function metadataSelectorEventListener(){
        for (let [index, item] of metadataTableMap) {
            form.on(`select(selectForeignColumn_${index})`, function (data) {
                let metadata = metadataTableMap.get(index);
                if (metadata) {
                    metadata.foreignColumn = data.value;
                }
            });
            form.on(`select(selectForeignTable_${index})`, function (data) {
                let metadata = metadataTableMap.get(index);
                if (metadata) {
                    metadata.foreignTable = data.value;
                }
            });
        }
    }

    /**
     * 初始化主表单选按钮
     * @param initData
     */
    function renderMasterTableRadio(initData){
        form.on('radio', obj => metadataTableMap.forEach((value, key) => {
            value.master = key == obj.value;
            form.val('metadataForm', {metadataName: obj.value})
        }));
        // 如果有初始化数据，则需要设置回显单选按钮的状态
        if (initData && initData.metadataDetailsList && initData.metadataDetailsList.length > 0) {
            initData.metadataDetailsList.forEach(meta => {
                if (meta.master) {
                    form.val('metadataForm', {master: meta.tableName})
                }
            })
        } else {
            const firstValue = metadataTableMap.values().next().value;
            console.info(firstValue)
            firstValue.master = true;
            const metadataName = firstValue.tableName;
            const metadataComment = firstValue.tableComment;
            form.val('metadataForm', {master: metadataName, metadataName, metadataComment})
        }
    }

    /**
     * 监听字段配置按钮
     */
    function filedConfigBtnListener(){
        // 设置字段默认全部显示
        metadataTableMap.forEach((tableInfo, key)=>{
            if(!tableInfo.tableColumns){
                const cols = tableInfo.columnList.map(({columnName, comment}) => {
                    return {columnName, comment}
                });
                tableInfo.tableColumns = JSON.stringify(cols);
                metadataTableMap.set(key, tableInfo);
            }
        })


        // 绑定按钮事件
        $('a.btn-field-config').on('click', function({target}){
            const tableName = $(target).data('table');
            const tableInfo = metadataTableMap.get(tableName);
            openFieldConfigLayer(tableInfo);
        })
    }

    /**
     * 打开字段配置窗口
     * @param tableInfo
     */
    function openFieldConfigLayer(tableInfo){
        // 设置选中数据
        if(tableInfo.tableColumns){
            const checkedCols = JSON.parse(tableInfo.tableColumns);
            if (Array.isArray(checkedCols)) {
                for(let item of checkedCols){
                    for(let col of tableInfo.columnList){
                        if(item.columnName === col.columnName){
                            col.LAY_CHECKED = true;
                        }
                    }
                }
            }
        }

        const data = $.extend([],tableInfo.columnList);
        layer.open({
            title: '配置字段',
            type: 1,
            area:['800px', '600px'],
            btn:['确定','取消'],
            content: `<table id="field-config-table" lay-filter="field-config-table"></table>`,
            success:function(){
                table.render({
                    elem:'#field-config-table',
                    data: data,
                    page:false,
                    limit: 999,
                    cols:[[
                        {type:'checkbox', width: 50},
                        {field:'columnName', title:'字段名称', width: 300},
                        {field:'dataType', title:'数据类型', width: 120},
                        {field:'comment', title:'注释', width: 300},
                    ]]
                });
            },
            yes: function (index) {
                const {data, isAll} = table.checkStatus('field-config-table');
                let tableColumns = [];
                for(let {columnName, comment} of data){
                    tableColumns.push({columnName, comment})
                }

                tableInfo.tableColumns = JSON.stringify(tableColumns);
                metadataTableMap.set(tableInfo.tableName, tableInfo);
                layer.close(index);
            }
        });
    }

    //日期选择器
    laydate.render({
        elem: '#collectionTime',
        format: "yyyy-MM-dd HH:mm:ss"
    });
});

/**
 * 对父页面暴露save方法
 * @returns {*}
 */
function save() {
    return editPage.submit();
}

/**
 * 测试连接
 */
function connectTest() {
    let data = editPage.getData();
    $.post(`${ctx}/business/datasource/connection/test`, data, function (res) {
        if (res.flag) {
            layer.msg('连接成功');
        } else {
            layer.msg(res.msg);
        }
    });
}