/**
 *  统计报表组件
 *
 * @author GuoXin
 * @since 2021/7/12
 */
function ReportComponent(layer, form) {
    this.reset(layer, form);
}

ReportComponent.prototype.reset = function (layer, form) {
    if (layer) {
        this.layer = layer || {}; // layui layer组件
    }
    if (form) {
        this.form = form || {}; // layui form组件
    }
    this.formData = {}; // 检索页面的form表单数据
    this.fields = common_fields; // 当前字段集合，默认只有公共字段
    this.size = 0;
    this.echarts = [];
};

/**
 * 打开统计报表主窗口
 */
ReportComponent.prototype.openMainLayer = function () {
    const _this = this;
    layer.open({
        type: 1,
        title: '统计报表',
        area: ['900px', '100%'],
        content: template_main,
        success: function (layero, index) {
            // 打开即页面全屏
            //layer.full(index);
            _this.reset();
            // 新增报表，先选择图表类型，然后再选择需要的字段
            $('#btn-add').on('click', function () {
                _this.openEchartsConfigLayer();
            });

            $('#btn-download').on('click', function () {
                _this.download();
            });
        },
        done: function () {
            _this.reset();
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
            if (_this.validate(formVal)) {
                _this.requestData(formVal);
                layer.close(index);
            }
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
                const field = item.columnName, type = item.dataType, comment = item.comment;
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

    // 图表类型下拉框事件监听
    _this.form.on('select(reportType)', function ({elem, value, othis}) {
        // 如果选择饼图，则聚合方式只能选则计数
        if (value == 'pie') {
            $('#aggregationType option[value="count"]').siblings().attr('disabled', true)
            _this.form.val('echartsConfigForm', {aggregationType: 'count'});
        } else {
            $('#aggregationType option[value="count"]').siblings().attr('disabled', false)
        }
        _this.form.render('select')
        console.info('echartsConfigForm', _this.form.val('echartsConfigForm'));
    })
    console.info('echartsConfigForm', _this.form.val('echartsConfigForm'));
}

/**
 * 查找字段类型
 * @param field
 */
ReportComponent.prototype.findFieldType = function (field) {
    const fields = this.fields;
    for (let f of fields) {
        if (field == f.field) {
            return f.dataType;
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

/**
 * 请求数据
 * @param formVal
 */
ReportComponent.prototype.requestData = function (formVal) {
    const _this = this;
    formVal.conditions = _this.conditions
    let load = layer.load();
    $.post(`${ctx}/report/data`, formVal, function (res) {
        layer.close(load);
        if (res.flag) {
            _this.renderEcharts(res.data);
        }
    });
}

/**
 * 数据校验
 * @param formVal
 */
ReportComponent.prototype.validate = function (formVal) {
    // 数据校验
    if (!formVal.title || formVal.title.trim() == '') {
        this.layer.msg('请输入标题');
        return;
    }
    if (!formVal.fieldX) {
        this.layer.msg('请设置统计维度字段');
        return;
    } else {
        if (dm_date_types.includes(formVal.fieldXType) && !formVal.dateInterval) {
            this.layer.msg('请设置时间间隔');
            return;
        }
        if (dm_number_types.includes(formVal.fieldXType) && !formVal.numberInterval) {
            this.layer.msg('请设置范围');
            return;
        }
    }
    if (!formVal.aggregationType) {
        this.layer.msg('请设置聚合方式');
        return;
    } else {
        if ((formVal.aggregationType == 'sum' || formVal.aggregationType == 'avg') && !formVal.fieldY) {
            this.layer.msg('请设置聚合字段');
            return;
        }
    }
    return true;
}

/**
 * 渲染echarts
 * @param reportData
 */
ReportComponent.prototype.renderEcharts = function (reportData) {
    const _this = this;
    const formData = _this.form.val('echartsConfigForm');
    const elemId = `echarts-item-${_this.echarts.length + 1}`;
    $('#report-items').append(`<div class="echarts-item" id="${elemId}"></div>`)
    let chartDom = document.getElementById(elemId);
    let echartsItem = echarts.init(chartDom);
    let option = {
        width: 500,
        title: {
            text: formData.title || '统计图',
            left: 'center'
        },
        toolbox: {
            show: true,
            feature: {
                dataView: {
                    readOnly: false,
                    title: '数据视图'
                },
                saveAsImage: {title: '下载'}
            }
        },
        tooltip: {
            trigger: 'item'
        },
        legend: {
            orient: 'vertical',
            left: 'left',
        },
        series: [
            {
                type: reportData.reportType,
                radius: '50%',
                data: reportData.data,
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    };
    if (reportData.reportType !== 'pie') {
        option.xAxis = [
            {
                type: 'category',
                data: [],
                axisTick: {
                    interval: 0,
                    alignWithLabel: true
                }
            }
        ];
        option.tooltip = {
            trigger: 'axis',
            axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                type: 'line'        // 默认为直线，可选为：'line' | 'shadow'
            }
        }
        option.yAxis = [{type: 'value'}]
        option.series[0].data = [];
        for (let item of reportData.data) {
            option.xAxis[0].data.push(item.name)
            option.series[0].data.push(item.value)
        }
    }
    option && echartsItem.setOption(option);
    echartsItem['formData'] = formData;
    echartsItem['reportRemark'] = formData.remark;
    echartsItem['requestData'] = reportData.data;

    _this.echarts.push(echartsItem);
}

ReportComponent.prototype.download = function () {
    const _this = this;
    if(_this.echarts.length == 0){
        layer.msg('请先定义图表', {icon:5})
        return false;
    }
    layer.prompt({title: '请输入报表文件名称', formType: 3}, function (text, index) {
        layer.close(index);
        const echartsData = [];
        for (let e of _this.echarts) {
            let config = e.formData;
            echartsData.push({
                title: config.title,
                fieldX: config.fieldX,
                filedY: config.fieldY,
                aggregationType: config.aggregationType,
                remark: config.remark,
                data: e.requestData,
                image: e.getDataURL(),
            })
        }
        const params = {
            title: text,
            conditions: _this.conditions,
            echarts: echartsData,
            fields: _this.fields
        };

        $.ajax({
            type: "POST",
            url: `${ctx}/report/export/word`,
            data: params,
            success: function (response, status, request) {
                window.parent.open(`${ctx}/report/download/word`, '_blank')
            }
        });
    });
};