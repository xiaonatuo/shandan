package com.keyware.shandan.bianmu.entity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.keyware.shandan.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * <p>
 * 目录与数据资源关系表
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("B_DIRECTORY_METADATA")
public class DirectoryMetadataVo extends BaseEntity{


    private static final long serialVersionUID = -8156088110092054901L;
    /**
     * 主键
     */
    @TableId("ID")
    private String id;

    /**
     * 目录ID
     */
    @TableField("DIRECTORY_ID")
    private String directoryId;

    /**
     * 数据资源ID
     */
    @TableField("METADATA_ID")
    private String metadataId;

    public DirectoryMetadataVo(String directoryId, String metadataId) {
        this.directoryId = directoryId;
        this.metadataId = metadataId;
    }
}
