package com.keyware.shandan.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限范围
 */
@Getter
@AllArgsConstructor
public enum DataPermisScope {
    /**
     * 仅限当前用户的数据
     */
    ONLY_SELF("ONLY_SELF", "仅限当前用户的数据"),

    /**
     * 仅限当前部门数据和用户数据
     */
    ONLY_CURRENT_ORG("ONLY_CURRENT_ORG", "仅限当前部门数据和用户数据"),

    /**
     * 仅限当前部门及子部门数据和用户数据
     */
    CURRENT_ORG_CHILDRENS("CURRENT_ORG_CHILDRENS", "仅限当前部门及子部门数据和用户数据"),

    /**
     * 特殊的数据权限范围，具体应由管理员指定
     */
    SPECIAL("SPECIAL", "特殊的数据权限范围，具体应由管理员指定"),

    /**
     * 全部数据
     */
    ALL_SCOPE("ALL_SCOPE", "全部数据");

    @EnumValue
    private final String value;
    private final String remark;

}
