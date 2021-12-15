/**
 * <p>
 *  client.js
 * </p>
 *
 * @author Administrator
 * @since 2021/12/11
 */
layui.use(['layer', 'gtable', 'form', 'element', 'upload'], function () {
    const layer = layui.layer,
        gtable = layui.gtable;
          form = layui.form;
    // 用于临时存储查询条件
    let searchText = '';


    /**
     * 打开应用编辑页面
     */
    function openAppEditLayer(id) {
        layer.open({
            type: 1,
            area: ['600px', '650px'],
            btn: ['确定', '取消'],
            content: `
                 <form class="layui-form" lay-filter="appInfoForm" style="overflow: hidden; padding: 15px 20px 0 0;">
                    <div class="layui-form-item">
                        <label class="layui-form-label">客户端ID</label>
                        <div class="layui-input-block">
                            <input type="text" name="id" autocomplete="off" lay-verify="required" placeholder="客户端ID" class="layui-input">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">客户端名称</label>
                        <div class="layui-input-block">
                            <input type="text" name="title" lay-verify="required" placeholder="请输入客户端名称" autocomplete="off" class="layui-input">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">访问密匙</label>
                        <div class="layui-input-block">
                            <input type="text" name="clientSecret" lay-verify="required" autocomplete="off" class="layui-input">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">权限范围</label>
                        <div class="layui-input-block">
                            <input type="text" name="scope" autocomplete="off" value="all" class="layui-input" lay-verify="required">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">访问模式</label>
                        <div class="layui-input-block">
                            <input type="text" name="authorizedGrantTypes" autocomplete="off" value="authorization_code,refresh_token" class="layui-input" lay-verify="required">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">客户端访问地址</label>
                        <div class="layui-input-block">
                            <input type="text" name="webServerRedirectUri" autocomplete="off" placeholder="客户端访问地址" class="layui-input" lay-verify="required">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">权限值</label>
                        <div class="layui-input-block">
                            <input type="text" name="authorities" autocomplete="off" value="ROLE_ADMIN,ROLE_USER" class="layui-input" lay-verify="required">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">访问令牌时效</label>
                        <div class="layui-input-block">
                            <input name="accessTokenValidity" autocomplete="off" value="7200" class="layui-input">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">刷新令牌时效</label>
                        <div class="layui-input-block">
                            <input name="refreshTokenValidity" autocomplete="off" value="7200" class="layui-input">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">自动授权</label>
                        <div class="layui-input-block">
                            <input type="radio" name="autoapprove"  value="true" title="是" checked="checked">
                            <input type="radio" name="autoapprove"  value="false" title="否">
                        </div>
                    </div>
                    <!--<div class="layui-form-item">
                        <label class="layui-form-label">展示排序</label>
                        <div class="layui-input-block">
                            <input type="text" name="sort" lay-verify="required|number" value ="1" placeholder="应用排列顺序" autocomplete="off" class="layui-input">
                        </div>
                    </div>-->
                    <div class="layui-form-item">
                        <label class="layui-form-label">打开方式</label>
                        <div class="layui-input-block">
                            <input type="radio" name="target" value="_blank" title="在新页面打开" checked>
                            <input type="radio" name="target" value="_self" title="在当前页打开">
                        </div>
                    </div>
                   
                    <div class="layui-form-item">
                        <label class="layui-form-label">设置图标</label>
                        <div class="layui-input-block">
                            <div class="selected-icon">
                                <div class="current-icon">
                                    <div class="icon" id="icon-content"></div>
                                </div>
                                <div class="upload-btn-area">
                                    <input type="hidden" name="icon" value="">
                                    <a class="layui-btn layui-btn-sm layui-btn-danger" id="upload-icon"><i class="layui-icon layui-icon-upload"></i>上传图标</a>
                                    <div class="layui-word-aux">仅限上传1MB以内的图标</div>
                                </div>
                            </div>
                            <ul class="icon-list">
                                <li class="icon-altbook" data-value="icon-altbook"><div class="mask"></div></li>
                                <li class="icon-approvedsafety" data-value=""><div class="mask"></div></li>
                                <li class="icon-boldness" data-value="icon-boldness"><div class="mask"></div></li>
                                <li class="icon-calculator" data-value="icon-calculator"><div class="mask"></div></li>
                                <li class="icon-cloud" data-value="icon-cloud"><div class="mask"></div></li>
                                <li class="icon-configured" data-value="icon-configured"><div class="mask"></div></li>
                                <li class="icon-dashmac" data-value="icon-dashmac"><div class="mask"></div></li>
                                <li class="icon-directmail" data-value="icon-directmail"><div class="mask"></div></li>
                                <li class="icon-dropbox" data-value="icon-dropbox"><div class="mask"></div></li>
                                <li class="icon-dunpai" data-value="icon-dunpai"><div class="mask"></div></li>
                                <li class="icon-excel" data-value="icon-excel"><div class="mask"></div></li>
                                <li class="icon-fanglian" data-value="icon-fanglian"><div class="mask"></div></li>
                                <li class="icon-googledocsblast" data-value="icon-googledocsblast"><div class="mask"></div></li>
                                <li class="icon-googlemapsrafaga" data-value="icon-googlemapsrafaga"><div class="mask"></div></li>
                                <li class="icon-icalmac" data-value="icon-icalmac"><div class="mask"></div></li>
                                <li class="icon-instagram" data-value="icon-instagram"><div class="mask"></div></li>
                                <li class="icon-internetdownloadmanager" data-value="icon-internetdownloadmanager"><div class="mask"></div></li>
                                <li class="icon-liveMessenger" data-value="icon-liveMessenger"><div class="mask"></div></li>
                                <li class="icon-liveSync" data-value="icon-liveSync"><div class="mask"></div></li>
                                <li class="icon-macappstorealt" data-value="icon-macappstorealt"><div class="mask"></div></li>
                                <li class="icon-magnifier" data-value="icon-magnifier"><div class="mask"></div></li>
                                <li class="icon-newswindows8" data-value="icon-newswindows8"><div class="mask"></div></li>
                                <li class="icon-notebook" data-value="icon-notebook"><div class="mask"></div></li>
                                <li class="icon-pc" data-value="icon-pc"><div class="mask"></div></li>
                                <li class="icon-search" data-value="icon-search"><div class="mask"></div></li>
                                <li class="icon-skyDrive" data-value="icon-skyDrive"><div class="mask"></div></li>
                                <li class="icon-windows8photos" data-value="icon-windows8photos"><div class="mask"></div></li>
                                <li class="icon-windowsmediaplayer" data-value="icon-windowsmediaplayer"><div class="mask"></div></li>
                                <li class="icon-yuansu" data-value="icon-yuansu"><div class="mask"></div></li>
                                <li class="icon-zipfile" data-value="icon-zipfile"><div class="mask"></div></li>
                            </ul>
                        </div>
                    </div>
                    <input type="hidden" name="sort"  value ="1" >
                    <button type="submit" lay-submit id="appInfoSubmit" style="border: none; position: absolute"/>
                </form>
            `,
            success: function (layerObj, index) {
                $('ul.icon-list li').on('click', showDefaultIcon)
                if (id) {
                    getApp(id).then(appInfo => {
                        form.val('appInfoForm', appInfo);
                        let icon;
                        if (appInfo.icon !=null && appInfo.icon.startsWith('icon-')) {
                            $('.current-icon div:first').attr('class', 'icon ' + appInfo.icon);
                        } else if (appInfo.icon.startsWith('images/')) {
                            $('#icon-content').html(`<img src="${ctx}/${appInfo.icon}" alt="" style="width: 100%; height: 100%; border-radius: 5px;"/>`);
                        } else {
                            $('#icon-content').html(`<img src="${appInfo.icon}" alt="" style="width: 100%; height: 100%; border-radius: 5px;"/>`);
                        }
                    })
                }
                form.render()
                bindUploadBtn();
            },
            yes: function (index) {
                form.on('submit(appInfoForm)', function () {
                    let formVal = form.val('appInfoForm');
                    if (!formVal.icon) {
                        layer.msg('请选择图标', {icon: 5});
                        return false;
                    }
                    $.post(`${ctx}/sys/oauth/client/save`, formVal, function (res) {
                        if (res.flag && res.data) {
                            layer.msg('保存成功');
                            layer.close(index);
                            gtable.reload();
                        } else {
                            layer.msg('保存失败', {icon: 5});
                        }
                    });
                    return false; //阻止表单跳转刷新
                });
                $('#appInfoSubmit').click();
            },
            end: function (index) {

            }
        });
    }






    /**
     * 显示默认的图标
     */
    function showDefaultIcon() {
        $(this).addClass('on').siblings().removeClass('on');
        const _class = $(this).attr('class');
        $('.current-icon div:first').attr('class', 'icon ' + _class);
        const icon = $(this).data('value');
        let data = form.val('appInfoForm');
        data.icon = icon;
        form.val('appInfoForm', data);
        $('#icon-content').html('');
    }

    /**
     * 绑定文件上传按钮
     */
    function bindUploadBtn() {
        layui.upload.render({
            elem: '#upload-icon',
            url: `${ctx}/sys/oauth/client/upload/icon`,
            multiple: false,
            auto: true,
            size: 1024, //限制文件大小，单位 KB
            accept: 'images',
            acceptMime: 'image/*', // 只显示图片类型
            done: function (res) {
                if (res.flag) { //上传成功
                    showImgIcon(res.data);
                    let data = form.val('appInfoForm');
                    data.icon = res.data;
                    form.val('appInfoForm', data);
                } else {
                    layer.msg('上传失败', {icon: 5});
                }
            }
        });
    }


    /**
     * 显示上传后的图标
     * @param path
     */
    function showImgIcon(path) {
        alert(17);
        const url = `${ctx}/${path}`;
        const img = `<img src="${url}" alt="" style="width: 100%; height: 100%; border-radius: 5px;" />`
        $('#icon-content').html(img);
        $('ul.icon-list li').removeClass('on')
    }


    /**
     * 查询应用
     * @param id 应用ID
     */
    async function getApp(id) {
        let appInfo = undefined;
        await $.ajax({
            url: `${ctx}/sys/oauth/client/get/${id}`,
            type: 'get',
            async: false,
            success: function (res) {
                if (res.flag) {
                    appInfo = res.data;
                }
            },
            error: function (err) {
                layer.msg('数据请求异常', {icon: 5});
            }
        });
        return appInfo;
    }


    const deleteRow = function(id, callback){
        $.delete(`${ctx}/sys/oauth/client/delete/${id}`, {}, function (res) {
            res.msg = res.flag ? '删除成功' : res.msg;
            let icon = res.flag ? 1 : 5;
            layer.msg(res.msg, {icon, time: 2000}, function () {
                if(res.flag){
                    gtable.reload();
                }
            });
            callback && callback();
        });
    }


    /**
     * 数据表格点击事件
     */
    const tableEventCallback = function(obj){
        let rowData = obj.data;
        switch (obj.event) {
            case 'add':
                openAppEditLayer();
                break;
            case 'query':
                searchText = $('#searchKeyInput').val().trim();
                let queryOps = {page: {current: 1}}
                if(searchText){
                    queryOps.where={id: searchText}
                }
                if(''==searchText){
                    queryOps.where={id: null}
                }
                gtable.reload(queryOps);
                break;
            case 'edit':
                openAppEditLayer(rowData.id);
                break;
            case 'delete':
                layer.confirm('确定要删除该条数据吗？', function (index) {
                    deleteRow(rowData.id, ()=>{layer.close(index)});
                });
                break;
        }
    }

    const dsTableOptions = {
        id: 'dsTable',
        url: `${ctx}/sys/oauth/client/page`,
        method: 'post',
        cols:[[
            {field: 'id', title: '客户端ID'},
            {field: 'title', title: '客户端名称'},
            {field: 'webServerRedirectUri', title: '客户端访问地址'},
            //{field: 'clientSecret', title: '访问密匙'},
            //{field: 'resourceIds', title: '资源ID集合'},
            {
                field: 'scope',
                title: '权限范围',
                //width: 100,
                align: 'center',
                templet: data => data.scope == 'all' ? '全部' : '部分'
            },
            //{field: 'authorizedGrantTypes', title: '访问模式'},
            {field: 'authorities', title: '权限值'},
            //{field: 'accessTokenValidity', title: '访问令牌时效', width: 100},
            //{field: 'refreshTokenValidity', title: '刷新令牌时效', width: 100,},
            //{field: 'additionalInformation', title: ''},
            {
                field: 'autoapprove',
                title: '自动授权',
                //width: 100,
                //align: 'center',
                templet: data => data.autoapprove == 'true'? '是' : '否'
            },
            {fixed: 'right', align:'center', title: '操作', toolbar: '#rowToolBar', width: 150}
        ]],
        onToolBarTable: tableEventCallback,
        onToolBarRow: tableEventCallback,
        done: function (res) {
            $('#searchKeyInput').val(searchText);
        }
    }
    gtable.init(dsTableOptions);
});