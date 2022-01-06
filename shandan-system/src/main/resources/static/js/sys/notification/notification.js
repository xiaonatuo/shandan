/**
 * <p>
 *  系统通知工具类
 * </p>
 *
 * @author GuoXin
 * @since 2021/6/11
 */
layui.define(['notice', 'jquery', 'layer'], function (exports) {

    var notice = layui.notice;
    var layer = layui.layer;
    var $ = layui.jquery;
    let notice01;

    function  openOptions(){
        let options = {
            closeButton:true,//显示关闭按钮
            debug:false,//启用debug
            positionClass:"toast-top-right",//弹出的位置,
            showDuration:"300",//显示的时间
            hideDuration:"1000",//消失的时间
            timeOut:"2000",//停留的时间,0则不自动关闭
            extendedTimeOut:"1000",//控制时间
            showEasing:"swing",//显示时的动画缓冲方式
            hideEasing:"linear",//消失时的动画缓冲方式
            iconClass: 'layui-icon layui-icon-praise', // 自定义图标，有内置，如不需要则传空 支持layui内置图标/自定义iconfont类名,需要完整加上 layui-icon/icon iconfont
            onclick: null, // 点击关闭回调
        }
        notice01 = notice.success("您有未读的系统通知，请查看");
    }

    const SysNotificationUtil = function (_user) {
        this.user = _user;
        // 数据存储的键
        this.StorageKeys = {
            DB_NAME: 'NOTIFICATION_DATA' + "_" + _user.userId,
            UNREAD_MAP_KEY: 'UNREAD_MAP_KEY',
            UNREAD_LIST_KEY: 'UNREAD_LIST_KEY'
        }
        this.urls = {
            unreadPageList: `${ctx}/sys/notification/page/unread`,
            allPageList: `${ctx}/sys/notification/page`,
            setRead: `${ctx}/sys/notification/read`
        }
        this.interval = 1000 * 30;
    }

    SysNotificationUtil.prototype.init = function () {
        this.initStorage();
        this.initUnreadMark();
    };

    /**
     * 轮询获取未读通知
     */
    SysNotificationUtil.prototype.getUnreadNotifications = function () {
        const _this = this;
        $.get(_this.urls.unreadPageList, {size: 999}, function (res) {
            if (res.flag && res.data.records) {
                const list = res.data.records;
                let hasUnread = false;
                for (let n of list) {
                    if (!_this.unreadExistsAndSet(n)) { // 如果不存在
                        hasUnread = true;
                        break;
                    }
                }
                if(hasUnread){
                    _this.showMark();
                }
            }

            if (res.flag) {
                setTimeout(() => {
                    _this.getUnreadNotifications();
                }, _this.interval)
            }
        });

    }
    /**
     * 初始化消息未读标记
     */
    SysNotificationUtil.prototype.initUnreadMark = function () {
        let list = this.getUnreadList();
        if (list && list.length > 0) {
            this.showMark();
        }else{
            this.hideMark();
        }
    }

    /**
     * 初始化通知存储
     */
    SysNotificationUtil.prototype.initStorage = function () {
        const _this = this;
        let data = _Store.get(_this.StorageKeys.DB_NAME);
        if ($.isEmptyObject(data)) {
            data = {};
            data[_this.StorageKeys.UNREAD_MAP_KEY] = {};
            data[_this.StorageKeys.UNREAD_LIST_KEY] = [];
            _Store.set(_this.StorageKeys.DB_NAME, data);
        }
    }

    /**
     * 获取未读数据map
     * @returns {*}
     */
    SysNotificationUtil.prototype.getUnreadMap = function () {
        const _this = this;
        let data = _Store.get(_this.StorageKeys.DB_NAME);
        return data[_this.StorageKeys.UNREAD_MAP_KEY];
    }

    /**
     * 根据ID获取未读数据
     * @param id
     * @returns {*}
     */
    SysNotificationUtil.prototype.getUnreadByID = function (id) {
        return this.getUnreadMap()[id];
    };

    /**
     * 获取未读列表
     * @returns {*}
     */
    SysNotificationUtil.prototype.getUnreadList = function () {
        const _this = this;
        let data = _Store.get(_this.StorageKeys.DB_NAME);
        return data[_this.StorageKeys.UNREAD_LIST_KEY];
    }

    /**
     * 设置未读数据并返回数据库中的当前数据
     * @param data 要保存的数据
     * @returns {*} 返回数据库中与当前数据id一致的数据
     */
    SysNotificationUtil.prototype.unreadExistsAndSet = function (data) {
        const _this = this;
        let map = this.getUnreadMap();
        let list = this.getUnreadList();
        let exists = map[data.id];
        if (!exists) {
            map[data.id] = data;
            list.push(data);
            this.save(_this.StorageKeys.UNREAD_MAP_KEY, map);
            this.save(_this.StorageKeys.UNREAD_LIST_KEY, list);
        }
        return exists;
    }

    /**
     * 保存数据
     * @param key
     * @param data
     */
    SysNotificationUtil.prototype.save = function (key, data) {
        const _this = this;
        const _data = _Store.get(_this.StorageKeys.DB_NAME);
        _data[key] = data;
        _Store.set(_this.StorageKeys.DB_NAME, _data);
    };

    /**
     * 显示未读消息标记
     */
    SysNotificationUtil.prototype.showMark = function () {
        setTimeout(() => {
            $('#unread-mark.layui-hide').removeClass('layui-hide');
            openOptions();
        }, 150)
    };


    SysNotificationUtil.prototype.hideMark = function () {
        setTimeout(() => {
            $('#unread-mark.layui-hide').addClass('layui-hide');
        }, 150)
    };

    /**
     * 从已读数据中移除
     * @param id
     */
    SysNotificationUtil.prototype.removeFromUnread = function (id) {
        const _this = this;
        const map = this.getUnreadMap();
        const data = map[id];
        if (data) {
            const list = this.getUnreadList();
            const index = list.indexOf(data);
            list.splice(index);
            map[id] = undefined;
            this.save(_this.StorageKeys.UNREAD_MAP_KEY, map);
            this.save(_this.StorageKeys.UNREAD_LIST_KEY, list);
        }
    }

    /**
     * 设置通知为已读
     * @param nid
     * @param callback
     */
    SysNotificationUtil.prototype.setRead = function (nid, callback) {
        const _this = this;
        $.post(`${ctx}/sys/notification/read`, {notificationId: nid}, function (res) {
            if (res.flag) {
                _this.removeFromUnread(nid);
                _this.initUnreadMark();
                callback && callback();
            }
        })
    }

    let NotificationUtil;

    const _exports = {
        init: function () {
            let user = sessionStorage.getItem('login_user');
            if(user){
                user = JSON.parse(user);
                NotificationUtil = new SysNotificationUtil(user);
                NotificationUtil.init();
                return NotificationUtil;
            }else{
                console.error('用户数据初始化失败');
            }
        }
    }
    exports('notification', _exports);
});
