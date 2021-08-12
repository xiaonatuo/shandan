layui.define([], function (exports) {
    const defaultConfig = {
        elem: '',
        oncheck:()=>{}
    }

    const Clazz = function () {
        this.config = $.extend({}, defaultConfig, options);
    };

    Clazz.prototype.init = function (options) {
        this.config = $.extend({}, this.config, options)

    }
    /**
     * 显示配置窗口
     * @param parentId
     */
    Clazz.prototype.showLayer = function (parentId) {

    }

    function getOrgTree(){
        Util.get(``).then(res => {

        }).catch(() => showErrorMsg());
    }


    const _export = {
        init: function (options) {
            const clazz = new Clazz();
            clazz.init(options);
            return clazz;
        },
        bind: function(elem){
            return _export.init({elem})
        }
    }
    exports('orgSelector', _export);
})