package com.keyware.shandan.system.entity;

import cn.hutool.core.io.FileTypeUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.keyware.shandan.common.entity.BaseEntity;
import com.keyware.shandan.common.util.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

/**
 * <p>
 * 系统文件表
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("SYS_FILE")
@Document(indexName = "shandan", type = "_doc")
public class SysFile extends BaseEntity {
    private static final long serialVersionUID = 9146049308480231565L;

    /**
     * 主键ID
     */
    @TableId("ID")
    @Id
    private String id;

    @TableField(exist = false)
    @Field(value = "META_TYPE")
    private String metaType = "file";

    /**
     * 数据实体ID
     */
    @Field(type = FieldType.Keyword)
    @TableField("ENTITY_ID")
    private String entityId;

    /**
     * 文件名称
     */
    @Field(type = FieldType.Text)
    @TableField("FILE_NAME")
    private String fileName;

    /**
     * 文件后缀
     */
    @TableField("FILE_SUFFIX")
    private String fileSuffix;

    /**
     * 文件类型
     */
    @TableField("FILE_TYPE")
    private String fileType;

    /**
     * 文件大小（单位：MB）
     */
    @TableField("FILE_SIZE")
    private Double fileSize;

    /**
     * 存储位置（local：本地存储，nas：NAS存储）
     */
    @TableField("LOCATION")
    private String location = "local";

    /**
     * 存储路径
     */
    @TableField("PATH")
    private String path;

    /**
     * 文件描述
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 是否分片上传
     */
    @TableField("IS_CHUNK")
    private Boolean isChunk = false;

    /**
     * 是否已经合并分片（用于秒传）
     */
    @TableField("IS_MERGE")
    private Boolean isMerge = false;

    /**
     * 当前分片索引（用于断点续传）
     */
    @TableField("CURRENT_CHUNK_INDEX")
    private int currentChunkIndex;

    /**
     * 文件的MD5值
     */
    @TableField("MD5")
    private String MD5;

    /**
     * 是否第一次上传
     */
    @TableField("IS_FIRST")
    private Boolean isFirst = false;

    /**
     * 文件全路径名称
     */
    @TableField(exist = false)
    private String fileFullName;

    /**
     * 录入人员
     */
    @Field(type = FieldType.Keyword)
    @TableField("ENTRYSTAFF")
    private String entryStaff;

    /**
     * 装备型号
     */
    @Field(type = FieldType.Keyword)
    @TableField("EQUIPMENTMODEL")
    private String equipmentModel;

    /**
     * 任务时间
     */
    @Field(type = FieldType.Date)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @TableField(value = "INPUTDATE")
    private Date inputDate;

    /**
     * 文件来源
     */
    @Field(type = FieldType.Keyword)
    @TableField("SOURCE")
    private String source;

    /**
     * 任务代号
     */
    @Field(type = FieldType.Keyword)
    @TableField("TASKCODE")
    private String taskCode;

    /**
     * 任务性质
     */
    @Field(type = FieldType.Keyword)
    @TableField("TASKNATURE")
    private String taskNature;

    /**
     * 部队代号
     */
    @Field(type = FieldType.Keyword)
    @TableField("TROOPCODE")
    private String troopCode;

    /**
     * 目标/靶标类型
     */
    @Field(type = FieldType.Keyword)
    @TableField("TARGETNUMBER")
    private String targetNumber;

    /**
     * 导弹编号
     */
    @Field(type = FieldType.Keyword)
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

    public SysFile() {
    }

    public Boolean isChunk(){
        return isChunk;
    }
    public Boolean isMerge(){
        return isMerge;
    }
    public boolean isFirst() {
        return isFirst;
    }

    public void setMultipartFile(MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            int index = file.getOriginalFilename().lastIndexOf(".");
            this.fileName = file.getOriginalFilename();
            this.fileSuffix = file.getOriginalFilename().substring(index);
            this.fileSize = file.getSize() / 1024d / 1024d;
            String fileType = FileTypeUtil.getType(file.getInputStream());
            this.fileType = StringUtils.isBlank(fileType) ? this.fileSuffix.substring(1) : fileType;

        } else {
            throw new IOException("file is empty!");
        }
    }
}
