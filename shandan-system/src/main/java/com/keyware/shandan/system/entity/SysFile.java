package com.keyware.shandan.system.entity;

import cn.hutool.core.io.FileTypeUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.keyware.shandan.common.entity.BaseEntity;
import com.keyware.shandan.common.util.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
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
@Document(indexName = "sys_file")
public class SysFile extends EsSysFile {

    private static final long serialVersionUID = 9146049308480231565L;

    /**
     * 主键ID
     */
    @TableId("ID")
    @Id
     private String id;

    /**
     * 数据实体ID
     */
    @TableField("ENTITY_ID")
    private String entityId;

    /**
     * 文件名称
     */
    @Field
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
    private String location;

    /**
     * 存储路径
     */
    @TableField("PATH")
    private String path;

    /**
     * 录入人员
     */
    @Field
    @TableField("ENTRYSTAFF")
    private String entryStaff;
    /**
     * 装备型号
     */
    @Field
    @TableField("EQUIPMENTMODEL")
    private String equipmentModel;
    /**
     * 收文时间
     */
    @Field
    @TableField("INPUTDATE")
    private Date inputDate;
    /**
     * 文件来源
     */
    @Field
    @TableField("SOURCE")
    private String source;
    /**
     * 任务代号
     */
    @Field
    @TableField("TASKCODE")
    private String taskCode;
    /**
     * 任务性质
     */
    @Field
    @TableField("TASKNATURE")
    private String taskNature;
    /**
     * 部队代号
     */
    @Field
    @TableField("TROOPCODE")
    private String troopCode;
    /**
     * 目标编号
     */
    @Field
    @TableField("TARGETNUMBER")
    private String targetNumber;
    /**
     * 导弹编号
     */
    @Field
    @TableField("MISSILENUMBER")
    private String missileNumber;


    public SysFile() {
    }

    public void setMultipartFile(MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            int index = file.getOriginalFilename().lastIndexOf(".");
            this.fileName = file.getOriginalFilename().substring(0, index);
            this.fileSuffix = file.getOriginalFilename().substring(index);
            this.fileSize = file.getSize() / 1024d / 1024d;
            String fileType = FileTypeUtil.getType(file.getInputStream());
            this.fileType = StringUtils.isBlank(fileType) ? this.fileSuffix.substring(1) : fileType;

        } else {
            throw new IOException("file is empty!");
        }
    }
}
