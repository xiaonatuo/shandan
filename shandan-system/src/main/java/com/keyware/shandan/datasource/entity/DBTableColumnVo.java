package com.keyware.shandan.datasource.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 当前用户所拥有的表、视图或聚簇的列
 * </p>
 *
 * @author Administrator
 * @since 2021/5/26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "USER_TAB_COLS", resultMap = "BaseResultMap")
public class DBTableColumnVo implements Serializable {
    private static final long serialVersionUID = -2356803615951395756L;

    @TableField("TABLE_NAME")
    private String tableName; // VARCHAR(128) 表、视图或聚簇的名字

    @TableField("COLUMN_NAME")
    private String columnName; // VARCHAR(128) 列名

    @TableField("DATA_TYPE")
    private String dataType; // VARCHAR(128) 列类型名

    @TableField("DATA_LENGTH")
    private String dataLength; // NUMBER 列长度。单位：字节

    @TableField("DATA_PRECISION")
    private String dataPrecision; // NUMBER 精度

    @TableField("DATA_SCALE")
    private String dataScale; // NUMBER 刻度

    @TableField("NULLABLE")
    private String nullAble; // VARCHAR(1) 是否允许为 NULL，Y 是，N 否

    @TableField("DEFAULT_LENGTH")
    private String defaultLength; // NUMBER 默认值长度

    @TableField("DATA_DEFAULT")
    private String dataDefault; // TEXT 默认值

    @TableField("DENSITY")
    private Integer density; // NUMBER 列的密度

    @TableField("AVG_COL_LEN")
    private Integer avgColLen; // NUMBER 列数据的平均长度，单位：字节

    @TableField("CHAR_LENGTH")
    private Integer charLength; // NUMBER 显示时的字符长度，对下述类型有效：CHAR、VARCHAR、NCHAR、NVARCHAR

    @TableField("QUALIFIED_COL_NAME")
    private String qualifiedColName; // VARCHAR(4000) 合格的列名

    @TableField(exist = false)
    private String comment; //注释
}
