package com.keyware.shandan.bianmu.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public enum DirectoryType {
    DIRECTORY("DIRECTORY","结构目录"),
    METADATA("METADATA","元数据目录");

    /*DirectoryType( String remark){
        this.remark = remark;
    }*/
    @EnumValue
    private final String value;
    private final String remark;
}
