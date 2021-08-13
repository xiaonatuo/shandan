package com.keyware.shandan.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 数据密级
 *
 * @author GuoXin
 * @since 2021/8/13
 */
@Getter
public enum SecretLevel {
    /**
     * 公开
     */
    PUBLIC(0, "公开"),
    /**
     * 内部
     */
    INTERNAL(1, "内部"),
    /**
     * 秘密
     */
    SECRET(2, "秘密"),
    /**
     * 机密
     */
    CONFIDENTIAL(3, "机密"),
    /**
     * 绝密
     */
    TOP_SECRET(4, "绝密");

    SecretLevel(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @EnumValue
    private final int value;
    private final String desc;
}
