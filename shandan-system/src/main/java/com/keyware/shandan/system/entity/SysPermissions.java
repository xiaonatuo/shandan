package com.keyware.shandan.system.entity;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.keyware.shandan.common.entity.BaseEntity;
import com.keyware.shandan.common.enums.DataPermisScope;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 系统权限表
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "SYS_PERMISSIONS", resultMap = "BaseResultMap")
public class SysPermissions extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 58946231564246063L;

    /**
     * 权限ID
     */
    @TableId("PERMIS_ID")
    private String permisId;

    /**
     * 权限名称
     */
    @TableField(value = "PERMIS_NAME", condition = SqlCondition.LIKE)
    private String permisName;

    /**
     * 权限描述
     */
    @TableField("PERMIS_REMARK")
    private String permisRemark;

    /**
     * 数据权限范围
     */
    @TableField("PERMIS_SCOPE")
    private DataPermisScope permisScope;

    /**
     * 查询权限
     */
    @TableField("HAS_SELECT")
    private Boolean hasSelect;

    /**
     * 新增权限
     */
    @TableField("HAS_ADD")
    private Boolean hasAdd;

    /**
     * 编辑权限
     */
    @TableField("HAS_EDIT")
    private Boolean hasEdit;

    /**
     * 删除权限
     */
    @TableField("HAS_DELETE")
    private Boolean hasDelete;

    /**
     * 数据权限范围标识值（供前端使用）
     */
    @TableField(exist = false)
    private String permisScopeValue;

    /**
     * 数据权限范围描述（供前端使用）
     */
    @TableField(exist = false)
    private String permisScopeRemark;

    public String getPermisScopeValue() {
        if(this.permisScope == null) return "";
        return this.permisScope.name();
    }

    public String getPermisScopeRemark() {
        if(this.permisScope == null) return "";
        return this.permisScope.getRemark();
    }
}
