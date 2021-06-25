package com.keyware.shandan.bianmu.entity;
import com.baomidou.mybatisplus.annotation.*;

import java.util.List;

import com.keyware.shandan.bianmu.enums.DirectoryType;
import com.keyware.shandan.bianmu.enums.ReviewStatus;
import com.keyware.shandan.common.entity.BaseEntity;
import com.keyware.shandan.system.entity.SysOrg;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * <p>
 * 目录表
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "B_DIRECTORY", resultMap = "BaseResultMap")
public class DirectoryVo extends BaseEntity{

    private static final long serialVersionUID = 2800844298381044177L;

    /**
     * 主键
     */
    @TableId("ID")
    private String id;

    /**
     * 目录名称
     */
    @TableField("DIRECTORY_NAME")
    private String directoryName;

    /**
     * 父目录ID
     */
    @TableField("PARENT_ID")
    private String parentId;

    /**
     * 目录路径
     */
    @TableField("DIRECTORY_PATH")
    private String directoryPath;

    /**
     * 目录类型（0：结构目录，1：元数据目录）
     */
    @TableField("DIRECTORY_TYPE")
    private DirectoryType directoryType;

    /**
     * 数据审核状态（'UN_SUBMIT'：未提交，'SUBMITTED'：已提交，'PASS'：审核通过，'FAIL'：审核未通过）
     */
    @TableField("REVIEW_STATUS")
    private ReviewStatus reviewStatus;

    /**
     * 所属部门ID
     */
    @TableField(value = "ORG_ID", fill = FieldFill.INSERT)
    private String orgId;
    /**
     * 所属部门对象
     */
    @TableField(exist = false)
    private SysOrg org;

    /**
     * 是否有子级
     */
    @TableField(exist = false)
    private Boolean hasChild;

    /**
     * 子级目录
     */
    @TableField(exist = false)
    private List<DirectoryVo> children;
}
