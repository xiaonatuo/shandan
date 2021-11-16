package com.keyware.shandan.beans;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "SYS_ROLE")
public class RoleVo implements Serializable {
    private static final long serialVersionUID = 8685890039875489037L;
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
}
