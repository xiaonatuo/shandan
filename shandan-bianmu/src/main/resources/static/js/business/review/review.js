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
 * 定义数据表格字段列
 * @type {{DIRECTORY: [{hide: boolean, field: string, title: string}, {field: string, title: string}, {field: string, title: string}, {field: string, title: string}, {field: string, title: string}, null][], METADATA: [{hide: boolean, field: string, title: string}, {field: string, title: string}, {field: string, title: string}, {field: string, title: string}, {field: string, title: string}, null, null][]}}
 */
const cols = {
    METADATA: [[
        {field: 'id', title: 'ID', hide: true},
        {field: 'metadataName', title: '数据名称'},
        {field: 'metadataComment', title: '中文注释'},
        {field: 'themeTask', title: '主题任务'},
        {field: 'modifyUserName', title: '提交人'},
        {field: 'collectionTime', title: '采集时间'},
        {fixed: 'right', title: '操作', toolbar: '#rowToolBar', width:80}
    ]],
    DIRECTORY: [[
        {field: 'id', title: 'ID', hide: true},
        {field: 'directoryName', title: '目录名称'},
        {field: 'directoryPath', title: '目录路径'},
        {field: 'modifyUserName', title: '提交人'},
        {field: 'modifyTime', title: '提交时间'},
        {fixed: 'right', title: '操作', toolbar: '#rowToolBar', width:80}
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
    init(){
        const _this = this;
        let opt = $.extend({}, _this.options);

        layui.use(['layer', 'listPage', 'form'], function () {
            let layer = layui.layer; // 弹出层组件
            let ListPage = layui.listPage; // 数据表格组件
            reviewForm = layui.form;
            const type = opt.entityType.toLowerCase();

            // 数据表格
            const listPage = ListPage.init({
                table: {
                    id: 'reviewTable',
                    url: `${ctx}/business/review/list/${type}`,
                    method: 'get',
                    cols: cols[opt.entityType],
                    done: function (res) {
                        //console.info('数据表格加载完成', res);
                    }
                }
            })

            // 绑定审核按钮点击事件
            listPage.addTableRowEvent('review', function (data) {
                _this.reviewOperate(data,() => listPage.reloadTable());
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
            title: '资源审核',
            area: ['auto'],
            btn:['确定','取消'],
            content: $('#reviewLayer').html(),
            success: function (_layer, index) {
                reviewForm.render();
                reviewForm.on('submit(reviewForm)', function({elem, field}){
                    if(!field.status){
                        layer.msg('请选择审核通过或者不通过！')
                        return false;
                    }
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
            yes:function(){
                $('#btn-submit').click();
            }
        });

    }
}