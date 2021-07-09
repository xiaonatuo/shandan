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
    fields = {};
    echarts = {};
    constructor(options) {
        options = options || {};
        this.type = options.type || '';
        this.data = options.data || '';
        this.condition = options.condition || {};
        this.fields = options.fields || [];
    }
}
function ReportComponent(layer, form) {
    this.template = {
        main: `
            <div id="layer-content-report" >
              <div class="report-main">
                  <div class="report-btn-line">
                      <a class="layui-btn layui-btn-sm layui-btn-normal" id="btn-add"><i class="layui-icon layui-icon-add-1"></i>定义新报表</a>
                      <a class="layui-btn layui-btn-sm layui-btn-normal" id="btn-download"><i class="layui-icon layui-icon-download-circle"></i>下载报表</a>
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
                <div class="layui-form-label" style="width: 100px">选择统计字段</div>
                <div class="layui-input-block" style="margin-left: 140px;">
                    <input type="text" class="layui-input" placeholder="设置图表的标题">
                </div>
            </div>
            <div class="layui-form-item" style="padding:0 20px;">
                <div class="layui-form-label" style="width: 100px">选择统计字段</div>
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
            // report.fields = data;
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
            _this.renderEcharts(report);
            layer.close(index);
        }
    });
}

/**
 * 渲染echarts
 * @param report
 */
ReportComponent.prototype.renderEcharts = function(report){
    console.info(report);
    const _this = this;
    const elemId = `echarts-item-${_this.reports.length + 1}`;
    $('#report-items').append(`<div class="echarts-item" id="${elemId}"></div>`)
    let chartDom = document.getElementById(elemId);
    let echartsItem = echarts.init(chartDom);
    let option = {
        title: {
            text: '某站点用户访问来源',
            subtext: '纯属虚构',
            left: 'center'
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
                name: '访问来源',
                type: 'pie',
                radius: '50%',
                data: [
                    {value: 1048, name: '搜索引擎'},
                    {value: 735, name: '直接访问'},
                    {value: 580, name: '邮件营销'},
                    {value: 484, name: '联盟广告'},
                    {value: 300, name: '视频广告'}
                ],
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
    console.info(this.formData);
}

/**
 * 初始化统计报表组件
 */
/*
ReportComponent.prototype.init = function(){
    const _this = this;

    // 监听统计报表按钮点击事件
    $('#btn-report').on('click', function () {
        // 1、如果没有数据则返回
        /!*if(currPageData.size <= 0){
            layer.msg('当前没有数据可用，请查询到数据后再点击')
            return;
        }*!/
        _this.openMainLayer();
    })
}*/
