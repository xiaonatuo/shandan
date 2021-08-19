package com.keyware.shandan.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.keyware.shandan.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 系统设置表
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "SYS_SETTING", resultMap = "BaseResultMap")
public class SysSetting extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表id
     */
    @TableId("ID")
    private String id;

    /**
     * 系统名称
     */
    @TableField("SYS_NAME")
    private String sysName;

    /**
     * 系统logo图标
     */
    @TableField("SYS_LOGO")
    private String sysLogo;

    /**
     * 系统底部信息
     */
    @TableField("SYS_BOTTOM_TEXT")
    private String sysBottomText;

    /**
     * 系统公告
     */
    @TableField("SYS_NOTICE_TEXT")
    private String sysNoticeText;

    /**
     * 用户管理：初始、重置密码
     */
    @TableField("USER_INIT_PASSWORD")
    private String userInitPassword;

    /**
     * 系统颜色
     */
    @TableField("SYS_COLOR")
    private String sysColor;

    /**
     * API是否加密
     */
    @TableField("SYS_API_ENCRYPT")
    private Boolean sysApiEncrypt;

}
