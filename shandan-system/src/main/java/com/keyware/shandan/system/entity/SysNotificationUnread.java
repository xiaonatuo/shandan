package com.keyware.shandan.system.entity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.keyware.shandan.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * <p>
 * 系统通知未读表
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("SYS_NOTIFICATION_UNREAD")
public class SysNotificationUnread extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("ID")
    private String id;

    /**
     * 用户ID
     */
    @TableField("USER_ID")
    private String userId;

    /**
     * 系统通知ID
     */
    @TableField("NOTIFICATION_ID")
    private String notificationId;

    /**
     * 未读标识
     */
    @TableField(exist = false)
    private String status ="2";

    public SysNotificationUnread() {
    }

    public SysNotificationUnread(String userId, String notificationId) {
        this.userId = userId;
        this.notificationId = notificationId;
    }
}
