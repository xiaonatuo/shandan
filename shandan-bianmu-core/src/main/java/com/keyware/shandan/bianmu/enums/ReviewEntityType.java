package com.keyware.shandan.bianmu.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public enum ReviewEntityType {
    DIRECTORY("DIRECTORY", "目录"),
    METADATA("METADATA", "元数据");

    @EnumValue
    private final String value;
    private final String remark;
}
