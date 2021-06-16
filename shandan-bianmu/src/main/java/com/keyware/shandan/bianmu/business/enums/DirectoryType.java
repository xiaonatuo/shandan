package com.keyware.shandan.bianmu.business.enums;

import lombok.Getter;

/**
 * <p>
 * 目录类型枚举类
 * </p>
 *
 * @author Administrator
 * @since 2021/6/4
 */
@Getter
public enum DirectoryType {
    DIRECTORY("结构目录"),
    METADATA("元数据目录");

    DirectoryType( String remark){
        this.remark = remark;
    }
    private final String remark;
}
