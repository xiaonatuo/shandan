package com.keyware.shandan.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.keyware.shandan.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 部门表
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "SYS_ORG", resultMap = "BaseResultMap")
public class SysOrg extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("ID")
    private String id;

    /**
     * 部门编号
     */
    @OrderBy
    @TableField("ORG_NUMBER")
    private String orgNumber;

    /**
     * 部门名称
     */
    @TableField(value = "ORG_NAME", condition= SqlCondition.LIKE)
    private String orgName;

    /**
     * 部门简称
     */
    @TableField("ORG_SHORT_NAME")
    private String orgShortName;

    /**
     * 父部门ID
     */
    @TableField("ORG_PARENT_ID")
    private String orgParentId;

    /**
     * 父部门名称
     */
    @TableField("ORG_PARENT_NAME")
    private String orgParentName;

    /**
     * 部门路径
     */
    @TableField("ORG_PATH")
    private String orgPath;

    /**
     * 部门负责人
     */
    @TableField("LEADER")
    private String leader;

    /**
     * 部门负责人名称
     */
    @TableField("LEADER_NAME")
    private String leaderName;

    /**
     * 部门描述
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 排列顺序
     */
    @OrderBy
    @TableField("SORT")
    private Integer sort;

    /**
     * 逻辑删除
     */
    //@TableLogic(value = "0", delval = "1")
    @TableField(value = "IS_DELETE")
    private Boolean deleted;

    /**
     * 子部门
     */
    @TableField(exist = false)
    private List<SysOrg> children;

    @TableField(exist = false)
    private String checked = "0";

}
