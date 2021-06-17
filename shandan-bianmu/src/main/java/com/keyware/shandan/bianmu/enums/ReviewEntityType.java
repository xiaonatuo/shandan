package com.keyware.shandan.bianmu.enums;

import lombok.Getter;

/**
 * <p>
 *  审核实体对象类型
 * </p>
 *
 * @author Administrator
 * @since 2021/6/4
 */
@Getter
public enum ReviewEntityType {
    DIRECTORY("目录"),
    METADATA("元数据");

    ReviewEntityType(String remark){
        this.remark = remark;
    }
    private final String remark;
}
