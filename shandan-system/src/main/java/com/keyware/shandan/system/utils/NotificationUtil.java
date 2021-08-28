package com.keyware.shandan.system.utils;

import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.common.enums.NotificationType;
import com.keyware.shandan.system.entity.SysNotification;
import com.keyware.shandan.system.entity.SysUser;
import com.keyware.shandan.common.util.StringUtils;

/**
 * <p>
 * 系统通知工具类
 * </p>
 *
 * @author Administrator
 * @since 2021/6/11
 */
public class NotificationUtil {

    /**
     * 生成新的普通通知
     *
     * @param content    通知内容
     * @param receiveIds 接收人ID
     * @return 新的通知
     */
    public static SysNotification newNormalNotify(String content, String receiveIds) {
        return newNotification(NotificationType.NORMAL, null, content, receiveIds);
    }

    /**
     * 生成新的元数据审核通知
     *
     * @param content    通知内容
     * @param receiveIds 接收人ID
     * @return 新的通知
     */
    public static SysNotification newMetadataReviewNotify(String content, String receiveIds) {
        return newNotification(NotificationType.METADATA_REVIEW, null, content, receiveIds);
    }

    /**
     * 生成新的目录审核通知
     *
     * @param content    通知内容
     * @param receiveIds 接收人ID
     * @return 新的通知
     */
    public static SysNotification newDirectoryReviewNotify(String content, String receiveIds) {
        return newNotification(NotificationType.DIRECTORY_REVIEW, null, content, receiveIds);
    }

    /**
     * 生成新的普通通知
     *
     * @param title      通知标题
     * @param content    通知内容
     * @param receiveIds 接收人ID
     * @return 新的通知
     */
    public static SysNotification newNormalNotify(String title, String content, String receiveIds) {
        return newNotification(NotificationType.NORMAL, title, content, receiveIds);
    }

    /**
     * 生成新的元数据审核通知
     *
     * @param title      通知标题
     * @param content    通知内容
     * @param receiveIds 接收人ID
     * @return 新的通知
     */
    public static SysNotification newMetadataReviewNotify(String title, String content, String receiveIds) {
        return newNotification(NotificationType.METADATA_REVIEW, title, content, receiveIds);
    }

    /**
     * 生成新的目录审核通知
     *
     * @param title      通知标题
     * @param content    通知内容
     * @param receiveIds 接收人ID
     * @return 新的通知
     */
    public static SysNotification newDirectoryReviewNotify(String title, String content, String receiveIds) {
        return newNotification(NotificationType.DIRECTORY_REVIEW, title, content, receiveIds);
    }

    /**
     * 生成新的系统通知
     *
     * @param type       通知类型
     * @param title      通知标题
     * @param content    通知内容
     * @param receiveIds 通知接收人ID
     * @return 新的通知
     */
    public static SysNotification newNotification(NotificationType type, String title, String content, String receiveIds) {
        if (StringUtils.isBlank(title)) {
            title = type.getRemark();
        }
        SysUser from = SecurityUtil.getLoginSysUser();
        SysNotification notification = new SysNotification();
        notification.setFromId(from.getUserId());
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setReceiveId(receiveIds);

        return notification;
    }
}
