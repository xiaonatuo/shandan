/**
 *  统计报表组件
 *
 * @author GuoXin
 * @since 2021/7/12
 */
function ReportComponent(layer, form) {
    this.layer = layer || {}; // layui layer组件
    this.form = form || {}; // layui form组件
    this.formData = {}; // 检索页面的form表单数据
    this.fields = common_fields; // 当前字段集合，默认只有公共字段
}

ReportComponent.Report = class {
    type = ''; // 图表类型('pie', 'bar', 'line')
    /**
     * X轴统计依据
     * @type {{}}
     */
    xAxis = {
        field: '', // 字段
        fieldType: '', // 字段类型
        range: {from: undefined, to: undefined}, // 范围区间
    }
    /**
     * Y轴聚合指标
     * @type {{}}
     */
    yAxis = {
        aggregation: '', // 聚合（count, sum）
        field: '', // 聚合字段
    }

    constructor() {
    }
}

/**
 * 打开统计报表主窗口
 */
ReportComponent.prototype.openMainLayer = function () {
    const _this = this;
    layer.open({
        type: 1,
        title: '统计报表',
        area: ['800px', '600px'],
        content: template_main,
        success: function (layero, index) {
            // 打开即页面全屏
            layer.full(index);

            // 新增报表，先选择图表类型，然后再选择需要的字段
            $('#btn-add').on('click', function () {
                _this.openEchartsConfigLayer();
            });
        }
    })
}

/**
 * 打开图表配置窗口
 */
ReportComponent.prototype.openEchartsConfigLayer = function () {
    const _this = this;
    layer.open({
        title: '统计报表配置',
        type: 1,
        resize: false,
        closeBtn: false,
        btn: ['确定', '取消'],
        content: template_config,
        success: function (layerObj, index) {
            _this.renderSelect(common_fields);
            $('#echartsConfigForm').parent().css('overflow', 'visible')
        },
        yes: function (index) {
            const formVal = _this.form.val('echartsConfigForm');
            console.info(formVal);
            // 数据校验
            if(!formVal.title || formVal.title.trim() == ''){
                _this.layer.msg('请输入标题');
                return;
            }
            if(!formVal.fieldX){
                _this.layer.msg('请设置统计维度字段');
                return;
            }else{
                if(dm_date_types.includes(formVal.fieldXType) && !formVal.dateInterval){
                    _this.layer.msg('请设置时间间隔');
                    return;
                }
                if(dm_number_types.includes(formVal.fieldXType) && !formVal.numberInterval){
                 _this.layer.msg('请设置范围');
                 return;
                }
            }
            if(!formVal.aggregationType){
                _this.layer.msg('请设置聚合方式');
                return;
            }else{
                if((formVal.aggregationType == 'sum' || formVal.aggregationType == 'avg') && !formVal.fieldY){
                    _this.layer.msg('请设置聚合字段');
                    return;
                }
            }
            _this.requestData(formVal);
            //layer.close(index);
        }
    });
}

/**
 * 请求数据
 * @param formVal
 */
ReportComponent.prototype.requestData = function (formVal) {
    const _this = this;
    formVal.conditions = _this.conditions
    $.post(`${ctx}/report/data`, formVal, function (res) {
        console.info(res);
        if (res.flag) {
            //report.data = res.data;
            //_this.renderEcharts(report);
        }
    });
}

/**
 * 初始化字段下拉框数据
 * @param metadataId 元数据ID
 */
ReportComponent.prototype.initFieldSelect = function (metadataId) {
    const _this = this;
    $.get(`${ctx}/report/metadata/columns/${metadataId}`, {}, function (res) {
        let fields = $.extend([], common_fields);
        if (res.flag && res.data) {
            let cols = JSON.parse(res.data);
            for (let item of cols) {
                const field = item.columnName, type = item.type, comment = item.comment;
                if (!field || field.toLowerCase().endsWith('id')) {
                    continue
                }
                let flag = true;
                // 将现有字段集合中同名但大小写不同的字段替换掉
                for (let i = 0; i < fields.length; i++) {
                    if (fields[i].field.toUpperCase() == field.toUpperCase()) {
                        fields[i].field = field;
                        fields[i].type = type;
                        fields[i].comment = comment;
                        flag = false;
                        break;
                    }
                }
                flag && fields.push({field, type, comment});
            }
            _this.fields = fields;
        }
        _this.renderSelect();
    })
}
/**
 * 渲染字段下拉框
 */
ReportComponent.prototype.renderSelect = function () {
    const _this = this;
    let options = `<option value="">选择字段</option>`;
    for (const field of _this.fields) {
        const text = field.comment || field.field;
        options += `<option value="${field.field}">${text}</option>`
    }
    $('#selectFieldX').html(options)
    _this.form.render('select');
    // 维度字段下拉框事件,根据选择字段类型判断是否显示范围输入选项
    _this.form.on('select(selectFieldX)', function ({elem, value, othis}) {
        const fieldType = _this.findFieldType(value);
        _this.form.val('echartsConfigForm', {fieldXType: fieldType});
        if (fieldType) {
            if (dm_date_types.includes(fieldType)) {
                $('#range-date-item').removeClass('layui-hide')
            } else if (dm_number_types.includes(fieldType)) {
                $('#range-number-item').removeClass('layui-hide')
            } else {
                $('#range-date-item').addClass('layui-hide')
                $('#range-number-item').addClass('layui-hide')
                _this.form.val('echartsConfigForm', {dateInterval: "", numberInterval: ""});
            }
        }
    })
    // 聚合指标下拉框事件监听
    _this.form.on('select(aggregationType)', function ({elem, value, othis}) {
        if (value == 'sum' || value == 'avg') {
            // 渲染数值类型字段
            const numberFields = _this.fields.filter(field => dm_number_types.includes(field.type));
            let options = '<option value="">选择字段</option>'
            if (numberFields.length == 0) {
                options = '<option value="">没有可用字段</option>'
            }
            for (const field of numberFields) {
                const text = field.comment || field.field;
                options += `<option value="${field.field}">${text}</option>`
            }
            $('#selectFieldY').html(options)
            $('#field-select-item-y').removeClass('layui-hide');
            _this.form.render('select')
        } else {
            $('#field-select-item-y').addClass('layui-hide');
            _this.form.val('echartsConfigForm', {fieldY: ""});
        }
    })
}

/**
 * 查找字段类型
 * @param field
 */
ReportComponent.prototype.findFieldType = function (field) {
    const fields = this.fields;
    for (let f of fields) {
        if (field == f.field) {
            return f.type;
        }
    }
    return '';
}

/**
 * 设置属性
 * @param value -
 */
ReportComponent.prototype.setFormData = function (value) {
    this.formData = value;
    // 需要判断目录树是否选中了元数据，如果是，则需要查询字段，然后渲染字段下拉框
    if (value.metadataId) {
        this.initFieldSelect(value.metadataId);
    } else {
        this.renderSelect(common_fields);
    }
}

/**
 * 设置条件参数
 * @param value -
 */
ReportComponent.prototype.setConditions = function (value) {
    this.conditions = value;
}