package com.keyware.shandan.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.keyware.shandan.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.Serializable;
import java.util.Date;

/**
 * 保存到ES存储的文件实体类
 *
 * @author GuoXin
 * @since 2021/6/30
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EsSysFile extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -3350047753584208572L;

    /**
     * 大文本字段，用于保存文件中的文本信息
     */
    @TableField(exist = false)
    private String text;

    /**
     * 创建用户
     */
    @Field
    @TableField(value = "CREATE_USER", fill = FieldFill.INSERT)
    private String createUser;

    /**
     * 创建时间
     */
    @Field
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改用户
     */
    @Field
    @TableField(value = "MODIFY_USER", fill = FieldFill.INSERT_UPDATE)
    private String modifyUser;

    /**
     * 修改时间
     */
    @Field
    @TableField(value = "MODIFY_TIME", fill = FieldFill.INSERT_UPDATE)
    private Date modifyTime;
}
