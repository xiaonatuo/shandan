package com.keyware.shandan.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.keyware.shandan.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * ES公共字段实体类
 *
 * @author Administrator
 * @since 2021/7/6
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Document(indexName = "shandan")
public class EsCommonEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -3633405631787992830L;

    /**
     * 主键ID
     */
    @TableId("ID")
    @Id
    private String id;

    /**
     * 录入人员
     */
    @Field(type = FieldType.Keyword, fielddata = true)
    @TableField("ENTRYSTAFF")
    private String entryStaff;

    /**
     * 装备型号
     */
    @Field(type = FieldType.Keyword, fielddata = true)
    @TableField("EQUIPMENTMODEL")
    private String equipmentModel;

    /**
     * 收文时间
     */
    @Field(type = FieldType.Keyword, fielddata = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "INPUTDATE")
    private Date inputDate;

    /**
     * 文件来源
     */
    @Field(type = FieldType.Keyword, fielddata = true)
    @TableField("SOURCE")
    private String source;

    /**
     * 任务代号
     */
    @Field(type = FieldType.Keyword, fielddata = true)
    @TableField("TASKCODE")
    private String taskCode;

    /**
     * 任务性质
     */
    @Field(type = FieldType.Keyword, fielddata = true)
    @TableField("TASKNATURE")
    private String taskNature;

    /**
     * 部队代号
     */
    @Field(type = FieldType.Keyword, fielddata = true)
    @TableField("TROOPCODE")
    private String troopCode;

    /**
     * 目标编号
     */
    @Field(type = FieldType.Keyword, fielddata = true)
    @TableField("TARGETNUMBER")
    private String targetNumber;

    /**
     * 导弹编号
     */
    @Field(type = FieldType.Keyword, fielddata = true)
    @TableField("MISSILENUMBER")
    private String missileNumber;

    /**
     * 大文本字段，用于保存文件中的文本信息
     */
    @Field(type = FieldType.Text)
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
