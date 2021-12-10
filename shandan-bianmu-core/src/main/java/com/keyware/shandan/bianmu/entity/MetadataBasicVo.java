package com.keyware.shandan.bianmu.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import com.keyware.shandan.bianmu.enums.ReviewStatus;
import com.keyware.shandan.common.entity.BaseEntity;
import com.keyware.shandan.common.enums.SecretLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 元数据表
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "B_METADATA_BASIC", resultMap = "BaseResultMap")
public class MetadataBasicVo extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 8359416863637721055L;

    /**
     * 主键
     */
    @TableId("ID")
    private String id;

    /**
     * 数据源表ID
     */
    @TableField("DATA_SOURCE_ID")
    private String dataSourceId;

    /**
     * 元数据名称
     */
    @TableField(value = "METADATA_NAME", condition = SqlCondition.LIKE)
    private String metadataName;

    /**
     * 元数据表中文注释
     */
    @TableField(value = "METADATA_COMMENT", condition = SqlCondition.LIKE)
    private String metadataComment;

    /**
     * 数据来源
     */
    @TableField("DATA_FROM")
    private String dataFrom;

    /**
     * 数据类型
     */
    @TableField("DATA_TYPE")
    private String dataType;

    /**
     * 数据密级
     */
    @TableField("SECRET_LEVEL")
    private Integer secretLevel;

    /**
     * 采集时间
     */
    @TableField("COLLECTION_TIME")
    private Date collectionTime;

    /**
     * 主题任务
     */
    @TableField("THEME_TASK")
    private String themeTask;

    /**
     * 数据审核状态（'UN_SUBMIT'：未提交，'SUBMITTED'：已提交，'PASS'：审核通过，'FAIL'：审核未通过）
     */
    @TableField("REVIEW_STATUS")
    @OrderBy()
    private ReviewStatus reviewStatus;

    @TableField(exist = false)
    private String modifyUserName;

    /**
     * 所属部门ID
     */
    @TableField(value = "ORG_ID", fill = FieldFill.INSERT)
    private String orgId;

    @TableField(exist = false)
    private List<MetadataDetailsVo> metadataDetailsList;

}
