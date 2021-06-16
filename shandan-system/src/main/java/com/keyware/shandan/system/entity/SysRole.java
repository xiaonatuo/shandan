package com.keyware.shandan.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.keyware.shandan.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 系统角色表
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "SYS_ROLE", resultMap = "BaseResultMap")
public class SysRole extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @TableId("ROLE_ID")
    private String roleId;

    /**
     * 角色名称
     */
    @TableField("ROLE_NAME")
    private String roleName;

    /**
     * 角色描述
     */
    @TableField("ROLE_REMARK")
    private String roleRemark;

    /**
     * 角色内容，可访问的url，多个时用,隔开
     */
    @TableField("ROLE_CONTENT")
    private String roleContent;

    /**
     * 菜单列表
     */
    @TableField(exist = false)
    private List<SysMenu> menuList;

    /**
     * 数据权限列表
     */
    @TableField(exist = false)
    private List<SysPermissions> permissionsList;
}
