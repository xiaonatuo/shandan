package com.keyware.shandan.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.system.entity.SysNotification;
import com.keyware.shandan.common.service.IBaseService;

/**
 * <p>
 * 系统通知表 服务类
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-11
 */
public interface SysNotificationService extends IBaseService<SysNotification, String> {

    /**
     * 未读通知分页列表
     * @param page
     * @return
     */
    Page<SysNotification> unreadPage(Page<SysNotification> page);

    /**
     * 发送通知
     * @param notification 通知
     * @return
     */
    Boolean sendNotification(SysNotification notification);

    /**
     * 设置已读
     * @param notificationId
     * @return
     */
    Boolean read(String notificationId);
}
