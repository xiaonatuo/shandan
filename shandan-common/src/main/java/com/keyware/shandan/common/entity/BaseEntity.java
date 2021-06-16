package com.keyware.shandan.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 公共字段实体类
 */
@Getter
@Setter
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = -1381566849518797246L;


    /**
     * 创建用户
     */
    @TableField(value = "CREATE_USER", fill = FieldFill.INSERT)
    private String createUser;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改用户
     */
    @TableField(value = "MODIFY_USER", fill = FieldFill.INSERT_UPDATE)
    private String modifyUser;

    /**
     * 修改时间
     */
    @TableField(value = "MODIFY_TIME", fill = FieldFill.INSERT_UPDATE)
    private Date modifyTime;

}
