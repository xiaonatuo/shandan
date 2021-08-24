package com.keyware.shandan.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 应用种类
 *
 * @author GuoXin
 * @since 2021/8/24
 */
@Getter
@AllArgsConstructor
public enum SystemTypes {

    BIANMU("数据分类编目分系统"),
    BROWSER("数据综合浏览分系统"),
    CONTROL("数据管控分系统"),
    DESKTOP("应用桌面分系统");

    @EnumValue
    private final String desc;
}
