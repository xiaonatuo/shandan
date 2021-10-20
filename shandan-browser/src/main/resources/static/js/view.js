layui.use(['form'], function () {
    layui.form.on('select(viewTypeSelector)', function ({value}) {
        Util.get(`/browser/view/${value}`).then(res => {
            if (res.flag) {
                switch (value) {
                    case 'year':
                        initYearView(res.data)
                        break;
                    case 'org':
                        initOrgView(res.data)
                        break;
                    case 'troop':
                        initTroopView(res.data)
                        break;
                    case 'equipment':
                        initEquipmentView(res.data)
                        break;
                    case 'task':
                        initTaskView(res.data)
                }
            }
        })
    });

    /**
     * 初始化年度视图
     * @param data
     */
    function initYearView(data) {

    }

    /**
     * 初始化部门视图
     * @param data
     */
    function initOrgView(data) {

    }

    /**
     * 初始化部队视图
     * @param data
     */
    function initTroopView(data) {

    }

    /**
     * 初始化装备型号视图
     * @param data
     */
    function initEquipmentView(data) {

    }

    /**
     * 初始化任务代号视图
     * @param data
     */
    function initTaskView(data) {

    }

});