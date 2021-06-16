package com.keyware.shandan.system.entity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.keyware.shandan.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * <p>
 * 系统角色菜单关系表
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "SYS_ROLE_MENU", resultMap = "BaseResultMap")
public class SysRoleMenu extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 75125072543477573L;

    /**
     * 主键
     */
    @TableId("RM_ID")
    private String rmId;

    /**
     * 角色ID
     */
    @TableField("ROLE_ID")
    private String roleId;

    /**
     * 菜单ID
     */
    @TableField("MENU_ID")
    private String menuId;

    public SysRoleMenu() { }

    public SysRoleMenu(String roleId, String menuId) {
        this.roleId = roleId;
        this.menuId = menuId;
    }
}
