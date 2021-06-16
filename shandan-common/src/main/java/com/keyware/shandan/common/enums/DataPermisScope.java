package com.keyware.shandan.common.enums;

import lombok.Getter;

/**
 * 数据权限范围
 */
@Getter
public enum DataPermisScope {
    /**
     * 仅限当前用户的数据
     */
    ONLY_SELF("仅限当前用户的数据"),

    /**
     * 仅限当前机构数据和用户数据
     */
    ONLY_CURRENT_ORG("仅限当前机构数据和用户数据"),

    /**
     * 仅限当前机构及子机构数据和用户数据
     */
    CURRENT_ORG_CHILDRENS("仅限当前机构及子机构数据和用户数据"),

    /**
     * 特殊的数据权限范围，具体应由管理员指定
     */
    SPECIAL("特殊的数据权限范围，具体应由管理员指定"),

    /**
     * 全部数据
     */
    ALL_SCOPE("全部数据");

    private final String remark;

    DataPermisScope(String remark) {
        this.remark = remark;
    }
}
