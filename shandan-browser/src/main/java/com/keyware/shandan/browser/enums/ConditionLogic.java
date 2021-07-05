package com.keyware.shandan.browser.enums;

import lombok.Getter;

/**
 * 条件查询逻辑枚举类
 *
 * @author GuoXin
 * @since 2021/7/1
 */
@Getter
public enum ConditionLogic {
    eq("="),
    nq("!="),
    gt(">"),
    lt("<"),
    like("like %{}%"),
    likeLeft("like %{}"),
    likeRight("like {}%"),
    and("and"),
    or("or");

    private final String text;

    ConditionLogic(String text) {
        this.text = text;
    }

}
