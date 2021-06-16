package com.keyware.shandan.datasource.entity;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 数据库用户拥有的表实体类
 * </p>
 *
 * @author Administrator
 * @since 2021/5/26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "USER_ALL_TABLES", resultMap = "BaseResultMap")
public class DBUserTableVo implements Serializable {
    private static final long serialVersionUID = 1556682373051584100L;

    @TableField(value = "TABLE_NAME", condition = SqlCondition.LIKE)
    private String tableName;// VARCHAR(128) 表名

    @TableField("TABLESPACE_NAME")
    private String tablespaceName;// VARCHAR(128) 表所在表空间名。，临时表和索引组织表为 NULL

    @TableField("STATUS")
    private String status; //VARCHAR(8) 表的状态。UNUSABLE 无效，VALID 有效

    @TableField("BACKED_UP")
    private String backedUp; // VARCHAR(1) 最后一次修改后表是否已备份，Y 是，N 否

    @TableField("NUM_ROWS")
    private String numRows; // NUMBER 表里的记录数

    @TableField("PARTITIONED")
    private String partitioned; // VARCHAR(3) 是否为分区表，YES 是，NO 否 34 IOT_TYPE VARCHAR(8) 堆表为 NULL，其他类型表为 IOT

    @TableField("NESTED")
    private String nested; // VARCHAR(1) 是否为嵌套表，YES 是，NO 否

    @TableField(exist = false)
    private String primaryColumn; // 主键列

    @TableField(exist = false)
    private String comment; // 注释

    /**
     * 表的字段列集合
     */
    @TableField(exist = false)
    private List<DBTableColumnVo> columnList;
}
