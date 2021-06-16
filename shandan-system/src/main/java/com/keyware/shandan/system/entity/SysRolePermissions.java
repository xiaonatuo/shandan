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
 * 系统角色与权限关系表
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "SYS_ROLE_PERMISSIONS", resultMap = "BaseResultMap")
public class SysRolePermissions extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 6530659897115437307L;

    /**
     * 主键
     */
    @TableId("RP_ID")
    private String rpId;

    /**
     * 角色ID
     */
    @TableField("ROLE_ID")
    private String roleId;

    /**
     * 权限ID
     */
    @TableField("PERMIS_ID")
    private String permisId;

    public SysRolePermissions() {}

    public SysRolePermissions(String roleId, String permisId) {
        this.roleId = roleId;
        this.permisId = permisId;
    }
}
