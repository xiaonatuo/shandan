package com.keyware.shandan.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.keyware.shandan.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 组织机构表
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
     * 机构编号
     */
    @OrderBy
    @TableField("ORG_NUMBER")
    private String orgNumber;

    /**
     * 机构名称
     */
    @TableField(value = "ORG_NAME", condition= SqlCondition.LIKE)
    private String orgName;

    /**
     * 机构简称
     */
    @TableField("ORG_SHORT_NAME")
    private String orgShortName;

    /**
     * 父机构ID
     */
    @TableField("ORG_PARENT_ID")
    private String orgParentId;

    /**
     * 父机构名称
     */
    @TableField("ORG_PARENT_NAME")
    private String orgParentName;

    /**
     * 机构路径
     */
    @TableField("ORG_PATH")
    private String orgPath;

    /**
     * 机构负责人
     */
    @TableField("LEADER")
    private String leader;

    /**
     * 机构负责人名称
     */
    @TableField("LEADER_NAME")
    private String leaderName;

    /**
     * 机构描述
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
    @TableLogic(value = "0", delval = "1")
    @TableField(value = "IS_DELETE")
    private Boolean deleted;

    /**
     * 子机构
     */
    @TableField(exist = false)
    private List<SysOrg> children;

}
