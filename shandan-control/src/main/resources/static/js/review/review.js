/**
 * <p>
 *  资源审核页面通用js
 * </p>
 *
 * @author GuoXin
 * @since 2021/6/3
 */
// 预定义属性
const reviewOps = {};

/**
 * 审核操作窗口html模板
 * @type {string}
 */
const reviewLayerHtml = `
    <div class="layui-form" lay-filter="reviewForm" style="padding: 20px">
        <div class="layui-form-item">
            <label class="layui-form-label">审核</label>
            <div class="layui-input-block">
                <input type="radio" name="status" value="PASS" title="通过" lay-verify="required" lay-filter="reviewStatusRadio">
                <input type="radio" name="status" value="FAIL" title="不通过" lay-verify="required" lay-filter="reviewStatusRadio">
                <input type="radio" name="status" value="REJECTED" title="驳回" lay-verify="required" lay-filter="reviewStatusRadio">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">审核意见</label>
            <div class="layui-input-block">
                <textarea class="layui-textarea" name="opinion" cols="4" lay-verify="required" placeholder="请输入审核意见..." style="width: 450px"/>
            </div>
        </div>
        <button id="btn-submit" class="layui-btn" lay-submit lay-filter="reviewForm"
                style="position: absolute; left:-1000px"/>
    </div>
`;
/**
 * 定义数据表格字段列
 * @type {{DIRECTORY: [{hide: boolean, field: string, title: string}, {field: string, title: string}, {field: string, title: string}, {field: string, title: string}, {field: string, title: string}, null][], METADATA: [{hide: boolean, field: string, title: string}, {field: string, title: string}, {field: string, title: string}, {field: string, title: string}, {field: string, title: string}, null, null][]}}
 */
const cols = {
    METADATA: [[
        {field: 'id', title: 'ID', hide: true},
        {field: 'metadataName', title: '数据表'},
        {field: 'metadataComment', title: '中文注释'},
        {field: 'themeTask', title: '主题任务'},
        {field: 'modifyUserName', title: '提交人'},
        {field: 'collectionTime', title: '采集时间'},
        {fixed: 'right', title: '操作', toolbar: '#rowToolBar', width: 80}
    ]],
    DIRECTORY: [[
        {field: 'id', title: 'ID', hide: true},
        {field: 'directoryName', title: '目录名称'},
        {field: 'directoryPath', title: '目录路径'},
        {field: 'modifyUserName', title: '提交人'},
        {field: 'modifyTime', title: '提交时间'},
        {fixed: 'right', title: '操作', toolbar: '#rowToolBar', width: 80}
    ]]
}
let reviewForm;

class Review {
    options = {
        entityType: '', // 数据实体类型
    };

    constructor(options) {
        this.options = $.extend(true, this.options, options);
    }

    /**
     * 初始化组件
     */
    init() {
        const _this = this;
        let opt = $.extend({}, _this.options);

        layui.use(['layer', 'listPage', 'form'], function () {
            let layer = layui.layer; // 弹出层组件
            let ListPage = layui.listPage; // 数据表格组件
            reviewForm = layui.form;
            const type = opt.entityType.toLowerCase();

            let reviewStatus = 'SUBMITTED';

            // 数据表格
            const listPage = ListPage.init({
                table: {
                    id: 'reviewTable',
                    url: `${ctx}/business/review/list/${type}`,
                    method: 'get',
                    searchFieldNames: opt.entityType == ReviewEntityType.METADATA ? 'metadataName' : 'directoryName',
                    where: {reviewStatus: reviewStatus},
                    cols: cols[opt.entityType],
                    done: function (res) {
                        reviewForm.val('tableToolForm', {reviewStatus:reviewStatus})
                        reviewForm.render('radio', 'tableToolForm');
                    }
                }
            })

            // 绑定审核按钮点击事件
            listPage.addTableRowEvent('review', function (data) {
                _this.reviewOperate(data, () => listPage.reloadTable());
            })

            reviewForm.on('radio(statusRadio)', function (data) {
                reviewStatus = data.value;
                let options = {
                    table: {
                        where: {reviewStatus}
                    }
                }
                listPage.reloadTable(options)
            })
        });
    }

    /**
     * 审核操作
     * @param data 需要审核的数据
     * @param callback 回调
     */
    reviewOperate(data, callback) {
        const _this = this;
        layer.open({
            type: 1,
            title: '审核操作',
            area: ['auto'],
            btn: ['确定', '取消'],
            content: reviewLayerHtml,
            success: function (_layer, index) {
                reviewForm.render();
                reviewForm.on('radio(reviewStatusRadio)', function ({value}) {
                    let opinion = value == 'PASS' ? '同意' : '';
                    reviewForm.val('reviewForm', {opinion});
                })
                reviewForm.on('submit(reviewForm)', function ({elem, field}) {
                    let param = $.extend({}, field);
                    param.entityId = data.id;
                    param.entityType = _this.options.entityType
                    $.post(`${ctx}/business/review/operate`, param, function (res) {
                        if (res.flag) {
                            layer.msg('操作成功');
                            callback && callback();
                        } else {
                            layer.msg('操作失败,' + res.msg);
                        }
                        layer.close(index);
                    });
                    return false;
                });
            },
            yes: function () {
                let formData = reviewForm.val('reviewForm');
                if(formData.status){
                    $('#btn-submit').click();
                }else{
                    layer.msg('请选择审核操作选项！');
                    return false;
                }
            }
        });

    }
}