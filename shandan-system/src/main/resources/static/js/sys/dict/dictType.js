layui.define(['form', 'gtable'], function (exports) {
    let layer = layui.layer, form = layui.form, gtable = layui.gtable, table, _window;
    // 管理窗口html模板
    const template_management = `
        <div class="layui-row">
            <div class="layui-col-md12">
                <table class="layui-hide" id="dictTypeTable" lay-filter="dictTypeTable"></table>
            </div>
        </div>`;
    const template_edit = `
        <div class="layui-row">
            <div class="layui-col-md12">
                <form class="layui-form" id="dictTypeEditForm" lay-filter="dictTypeEditForm" style="padding: 15px 15px 0 0">
                    <div class="layui-form-item">
                        <label class="layui-form-label" style="width: 80px; text-align: right; padding-right: 5px;">类型标识：</label>
                        <div class="layui-input-block" style="margin-left: 100px">
                            <input class="layui-input" type="text" name="id" id="idInput" lay-verify="required" autocomplete="off">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label" style="width: 80px; text-align: right; padding-right: 5px;">类型名称：</label>
                        <div class="layui-input-block" style="margin-left: 100px">
                            <input class="layui-input" type="text" name="name" lay-verify="required" autocomplete="off">
                        </div>
                    </div>
                    <button type="submit" lay-submit id="dictFormSubmitBtn" style="width: 1px; height: 1px;position: absolute;left: -100px;top:-100px">submit</button>
                </form>
            </div>
        </div>`;

    /**
     * 打开管理弹窗
     */
    function openManagementLayer(callback) {
        openLayer({
            id: 'dictTypeManagement',
            title: '管理字典类型',
            area: ['550px', '530px'],
            content: template_management,
            btn: ['确定'],
            success: function (layero, index) {
                // 重新设定layer窗口的样式
                let content = layero.find('.layui-layer-content');
                content.css({padding: '0 10px', height: content.height() + 10})
                content.parent().find('.layui-layer-btn-').css('padding-top','0')
                initDictTypeTable();
            },
            yes: function (index) {
                layer.close(index);
                callback && callback();
            },
            end: function () {
            }
        });
    }

    /**
     * 打开字典类型编辑弹窗
     * @param data 字典类型数据
     */
    let editLayerIndex;
    function openDictTypeEditLayer(data){
        openLayer({
            id: 'dictTypeEdit',
            title: '管理字典类型',
            area: ['400px', '230px'],
            content: template_edit,
            btn: ['保存', '取消'],
            success: function(layerObj, index){
                editLayerIndex = index;
                if(data){
                    form.val('dictTypeEditForm', data);
                    $('#dictTypeEditForm input[name="id"]').attr('readonly',true)
                }
                initDictTypeForm();
            },
            yes: function () {
               $('#dictFormSubmitBtn').click();
            }
        })
    }

    /**
     * 关闭编辑窗口
     */
    function closeEditLayer(){
        layer.close(editLayerIndex);
    }

    /**
     * 初始化字典类型Form表单
     */
    function initDictTypeForm(){
        form.on('submit(dictTypeEditForm)', function () {
            let data = form.val('dictTypeEditForm')
            saveDictTypeData(data)
            return false;
        });
    }

    /**
     * 保存数据
     * @param data
     */
    function saveDictTypeData(data){
        Util.post('/sys/dict/type/save',data).then(res =>{
            if (res.flag){
                showOkMsg('保存成功');
                closeEditLayer();
                table && table.reload();
            }else{
                showErrorMsg('保存失败');
            }
        }).catch(()=>showErrorMsg())
    }

    /**
     * 删除字典类型数据
     * @param ids
     */
    function deleteDictTypeData(ids){
        layer.confirm('是否确定删除这些数据？', function () {
            Util.send('/sys/dict/type/delete', {ids}, 'delete').then(res =>{
                if (res.flag){
                    showOkMsg('删除成功');
                    table && table.reload();
                }else{
                    showErrorMsg('删除失败');
                }
            }).catch(()=>showErrorMsg())
        });
    }

    /**
     * 初始化字典类型数据表格
     */
    function initDictTypeTable() {
        table = gtable.init({
            id: 'dictTypeTable',
            url: `${ctx}/sys/dict/type/list`,
            method: 'post',
            toolbar: 'default',
            height: 'full-507',
            page: false,
            cols: [[
                {type: 'checkbox', fixed: 'left'},
                {field: 'id', title: '类型标识', width: 120},
                {field: 'name', title: '类型名称'},
            ]],
            done: (obj) => {
                //console.info(obj)
            },
            onToolBarTable: function (obj) {
                let checkStatus = table.getCheckStatus()
                    ,data = checkStatus.data; //获取选中的数据
                switch (obj.event) {
                    case 'add':
                        openDictTypeEditLayer();
                        break;
                    case 'update':
                        if (data.length === 0) {
                            layer.msg('请至少选择一行');
                        } else if (data.length > 1) {
                            layer.msg('只能同时编辑一个');
                        } else {
                            openDictTypeEditLayer(data[0]);
                        }
                        break;
                    case 'delete':
                        if (data.length === 0) {
                            layer.msg('请至少选择一行');
                        } else {
                            let ids = data.map(item => item.id).join(',');
                            deleteDictTypeData(ids)
                        }
                        break;
                }
            }
        });
    }

    function openLayer(options){
        let defaultOps = {type: 1}
        layer.open($.extend(defaultOps, options))
    }

    const component = {
        /**
         * 打开管理弹窗
         */
        openManagementLayer: function (callback) {
            openManagementLayer(callback)
        },
        setWindowObject: function (win) {
            _window = win;
            layer = _window.layer;
            form = _window.layui.form;
        }
    };
    exports('dictType', component);
});