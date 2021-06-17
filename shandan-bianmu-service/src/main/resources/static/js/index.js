layui.use(['element', 'flow', 'util'], function () {
    let element = layui.element;

    // 右侧用户导航栏不显示点击状态样式
    element.on('nav(user-nav)', function(elem){
        $('#user-nav li').removeClass('layui-this')
        if(elem.data('id') === 'unread'){
            layer.open({
                type:2,
                title: '系统通知',
                area:['550px', `${frameHeight+40}px`],
                shadeClose : true,
                btn:[],
                closeBtn: 0,
                move:false,
                offset: 'r', //右侧弹出
                anim: 5,
                content: `${ctx}/sys/notification/`,
            });
        }
    });

    /**
     * 水平导航栏监听
     */
    element.on('nav(navbar-top)', function (elem) {
        let menuId = $(elem).data("id")
        let url = $(elem).data('url');
        let hasChild = $(elem).data("childrenLength") > 0;
        if(hasChild){
            sideNavRender(getMenuChildren(menuId, menuList));
            element.render("nav");
            // 默认点击第一个
            $("#navbar-side li:first a:first").click();
            navToggle(hasChild);
        }else{
            navToggle(hasChild);
            $("#containerFrame").attr("src", url)
        }
    });

    /**
     * 侧边导航栏监听
     */
    element.on('nav(navbar-side)', function (elem) {
        const url = $(elem).data('url');
        const hasChild = $(elem).data('childLength') > 0;
        if(hasChild){

        }else{
            $("#containerFrame").attr("src", url)
        }
    })

    /**
     * 水平导航栏默认点击第一个
     */
    const navbarInit = function () {
        $("#navbar-top li:first a:first").click();
    }

    /**
     * 设置侧边导航栏菜单
     * @param menus
     */
    const sideNavRender = function (menus) {
        let htm = ``;
        if(menus && Array.isArray(menus)) {
            for (let menu of menus) {
                htm += `<li class="layui-nav-item">
                        <a data-url="${menu.menuPath}" data-child-length="${menu.children.length}"
                           data-id="${menu.menuId}" href="javascript:;">${menu.menuName}</a>
                    </li>`;
            }
        }
        $("#navbar-side").html(htm);
    }

    /**
     * 递归获取子菜单
     * @param menuId
     * @param menus
     * @returns {*}
     */
    const getMenuChildren = function (menuId, menus) {
        for (let menu of menus) {
            if (menuId === menu.menuId) {
                return menu.children;
            } else if (menu.children.length > 0) {
                let children = getMenuChildren(menuId, menu.children);
                if(children){
                    return children;
                }
            }
        }
    }

    /**
     * 侧边导航栏显示隐藏切换
     * @param isShow
     */
    const navToggle = function (isShow) {
        if (isShow === undefined) {
            $("#nav-col").toggleClass("layui-hide");
            $("#container-body").toggleClass("layui-body-width-max")
        } else {
            if (isShow) {
                $("#nav-col").removeClass("layui-hide");
                $("#container-body").removeClass("layui-body-width-max")
            } else {
                $("#nav-col").addClass("layui-hide");
                $("#container-body").addClass("layui-body-width-max")
            }
        }
    }

    /**
     * 监听浏览器可视高度，自适应设置高度
     */
    window.onresize = function (ev) {
        $(".tab-content-div").css("height", $(".layui-body")[0].offsetHeight + "px");
    }
    navbarInit();

    // 轮询查询系统未读通知
    NotificationUtil.getUnreadNotifications();
})

