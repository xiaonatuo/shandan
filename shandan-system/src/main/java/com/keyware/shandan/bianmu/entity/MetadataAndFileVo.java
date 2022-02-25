package com.keyware.shandan.bianmu.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Administrator
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MetadataAndFileVo extends MetadataBasicVo {
    private static final long serialVersionUID = 6410020702897065827L;

    private String directoryId;
}