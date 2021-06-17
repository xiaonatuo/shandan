package com.keyware.shandan.bianmu.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.keyware.shandan.common.entity.BaseEntity;
import com.keyware.shandan.datasource.entity.DBTableColumnVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 元数据详情表
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "B_METADATA_DETAILS", resultMap = "BaseResultMap")
public class MetadataDetailsVo extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 7522226661327042818L;

    /**
     * 主键
     */
    @TableId("ID")
    private String id;

    /**
     * 元数据ID
     */
    @TableField("METADATA_ID")
    private String metadataId;

    /**
     * 元数据对象
     */
    @TableField(exist = false)
    private MetadataBasicVo basic;

    /**
     * 数据表名称
     */
    @TableField("TABLE_NAME")
    private String tableName;

    /**
     * 是否主表 （1：是，0：否）
     */
    @TableField("master")
    private Boolean master;

    /**
     * 数据表注释
     */
    @TableField("TABLE_COMMENT")
    private String tableComment;

    /**
     * 数据表主键列
     */
    @TableField("TABLE_PRIMARY_COLUMN")
    private String tablePrimaryColumn;

    /**
     * 表的外键
     */
    @TableField("FOREIGN_COLUMN")
    private String foreignColumn;

    /**
     * 外表
     */
    @TableField("FOREIGN_TABLE")
    private String foreignTable;

    /**
     * 数据表字段
     */
    @TableField("TABLE_COLUMNS")
    private String tableColumns;

    /**
     * 表的字段列集合
     */
    @TableField(exist = false)
    private List<DBTableColumnVo> columnList;

}
