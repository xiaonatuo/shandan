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
@TableName(value = "SYS_DICT_TYPE", resultMap = "BaseResultMap")
public class SysDictType extends BaseEntity {
    private static final long serialVersionUID = 4551403110917711366L;

    /**
     * 主键
     */
    @TableId("ID")
    private Integer id;

    /**
     * 字典编码
     */
    @TableField("NAME")
    private String name;
}
