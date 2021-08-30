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
public class DBUserTableVo implements Serializable {
    private static final long serialVersionUID = 1556682373051584100L;

    private String owner;

    private String tableName;

    private String tablespaceName;

    private String tablePrimaryColumn;

    private String tableComment;

    /**
     * 表的字段列集合
     */
    private List<DBTableColumnVo> columnList;
}
