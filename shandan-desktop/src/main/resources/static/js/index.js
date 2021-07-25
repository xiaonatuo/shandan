const AUTH_KEY = 'desktop-auth-flag'
layui.use(['layer', 'form', 'element', 'upload'], function () {
    const layer = layui.layer,
        form = layui.form;

    /**
     * 页面加载时，判断session存储中认证标记是否存在，
     * 如果不存在设置session存储认证标记为false（未认证），打开设置页面需要认证
     * 如果认证成功则设置为true，并打开设置页面，
     * 关闭主页面后session存储失效，需要重新认证
     * @type {string}
     */
    let auth = sessionStorage.getItem(AUTH_KEY);
    if (!auth) {
        auth = 'false';
        sessionStorage.setItem(AUTH_KEY, auth);
    }

    // 监听设置按钮
    $('#btn-setting').on('click', function () {
        if (auth == 'true') {
            openSettingLayer()
        } else {
            layer.prompt({title: '请输入管理员（admin）的密码', formType: 1}, function (pass, index) {
                $.post(`${ctx}/desktop/auth`, {pwd: pass}, function (res) {
                    if (res.flag) {
                        if (res.data) {
                            sessionStorage.setItem(AUTH_KEY, res.data + '');
                            openSettingLayer();
                            layer.close(index);
                        } else {
                            layer.msg('密码错误', {icon: 5});
                        }
                    } else {
                        layer.msg('服务异常', {icon: 5});
                    }
                });
            });
        }
    });

    /**
     * 打开设置页面
     */
    function openSettingLayer() {
        layer.open({
            type: 1,
            area: ['600px', '550px'],
            content: `
                <div class="layer-setting">
                    <div class="setting-btn">
                        <a class="layui-btn layui-btn-xs" id="addApp"><i class="layui-icon"></i>添加应用</a>
                        <!--<a class="layui-btn layui-btn-xs" id="setBackground"><i class="layui-icon"></i>设置桌面背景图</a>-->
                    </div>
                    <fieldset class="layui-elem-field layui-field-title" style="margin:0">
                        <legend style="margin-left: 5px; font-size: 16px;color: #4a4a4a;">应用列表</legend>
                    </fieldset> 
                    <div class="layer-app-list" id="layer-app-list">
                        <p style="text-align: center;">暂无数据</p>
                    </div>
                </div>
            `,
            success: function (layerObj, index) {
                getApps();
                bindBtnEvent(true)
            },
            end: function (index) {
                window.location.reload();
            }
        });
    }

    /**
     * 查询应用列表
     */
    function getApps() {
        const loadIndex = layer.load();
        $.get(`${ctx}/desktop/apps`, {}, function (res) {
            layer.close(loadIndex);
            if (res) {
                if (res.length == 0) {
                    $('#layer-app-list').html(`<p style="text-align: center;">暂无数据</p>`);
                    return;
                }
                let appsHtml = '';
                for (let item of res) {
                    const tabStyle = item.target == '_blank' ? 'layui-bg-green' : 'layui-bg-orange'
                    let icon;
                    if (item.icon.startsWith('icon-')) {
                        icon = `<div class="app-icon ${item.icon}"></div>`;
                    } else if (item.icon.startsWith('images/')) {
                        icon = `<img src="${ctx}/${item.icon}" alt="" />`;
                    } else {
                        icon = `<img src="${item.icon}" alt="" />`;
                    }
                    appsHtml += `
                                <div class="app-item">
                                    ${icon}
                                    <div class="app-info">
                                        <p class="title">${item.title}<span class="layui-badge ${tabStyle}">${item.target}</span></p>
                                        <p class="sub-title">${item.url}</p>
                                    </div>
                                    <div class="app-btn">
                                        <a class="layui-btn layui-btn-xs edit" data-id="${item.id}"><i class="layui-icon"></i>编辑</a>
                                        <a class="layui-btn layui-btn-xs delete" data-id="${item.id}"><i class="layui-icon"></i>删除</a>
                                    </div>
                                </div>
                            `
                }
                $('#layer-app-list').html(appsHtml);
                bindBtnEvent(false)
            }
        })
    }

    /**
     * 绑定按钮事件
     * @param isFirst 首次调用，为头部按钮绑定事件，其余为列表行级事件
     */
    function bindBtnEvent(isFirst) {
        if (isFirst) {
            $('#addApp').on('click', function () {
                openAppEditLayer();
            });
            $('#setBackground').on('click', function () {
                openBackgroundLayer();
            })
        } else {
            $('.app-btn a.edit').on('click', function () {
                const id = $(this).data('id')
                openAppEditLayer(id);
            })
            $('.app-btn a.delete').on('click', function () {
                const id = $(this).data('id');
                deleteApp(id);
            });
        }
    }

    /**
     * 打开应用编辑页面
     */
    function openAppEditLayer(id) {
        layer.open({
            type: 1,
            area: ['500px', '573px'],
            btn: ['确定', '取消'],
            content: `
                <form class="layui-form" lay-filter="appInfoForm" style="overflow: hidden; padding: 15px 20px 0 0;">
                    <div class="layui-form-item">
                        <label class="layui-form-label">应用名称</label>
                        <div class="layui-input-block">
                            <input type="text" name="title" lay-verify="required" placeholder="请输入应用名称" autocomplete="off" class="layui-input">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">访问url</label>
                        <div class="layui-input-block">
                            <input type="text" name="url" lay-verify="required" placeholder="请输入访问地址" autocomplete="off" class="layui-input">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">排序</label>
                        <div class="layui-input-block">
                            <input type="text" name="sort" lay-verify="required|number" placeholder="应用排列顺序" autocomplete="off" class="layui-input">
                        </div>
                    </div>
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
                    <input type="hidden" name="id">
                    <button type="submit" lay-submit id="appInfoSubmit" style="border: none; position: absolute"/>
                </form>
            `,
            success: function (layerObj, index) {
                $('ul.icon-list li').on('click', showDefaultIcon)
                if (id) {
                    getApp(id).then(appInfo => {
                        form.val('appInfoForm', appInfo);
                        let icon;
                        if (appInfo.icon.startsWith('icon-')) {
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
                    $.post(`${ctx}/desktop/app/save`, formVal, function (res) {
                        if (res.flag && res.data) {
                            layer.msg('保存成功');
                            layer.close(index);
                            getApps();
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
     * 打开背景图设置页面
     */
    function openBackgroundLayer() {
        layer.open({
            title: '设置背景图',
            type: 1,
            area: ['500px, 400px'],
            content: '',
            success: function (layerObj, index) {

            },
            end: function (index) {

            }
        });
    }

    /**
     * 查询应用
     * @param id 应用ID
     */
    async function getApp(id) {
        let appInfo = undefined;
        await $.ajax({
            url: `${ctx}/desktop/app/${id}`,
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

    /**
     * 删除应用
     * @param id 应用ID
     */
    function deleteApp(id) {
        layer.confirm('确定要删除该应用吗？', {title: '删除确认'}, function () {
            $.delete(`${ctx}/desktop/app/${id}`, {}, function (res) {
                if (res.flag) {
                    getApps();
                    layer.msg('删除成功');
                } else {
                    layer.msg('删除失败', {icon: 5})
                }
            })
        })
    }

    /**
     * 绑定文件上传按钮
     */
    function bindUploadBtn() {
        layui.upload.render({
            elem: '#upload-icon',
            url: `${ctx}/desktop/upload/icon`,
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
        const url = `${ctx}/${path}`;
        const img = `<img src="${url}" alt="" style="width: 100%; height: 100%; border-radius: 5px;" />`
        $('#icon-content').html(img);
        $('ul.icon-list li').removeClass('on')
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
});