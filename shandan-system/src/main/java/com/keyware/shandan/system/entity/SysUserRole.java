package com.keyware.shandan.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.keyware.shandan.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 系统用户角色关系表
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "SYS_USER_ROLE", resultMap = "BaseResultMap")
public class SysUserRole extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关系主键
     */
    @TableId("USER_ROLE_ID")
    private String userRoleId;

    /**
     * 用户ID
     */
    @TableField("USER_ID")
    private String userId;

    /**
     * 角色ID
     */
    @TableField("ROLE_ID")
    private String roleId;

    /**
     * 角色
     */
    @TableField(exist = false)
    private SysRole role;

    /**
     * 用户对象
     */
    @TableField(exist = false)
    private SysUser user;

    /**
     * 角色Id列表，用于接收前端传参
     */
    @TableField(exist = false)
    public String roleIdList;
}
