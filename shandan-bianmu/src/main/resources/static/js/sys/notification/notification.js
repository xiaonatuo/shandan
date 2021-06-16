/**
 * <p>
 *  系统通知工具类
 * </p>
 *
 * @author GuoXin
 * @since 2021/6/11
 */
const urls = {
    unreadPageList: `${ctx}/sys/notification/page/unread`,
    allPageList: `${ctx}/sys/notification/page`,
    setRead: `${ctx}/sys/notification/read`
}
const interval = 1000 * 30;
// 数据存储的键
const StorageKeys = {
    DB_NAME: 'NOTIFICATION_DATA',
    UNREAD_MAP_KEY: 'UNREAD_MAP_KEY',
    UNREAD_LIST_KEY: 'UNREAD_LIST_KEY'
}

const SysNotificationUtil = function () {
    this.urls = {
        unreadPageList: `${ctx}/sys/notification/page/unread`,
        allPageList: `${ctx}/sys/notification/page`,
        setRead: `${ctx}/sys/notification/read`
    }
    this.interval = 1000 * 30;

    this.initStorage();
    this.initUnreadMark();
}

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
                if (!_this.setUnread(n)) { // 如果不存在
                    hasUnread = true;
                    break;
                }
            }
            hasUnread && _this.showMark();
        }
    });

    setTimeout(() => {
        _this.getUnreadNotifications();
    }, _this.interval)
}
/**
 * 初始化消息未读标记
 */
SysNotificationUtil.prototype.initUnreadMark = function () {
    let list = this.getUnreadList()
    if (list && list.length > 0) {
        this.showMark();
    }
}

/**
 * 初始化通知存储
 */
SysNotificationUtil.prototype.initStorage = function () {

    let data = _Store.get(StorageKeys.DB_NAME);
    if ($.isEmptyObject(data)) {
        data = {};
        data[StorageKeys.UNREAD_MAP_KEY] = {};
        data[StorageKeys.UNREAD_LIST_KEY] = [];
        _Store.set(StorageKeys.DB_NAME, data);
    }
}

/**
 * 获取未读数据map
 * @returns {*}
 */
SysNotificationUtil.prototype.getUnreadMap = function () {
    let data = _Store.get(StorageKeys.DB_NAME);
    return data[StorageKeys.UNREAD_MAP_KEY];
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
    let data = _Store.get(StorageKeys.DB_NAME);
    return data[StorageKeys.UNREAD_LIST_KEY];
}

/**
 * 设置未读数据并返回数据库中的当前数据
 * @param data 要保存的数据
 * @returns {*} 返回数据库中与当前数据id一致的数据
 */
SysNotificationUtil.prototype.setUnread = function (data) {
    let map = this.getUnreadMap();
    let list = this.getUnreadList();
    let exists = map[data.id];
    if (!exists) {
        map[data.id] = data;
        list.push(data);
        this.save(StorageKeys.UNREAD_MAP_KEY, map);
        this.save(StorageKeys.UNREAD_LIST_KEY, list);
    }
    return exists;
}

/**
 * 保存数据
 * @param key
 * @param data
 */
SysNotificationUtil.prototype.save = function (key, data) {
    const _data = _Store.get(StorageKeys.DB_NAME);
    _data[key] = data;
    _Store.set(StorageKeys.DB_NAME, _data);
};

/**
 * 显示未读消息标记
 */
SysNotificationUtil.prototype.showMark = function () {
    setTimeout(() => {
        $('#unread-mark.layui-hide').removeClass('layui-hide');
    }, 150)
};

/**
 * 从已读数据中移除
 * @param id
 */
SysNotificationUtil.prototype.removeFromUnread = function (id){
    const map = this.getUnreadMap();
    const data = map[id];
    if(data){
        const list = this.getUnreadList();
        const index = list.indexOf(data);
        list.splice(index);
        map[id] = undefined;
        this.save(StorageKeys.UNREAD_MAP_KEY, map);
        this.save(StorageKeys.UNREAD_LIST_KEY, list);
    }
}

/**
 * 设置通知为已读
 * @param nid
 * @param callback
 */
SysNotificationUtil.prototype.setRead = function(nid, callback){
    const _this = this;
    $.post(`${ctx}/sys/notification/read`,{notificationId: nid}, function(res){
        if(res.flag){
            _this.removeFromUnread(nid);
            _this.initUnreadMark();
            callback && callback();
        }
    })
}

const NotificationUtil = new SysNotificationUtil();