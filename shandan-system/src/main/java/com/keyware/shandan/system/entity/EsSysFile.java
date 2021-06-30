package com.keyware.shandan.system.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 保存到ES存储的文件实体类
 *
 * @author GuoXin
 * @since 2021/6/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EsSysFile extends SysFile implements Serializable {

    private static final long serialVersionUID = -3350047753584208572L;

    /**
     * 大文本字段，用于保存文件中的文本信息
     */
    private String text;
}
