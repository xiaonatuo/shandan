/**
 *  统计报表组件
 *
 * @author GuoXin
 * @since 2021/7/12
 */
function ReportComponent(columns, conditions, metadataId) {
    this.metadataId = metadataId || '';
    this.columns = columns || [];
    this.conditions = conditions || [];
    this.echarts = [];
    this.size = 0;
    this.formData = {};

    this.form = {};
    layui.use(['form'], () => {
        this.form = layui.form;
    })
}


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
            // 新增报表，先选择图表类型，然后再选择需要的字段
            $('#btn-add').on('click', function () {
                _this.openEchartsConfigLayer();
            });

            $('#btn-download').on('click', function () {
                _this.download();
            });
        },
        done: function () {
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
            _this.renderSelect();
            $('#echartsConfigForm').parent().css('overflow', 'visible')
        },
        yes: function (index) {
            _this.requestData();
            //layer.close(index);
        }
    });
}

/**
 * 渲染字段下拉框
 */
ReportComponent.prototype.renderSelect = function () {
    const _this = this;
    let options = `<option value="">选择字段</option>`;
    for (const col of _this.columns) {
        const text = col.comment || col.columnName;
        options += `<option value="${col.columnName}" data-type="${col.dataType}" data-table="${col.tableName}">${text}</option>`
    }
    $('#selectFieldX').html(options)
    $('#selectFieldY').html(options)
    _this.form.render('select');
    // 维度字段下拉框事件,根据选择字段类型判断是否显示范围输入选项
    _this.form.on('select(selectFieldX)', function ({elem, value, othis}) {
        const fieldType = _this.findFieldType(value, elem);
        _this.form.val('echartsConfigForm', {fieldXType: fieldType});
        if (dm_date_types.includes(fieldType)) {
            $('#range-number-item').addClass('layui-hide')
            $('#range-date-item').removeClass('layui-hide')
        } else if (dm_number_types.includes(fieldType)) {
            $('#range-date-item').addClass('layui-hide')
            $('#range-number-item').removeClass('layui-hide')
        } else {
            $('#range-date-item').addClass('layui-hide')
            $('#range-number-item').addClass('layui-hide')
            _this.form.val('echartsConfigForm', {dateInterval: "", numberInterval: ""});
        }
    })

    _this.form.on('select(selectFieldY)', function ({elem, value, othis}) {
        const fieldType = _this.findFieldType(value, elem);
        if (dm_number_types.includes(fieldType)) {
            $('#aggregationType option[value="sum"]').removeAttr('disabled')
            $('#aggregationType option[value="avg"]').removeAttr('disabled')
        } else {
            $('#aggregationType option[value="sum"]').attr('disabled', 'disabled')
            $('#aggregationType option[value="avg"]').attr('disabled', 'disabled')
            _this.form.val('echartsConfigForm', {dateInterval: "", numberInterval: ""});
        }
        _this.form.render('select');
    });

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
    })
}

/**
 * 查找字段类型
 * @param value
 * @param elem
 */
ReportComponent.prototype.findFieldType = function (value, elem) {
    return $(elem).find(`option[value="${value}"]`).data('type');
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
 */
ReportComponent.prototype.requestData = function () {
    const _this = this;
    Util.post(`/report/data/metadata/conditions`, _this.getReportData()).then(res => {
        if (res.flag) {
            _this.renderEcharts(res.data);
        } else {
            showErrorMsg('数据统计请求失败');
        }
    })
}

ReportComponent.prototype.getReportData = function () {
    const _this = this;
    _this.formData = _this.form.val('echartsConfigForm');
    if (_this.validate(_this.formData)) {
        _this.formData.conditions = _this.conditions;
        _this.formData.metadataId = _this.metadataId;
        _this.formData.fieldXTable = getFiledTable($('#selectFieldX'), _this.formData.fieldX);
        _this.formData.fieldYTable = getFiledTable($('#selectFieldY'), _this.formData.fieldY);
    }

    function getFiledTable($select, value) {
        return $select.find(`option[value="${value}"]`).data('table');
    }

    return _this.formData;
}
/**
 * 数据校验
 * @param formVal
 */
ReportComponent.prototype.validate = function (formVal) {
    // 数据校验
    if (!formVal.title || formVal.title.trim() == '') {
        showErrorMsg('请输入标题');
        return;
    }
    if (!formVal.fieldX) {
        showErrorMsg('请设置统计维度字段');
        return;
    } else {
        if (dm_date_types.includes(formVal.fieldXType) && !formVal.dateInterval) {
            showErrorMsg('请设置时间间隔');
            return;
        }
        if (dm_number_types.includes(formVal.fieldXType) && !formVal.numberInterval) {
            showErrorMsg('请设置范围');
            return;
        }
    }
    if (!formVal.aggregationType) {
        showErrorMsg('请设置聚合方式');
        return;
    } else {
        if ((formVal.aggregationType == 'sum' || formVal.aggregationType == 'avg') && !formVal.fieldY) {
            showErrorMsg('请设置聚合字段');
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
    if (_this.echarts.length == 0) {
        layer.msg('请先定义图表', {icon: 5})
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