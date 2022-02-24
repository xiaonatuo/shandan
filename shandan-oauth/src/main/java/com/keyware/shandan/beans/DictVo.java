package com.keyware.shandan.beans;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "SYS_DICT")
public class DictVo implements Serializable {
    private static final long serialVersionUID = -6500854659575607594L;
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
}
