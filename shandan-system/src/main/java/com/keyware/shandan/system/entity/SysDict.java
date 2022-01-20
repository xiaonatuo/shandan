package com.keyware.shandan.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.keyware.shandan.common.entity.BaseEntity;
import com.keyware.shandan.common.util.StringUtils;
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
@TableName(value = "SYS_DICT", resultMap = "BaseResultMap")
public class SysDict extends BaseEntity {

    private static final long serialVersionUID = 4370118195317042167L;

    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 字典编码
     */
    @TableField("DICT_CODE")
    private String dictCode;

    /**
     * 字典类型ID
     */
    @TableField("TYPE_ID")
    @OrderBy(isDesc = false, sort = 1)
    private String typeId;

    /**
     * 字典名称
     */
    @TableField("DICT_NAME")
    private String dictName;

    /**
     * 字典值
     */
    @TableField("DICT_VALUE")
    private String dictValue;

    /**
     * 字典描述
     */
    @TableField("DICT_DESC")
    private String dictDesc;

    /**
     * 排序
     */
    @TableField("DICT_ORDER")
    @OrderBy(isDesc = false, sort = 2)
    private Integer dictOrder;

    /**
     * 状态，0：停用，1：启用
     */
    @TableField("DICT_STATE")
    private Boolean dictState;

    /**
     * 数据字典类型对象
     */
    @TableField(exist = false)
    private SysDictType type;

    /**
     * @return 数据字典类型名称
     */
    public String getTypeName() {
        if (type != null) {
            return this.type.getName();
        }
        return "";
    }

    /**
     *
     * @return 获取字典值，如果dictValue为空，则返回dictCode
     */
    public String getValue(){
        if(StringUtils.isBlank(this.dictValue)){
            return this.dictCode;
        }
        return this.dictValue;
    }
}
