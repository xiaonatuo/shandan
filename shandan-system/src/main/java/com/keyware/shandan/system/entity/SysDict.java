package com.keyware.shandan.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.keyware.shandan.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 系统数据字典表
 * </p>
 *
 * @author GuoXin
 * @since 2021-11-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("SYS_DICT")
public class SysDict extends BaseEntity {

    private static final long serialVersionUID = 4370118195317042167L;

    /**
     * 主键
     */
    @TableId("ID")
    private Integer id;

    /**
     * 字典编码
     */
    @TableField("CODE")
    private String code;

    /**
     * 字典类型ID
     */
    @TableField("TYPE_ID")
    private Integer typeId;

    /**
     * 字典名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 字典值
     */
    @TableField("VALUE")
    private String value;

    /**
     * 字典描述
     */
    @TableField("DESC")
    private String desc;

    /**
     * 排序
     */
    @TableField("ORDER")
    private Integer order;

    /**
     * 状态，0：停用，1：启用
     */
    @TableField("STATE")
    private Boolean state;

    /**
     * 数据字典类型对象
     */
    @TableField(exist = false)
    private SysDictType type;

    /**
     * @return 数据字典类型名称
     */
    public String getTypeName() {
        if (type != null) return this.type.getName();
        return "";
    }
}
