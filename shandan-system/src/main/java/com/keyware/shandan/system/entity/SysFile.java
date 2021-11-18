package com.keyware.shandan.system.entity;

import cn.hutool.core.io.FileTypeUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.keyware.shandan.common.util.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
public class SysFile extends EsCommonEntity {

    private static final long serialVersionUID = 9146049308480231565L;

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
    private String location;

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
