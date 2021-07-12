/**
 *  统计报表组件
 *
 * @author GuoXin
 * @since 2021/7/12
 */
const template_main = `
<div id="layer-content-report" >
  <div class="report-main">
      <div class="report-btn-line">
          <a class="layui-btn layui-btn-sm layui-btn-normal" id="btn-add"><i class="layui-icon layui-icon-add-1"></i>定义新报表</a>
          <!--<a class="layui-btn layui-btn-sm layui-btn-normal" id="btn-download"><i class="layui-icon layui-icon-download-circle"></i>下载报表</a>-->
      </div>
      <div class="report-items" id="report-items">
      </div>
  </div>
</div>
`;
const template_config = `
<div class="layui-form" lay-filter="form-echarts-config" id="form-config-pie">
    <div class="layui-form-item" style="padding:0 20px;">
        <div class="layui-form-label" style="width: 100px">标题</div>
        <div class="layui-input-block" style="margin-left: 140px;">
            <input type="text" name="title" class="layui-input" placeholder="设置图表的标题">
        </div>
    </div>
    <div class="layui-form-item" style="padding:0 20px;">
        <div class="layui-form-label" style="width: 100px">图表类型</div>
        <div class="layui-input-block" style="margin-left: 140px;">
            <select name="reportType">
                <option value="pie" selected>饼图</option>
                <option value="bar">柱状图</option>
                <option value="line">折线图</option>
            </select>
        </div>
    </div>
    <div class="layui-form-item" style="padding:0 20px;">
        <div class="layui-form-label" style="width: 100px">统计维度</div>
        <div class="layui-input-block" style="margin-left: 140px;">
            <select name="field" id="selectField" lay-search>
                <option value="" selected>选择字段</option>
                <option value="source">文件来源</option>
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
</div>`;
function ReportComponent(layer, form) {
    this.layer = layer || {};
    this.form = form || {};
}
ReportComponent.Report = class {
    type = ''; // 图表类型('pie', 'bar', 'line')
    /**
     * X轴统计依据
     * @type {{}}
     */
    xAxis = {
        field:'', // 字段
        fieldType: '', // 字段类型
        range: {from:undefined,to:undefined}, // 范围区间
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
 * 打开统计报表主要窗
 */
ReportComponent.prototype.openMainLayer = function(){
    const _this = this;
    layer.open({
        type: 1,
        title: '统计报表',
        area: ['800px', '600px'],
        content: template_main,
        success: function (layero, index) {
            layer.full(index);

            // 新增报表，先选择图表类型，然后再选择需要的字段
            $('#btn-add').on('click', function () {
                _this.openEchartsChooseLayer();
            });
        }
    })
}

ReportComponent.prototype.openEchartsChooseLayer = function () {
    const _this = this;
    layer.open({
        title: '统计报表配置',
        type:1,
        resize: false,
        closeBtn: false,
        area:['600px', '350px'],
        btn: ['确定','取消'],
        content: template_config,
        success: function(layerObj, index){
            _this.form.render('select');
            $('#form-config-pie').parent().css('overflow','visible')
        },
        yes: function(index){

            layer.close(index);
        }
    });
}

/**
 * 设置属性
 * @param value -
 */
ReportComponent.prototype.setFormData = function (value){
    this.formData = value;
}

/**
 * 设置条件参数
 * @param value -
 */
ReportComponent.prototype.setConditions = function (value){
    this.conditions = value;
}