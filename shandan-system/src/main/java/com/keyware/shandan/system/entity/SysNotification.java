package com.keyware.shandan.system.entity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.keyware.shandan.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.keyware.shandan.common.enums.NotificationType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <p>
 * 系统通知表
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("SYS_NOTIFICATION")
public class SysNotification extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId("ID")
    private String id;

    /**
     * 发送人ID
     */
    @TableField("FROM_ID")
    private String fromId;

    /**
     * 接收人ID，多个接收人ID使用`,`拼接，最多40人
     */
    @TableField("RECEIVE_ID")
    private String receiveId;

    /**
     * 通知标题
     */
    @TableField("TITLE")
    private String title;

    /**
     * 通知内容，纯汉字内容最大不超过500个字符
     */
    @TableField("CONTENT")
    private String content;

    /**
     * 通知类型，NORMAL:普通通知（默认），METADATA_REVIEW:数据资源审核，DIRECTORY_REVIEW:目录审核
     */
    @TableField("TYPE")
    private NotificationType type;

    /**
     * 全部已读
     */
    @TableField("ALL_READ")
    private String allRead;

    /**
     * 发送人
     */
    @TableField(exist = false)
    private SysUser from;

    /**
     * 接收人
     */
    @TableField(exist = false)
    private List<SysUser> receiveList;
}
