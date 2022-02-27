package com.keyware.shandan.bianmu.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 目录复制功能接口数据传输对象
 */
@Data
public class DirCopyDTO implements Serializable {

    private static final long serialVersionUID = 6457656734510446800L;

    /**
     * 目标目录id
     */
    @NotNull
    private String targetId;

    /**
     * 复制的项目
     */
    @NotNull
    private List<Item> items;

    /**
     * 数据项
     */
    @Getter
    @Setter
    public static class Item {
        /**
         * 资源id
         */
        private String id;
        /**
         * 资源类型： 目录、数据资源、文件
         */
        private Type type;
        /**
         * 所属（父）目录ID
         */
        private String parentDirId;
    }

    /**
     * 资源类型
     */
    public enum Type {
        directory,
        metadata,
        file;
    }
}
