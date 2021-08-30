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
//@TableName(value = "USER_TAB_COLS", resultMap = "BaseResultMap")
public class DBTableColumnVo implements Serializable {
    private static final long serialVersionUID = -2356803615951395756L;

    private String owner;

    private String tableName; // VARCHAR(128) 表、视图或聚簇的名字

    private String columnName; // VARCHAR(128) 列名

    private String dataType; // VARCHAR(128) 列类型名

    private Integer dataLength; // NUMBER 列长度。单位：字节

    private Integer dataPrecision; // NUMBER 精度

    private Integer dataScale; // NUMBER 刻度

    private String nullAble; // VARCHAR(1) 是否允许为 NULL，Y 是，N 否

    private String dataDefault; // TEXT 默认值

    private String comment; //注释
}
