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
    @Field(value = "ENTRYSTAFF", type = FieldType.Keyword)
    @TableField("ENTRYSTAFF")
    private String entryStaff;

    /**
     * 装备型号
     */
    @Field(value = "EQUIPMENTMODEL", type = FieldType.Keyword)
    @TableField("EQUIPMENTMODEL")
    private String equipmentModel;

    /**
     * 任务时间
     */
    @Field(value = "INPUTDATE", type = FieldType.Date)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @TableField(value = "INPUTDATE")
    private Date inputDate;

    /**
     * 文件来源
     */
    @Field(value = "SOURCE", type = FieldType.Keyword)
    @TableField("SOURCE")
    private String source;

    /**
     * 任务代号
     */
    @Field(value = "TASKCODE", type = FieldType.Keyword)
    @TableField("TASKCODE")
    private String taskCode;

    /**
     * 任务性质
     */
    @Field(value = "TASKNATURE", type = FieldType.Keyword)
    @TableField("TASKNATURE")
    private String taskNature;

    /**
     * 部队代号
     */
    @Field(value = "TROOPCODE", type = FieldType.Keyword)
    @TableField("TROOPCODE")
    private String troopCode;

    /**
     * 目标/靶标类型
     */
    @Field(value = "TARGETNUMBER", type = FieldType.Keyword)
    @TableField("TARGETNUMBER")
    private String targetNumber;

    /**
     * 导弹编号
     */
    @Field(value = "MISSILENUMBER", type = FieldType.Keyword)
    @TableField("MISSILENUMBER")
    private String missileNumber;

    /**
     * 大文本字段，用于保存文件中的文本信息
     */
    @Field(value = "text", type = FieldType.Text)
    @TableField(exist = false)
    private String text;

    /**
     * 创建用户
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "CREATE_USER", fill = FieldFill.INSERT)
    private String createUser;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date)
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改用户
     */
    @Field(type = FieldType.Keyword)
    @TableField(value = "MODIFY_USER", fill = FieldFill.INSERT_UPDATE)
    private String modifyUser;

    /**
     * 修改时间
     */
    @Field(type = FieldType.Date)
    @TableField(value = "MODIFY_TIME", fill = FieldFill.INSERT_UPDATE)
    private Date modifyTime;
}
