layui.extend({
    permisConfig: `{/}${ctx}/js/sys/permissions/permisConfig` // {/}的意思即代表采用自有路径，即不跟随 base 路径
})
layui.use(['form', 'menuTree', 'layer', 'gtable', 'permisConfig'], function () {
    let layer = layui.layer,
        gtable = layui.gtable,
        form = layui.form;
    const permisConfLayer = layui.permisConfig.init()

    initDictTypeData();
    initDictTable();

    // 用于临时存储查询条件
    let dictTypes,searchText = '';

    /**
     * 打开编辑窗口
     * @param id 当前部门
     */
    let editLayerWin;
    function openEditLayer(id = '') {
        layer.open({
            id: 'dictEdit',
            type: 2,
            title: '权限编辑',
            area: ['550px', '530px'],
            content: ctx + `/sys/dict/edit?id=${id}`,
            btn: ['确定', '取消'],
            success: function (layero, index) {
                editLayerWin = window[layero.find('iframe')[0]['name']]
            },
            yes: function (index) {
                editLayerWin && editLayerWin.save().then(ok => {
                    if (ok) {
                        gtable.reload();
                        layer.close(index);
                    }
                });
            },
            end: function () {
            }
        });
    }

    /**
     * 删除角色
     * @param id
     * @param callback
     */
    function deleteDict(id, callback) {
        $.delete(`${ctx}/sys/dict/delete/${id}`, {}, function (res) {
            res.msg = res.flag ? '删除成功' : '删除失败';
            let icon = res.flag ? 1 : 5;
            layer.msg(res.msg, {icon, time: 2000}, function () {
                if (res.flag) {
                    gtable.reload();
                }
            });
            callback && callback();
        });
    }

    /**
     * 数据表格点击事件
     */
    function tableEventCallback(obj) {
        let rowData = obj.data;
        switch (obj.event) {
            case 'add':
                openEditLayer();
                break;
            case 'query':
                break;
            case 'edit':
                openEditLayer(rowData.id);
                break;
            case 'delete':
                layer.confirm('确定要删除该权限吗？', function (index) {
                    deleteDict(rowData.id, () => layer.close(index));
                });
                break;
            case 'config':
                permisConfLayer.showLayer(rowData)
        }
    }
    /**
     * 数据表格加载完成回调
     * @param res
     */
    function tableDone(res) {
        //console.info('数据表格加载完成', res);
        $('#searchKeyInput').val(searchText);
        renderDictTypeSelect();
    }

    /**
     * 初始化数据字典列表
     */
    function initDictTable() {
        let options = {
            id: 'dictTable',
            url: ctx + '/sys/dict/page',
            toolbar: '#tableToolBar',
            title: '权限列表',
            cols: [[
                {field: 'dictCode', title: '字典编码'},
                {field: 'typeName', title: '字典类型'},
                {field: 'dictName', title: '字典名称'},
                {field: 'dictValue', title: '字典值'},
                {
                    field: 'dictState',
                    title: '状态',
                    width: 80,
                    align: 'center',
                    templet: data => data.dictState ? '启用' : '停用'
                },
                {field: 'dictDesc', title: '字典描述', width: '20%', align: 'left'},
                {fixed: 'right', title: '操作', toolbar: '#rowToolBar', align: 'center', width: 120}
            ]],
            onToolBarTable: tableEventCallback,
            onToolBarRow: tableEventCallback,
            done: tableDone
        };
        gtable.init(options);
    }

    /**
     * 初始化数据字典类型
     */
    function initDictTypeData() {
        Util.post('/sys/dict/type/list', {}).then(res => {
            if(res.flag){
                dictTypes = res.data;
            }else {
                showErrorMsg('数据字典类型初始化失败')
            }
        })
    }

    /**
     * 渲染数据字典类型下拉选择框
     */
    function renderDictTypeSelect(){
        let data = dictTypes;
        let htm = '<option value="">直接选择或搜索选择</option>';
        for(let type of data){
            htm += `<option value="${type.id}">${type.name}</option>`
        }
        $('#dictTypeSelect').html(htm);
        // 开始渲染
        form.render('select');
        // 选择事件监听
        form.on('select(dictTypeSelect)', function({value}){
            let queryOps = {page: {current: 1}, where:{typeId: value}}
            gtable.reload(queryOps);
        });
    }
});
