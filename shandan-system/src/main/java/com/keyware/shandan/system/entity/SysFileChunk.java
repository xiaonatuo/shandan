package com.keyware.shandan.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.keyware.shandan.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("SYS_FILE_CHUNK")
public class SysFileChunk extends BaseEntity {
    private static final long serialVersionUID = -8732733147355105081L;

    /**
     * 分片ID
     */
    @TableId(value = "ID")
    private String id;
    /**
     * 分片名称
     */
    @TableField("CHUNK_NAME")
    private String chunkName;
    /**
     * 分片的文件路径
     */
    @TableField("CHUNK_PATH")
    private String chunkPath;
    /**
     * 分片MD5
     */
    @TableField("CHUNK_MD5")
    private String chunkMd5;
    /**
     * 分片文件后缀
     */
    @TableField("CHUNK_SUFFIX")
    private String chunkSuffix;
    /**
     * 总的分片数
     */
    @TableField("CHUNK_TOTAL")
    private Integer chunkTotal;
    /**
     * 当前分片索引
     */
    @TableField("CHUNK_INDEX")
    private Integer chunkIndex;//zoneNowIndex;
    /**
     * 文件开始位置
     */
    @TableField("START_OFFSET")
    private Long startOffset;
    /**
     * 文件结束位置
     */
    @TableField("END_OFFSET")
    private Long endOffset;
    /**
     * 文件ID
     */
    @TableField("FILE_ID")
    private String fileId;
    /**
     * 总文件的MD5值
     */
    @TableField("FILE_MD5")
    private String fileMd5;
    /**
     * 文件总大小
     */
    @TableField("FILE_SIZE")
    private Long fileSize;

}
