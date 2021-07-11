/**
 *  统计报表js组件
 *
 * @author GuoXin
 * @since 2021/7/1
 */
// 统计报表对象
class ReportItem{
    title = '';
    type = '';
    data = {};
    condition = {};
    xField = '';
    yField = ''
    echarts = {};
    constructor(options) {
        options = options || {};
        this.type = options.type || '';
        this.data = options.data || '';
        this.condition = options.condition || {};
    }
}
function ReportComponent(layer, form) {
    this.template = {
        main: `
            <div id="layer-content-report" >
              <div class="report-main">
                  <div class="report-btn-line">
                      <a class="layui-btn layui-btn-sm layui-btn-normal" id="btn-add"><i class="layui-icon layui-icon-add-1"></i>定义新报表</a>
                      <!--<a class="layui-btn layui-btn-sm layui-btn-normal" id="btn-download"><i class="layui-icon layui-icon-download-circle"></i>下载报表</a>-->
                  </div>
                  <div class="report-items" id="report-items">
                  </div>
              </div>
            </div>`,
        chooseType: `
        <div class="layui-form" lay-filter="form-report-select" id="form-report-select">
            <div class="layui-form-item" style="margin: 0; padding:20px;">
                <select name="reportType">
                    <option value="pie" selected>饼图</option>
                    <option value="bar">柱状图</option>
                    <option value="line">折线图</option>
                </select>
            </div>
        </div>`,
        configPie: `
        <div class="layui-form" lay-filter="form-config-pie" id="form-config-pie">
            <div class="layui-form-item" style="padding:0 20px;">
                <div class="layui-form-label" style="width: 100px">标题</div>
                <div class="layui-input-block" style="margin-left: 140px;">
                    <input type="text" class="layui-input" placeholder="设置图表的标题">
                </div>
            </div>
            <div class="layui-form-item" style="padding:0 20px;">
                <div class="layui-form-label" style="width: 100px">统计维度</div>
                <div class="layui-input-block" style="margin-left: 140px;">
                    <select name="field" id="selectField" lay-search>
                        <option value="source" selected>文件来源</option>
                        <option value="entryStaff">录入人员</option>
                        <option value="taskNature">任务性质</option>
                        <option value="taskCode">任务代号</option>
                        <option value="targetNumber">目标编号</option>
                        <option value="troopCode">部队代号</option>
                        <option value="equipmentModel">装备型号</option>
                        <option value="missileNumber">导弹编号</option>
                    </select>
                </div>
            </div>
        </div>`,
        configBar: ``,
        configLine: ``
    };
    this.layer = layer || {};
    this.form = form || {};
    this.formData = {};
    this.conditions = [];
    this.reports = [];
}

/**
 * 打开统计报表主要窗口
 */
ReportComponent.prototype.openMainLayer = function(){
    // 条件表单数据
    const _this = this;
    layer.open({
        //id: 'layer-content-report',
        type: 1,
        title: '统计报表',
        area: ['800px', '600px'],
        content: this.template.main,
        success: function (layero, index) {
            layer.full(index);

            // 新增报表，先选择图表类型，然后再选择需要的字段
            $('#btn-add').on('click', function () {
                _this.openEchartsChooseLayer();
            });
        }
    })
}

/**
 * 打开echarts类型选择框
 */
ReportComponent.prototype.openEchartsChooseLayer = function () {

    const _this = this;
    layer.open({
        title: '请选择图表类型',
        type:1,
        resize: false,
        closeBtn: false,
        area:['300px', '180px'],
        btn: ['确定','取消'],
        content: _this.template.chooseType,
        success: function(layerObj, index){
            _this.form.render('select');
            $('#form-report-select').parent().css('overflow','visible')
        },
        yes: function(index){
            const selectForm = _this.form.val('form-report-select');
            let report = new ReportItem({type: selectForm['reportType']});
            _this.openConfigLayer(report)
            layer.close(index);
        }
    });
};

/**
 * 打开echarts配置窗口
 * @param report
 */
ReportComponent.prototype.openConfigLayer = function(report){
    const _this = this;
    layer.open({
        title: '统计报表参数配置',
        type:1,
        resize: false,
        closeBtn: false,
        area:['400px', '300px'],
        btn: ['确定','取消'],
        content: _this.template.configPie,
        success: function(layerObj, index){
            // 判断是否元数据表，如果是，则查询所有字段配置，并添加到字段下拉框
            let formData = _this.formData
            if(formData.metadataId){
                $.get(`${ctx}/report/metadata/columns/${formData.metadataId}`,{}, function(res){
                    if(res.flag && res.data){
                        let cols = JSON.parse(res.data);
                        for (let item of cols) {
                            if(item.columnName.toLowerCase().endsWith('id')){continue}
                            let text = item.comment || item.columnName;
                            let value = item.columnName;
                            $('#selectField').append(`<option value="${value}">${text}</option>`);
                        }
                    }
                    _this.form.render('select');
                })
            }else{
                _this.form.render('select');
            }
            $('#form-config-pie').parent().css('overflow','visible')
        },
        yes: function(index){
            const selectForm = _this.form.val('form-config-pie');
            if(report.type == 'pie'){
                report.xField = selectForm.field;
            }
            _this.requestData(report);
            layer.close(index);
        }
    });
}

/**
 * 请求数据
 */
ReportComponent.prototype.requestData = function(report){
    const _this = this;
    const data = {
        type: report.type,
        fieldX: report.xField,
        fieldY: report.yField,
        conditions: _this.conditions
    }
    $.post(`${ctx}/report/data`, data, function(res){
        if(res.flag){
            report.data = res.data;
            _this.renderEcharts(report);
        }
    });
}

/**
 * 渲染echarts
 * @param report
 */
ReportComponent.prototype.renderEcharts = function(report){
    //console.info(report);
    const _this = this;
    const elemId = `echarts-item-${_this.reports.length + 1}`;
    $('#report-items').append(`<div class="echarts-item" id="${elemId}"></div>`)
    let chartDom = document.getElementById(elemId);
    let echartsItem = echarts.init(chartDom);
    let option = {
        title: {
            text: report.title || '统计图',
            left: 'center'
        },
        toolbox: {
            show: true,
            feature: {
                dataView: {
                    readOnly: false,
                    title: '数据视图'
                },
                saveAsImage: {title:'下载'}
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
                type: report.type,
                radius: '50%',
                data: report.data.series,
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
    if(report.type !== 'pie'){
        option.xAxis = [
            {
                type: 'category',
                data: report.data.xAxis,
                axisTick: {
                    alignWithLabel: true
                }
            }
        ];
        option.yAxis = [{type: 'value'}]
    }
    option && echartsItem.setOption(option);
    report.echarts = echartsItem;
    _this.addReport(report);
}

/**
 * 添加统计报表对象
 * @param report
 */
ReportComponent.prototype.addReport = function (report) {
    this.reports.push(report);
};

/**
 * 设置属性
 * @param key
 * @param value
 */
ReportComponent.prototype.setFormData = function (value){
    this.formData = value;
}

/**
 * 设置条件参数
 * @param value
 */
ReportComponent.prototype.setConditions = function (value){
    this.conditions = value;
}