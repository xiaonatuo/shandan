layui.extend({
    orgSelector: '{/}/js/sys/org/orgSelector', // {/}的意思即代表采用自有路径，即不跟随 base 路径
})
layui.define(['orgSelector', 'orgTree'], function (exports) {
    const template = `
        <div class="layui-tab layui-tab-card flex-column flex-fill" style="margin: 0">
            <ul class="layui-tab-title">
                <li class="layui-this">机构权限</li>
                <li>目录权限</li>
            </ul>
            <div class="layui-tab-content flex-column flex-fill">
                <div class="layui-tab-item layui-show" id="org-config">机构权限</div>
                <div class="layui-tab-item" id="directory-config">目录权限</div>
            </div>
        </div>
    `
    const defaultConfig = {
        elem: '',
        done: function () {

        }
    }

    const Clazz = function () {
        this.config = $.extend({}, defaultConfig);
        this.permisData = {}
        this.orgIds = '';
        this.dirIds = '';
    };

    Clazz.prototype.init = function (options) {
        this.config = $.extend({}, this.config, options);

    }
    /**
     * 显示配置窗口
     * @param data 权限数据
     */
    Clazz.prototype.showLayer = function (data) {
        console.info(data);
        this.permisData = data;
        const _this = this;
        const _config = this.config;
        layer.open({
            id: 'permis-config-layer',
            title: '权限配置',
            area: ['500px', '600px'],
            shadeClose: true,
            content: template,
            btn: ['确定', '取消'],
            success: function (layerObj, index) {
                $('#permis-config-layer').addClass('flex-column');
                bindOrgTree(_this);
                bindDirectoryTree(_this);
            },
            yes: function (index) {
                const {orgIds, dirIds} = _this;
                save(data.permisId, orgIds, dirIds).then((ok) => {
                    if (ok) {
                        layer.close(index);
                        _config.done();
                    } else {
                        showErrorMsg('保存失败');
                    }
                });
            }
        })
    }

    /**
     * 绑定机构树
     */
    function bindOrgTree(_this) {
        layui.orgTree.init({
            id: 'org-config',
            checkbar: true,
            // 复选框回调配置
            checkbarFun: {
                // 选中后回调
                chooseDone: function (obj) {
                    let ids = obj.map(o => o.id) || [];
                    _this.orgIds = ids.join(',');
                }
            },
            done: () => {
                let id = _this.permisData.permisId;
                getConfigs(id, 'org').then(res => {
                    if (res.flag) {
                        res.data = res.data || [];
                        _this.orgIds = res.data.join(',');
                        layui.orgTree.chooseDataInit(res.data);
                    }
                });
            }
        })
    }

    /**
     * 绑定目录树
     */
    function bindDirectoryTree(_this) {

    }

    async function getConfigs(permisId, type) {
        return await Util.get(`${ctx}/sys/permissions/config/get/${type}/${permisId}`).catch(err => {
            console.error(err);
            showErrorMsg();
        });
    }

    async function save(permisId, orgIds, dirIds) {
        let orgOk = true;
        await saveOrgConfig(permisId, orgIds).then(res => {
            if (!res.flag) {
                orgOk = false
                console.error('机构权限保存失败', res);
            }
        }).catch((err) => {
            orgOk = false
            console.error('机构权限保存失败', err);
        });
        let dirOk = true;
         await saveDirConfig(permisId, dirIds).then(res => {
             if (!res.flag) {
                 dirOk = false
                 console.error('目录权限保存失败', res);
             }
         }).catch((err) => {
             dirOk = false;
             console.error('目录权限保存失败', err);
         });
        return orgOk && dirOk;
    }

    async function saveOrgConfig(permisId, orgIds) {
        const data = {permisId, orgIds}
        return await Util.post(`${ctx}/sys/permissions/config/org`, data);
    }

    async function saveDirConfig(permisId, dirIds) {
        const data = {permisId, dirIds}
        return await Util.post(`${ctx}/sys/permissions/config/dir`, data);
    }


    const _export = {
        init: function (options) {
            const clazz = new Clazz();
            clazz.init(options);
            return clazz;
        }
    }
    exports('permisConfig', _export);
})