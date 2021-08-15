package com.keyware.shandan.browser.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 条件查询逻辑枚举类
 *
 * @author GuoXin
 * @since 2021/7/1
 */
@Getter
public enum ConditionLogic {
    eq("等于?"),
    nq("不等于?"),
    gt("大于?"),
    lt("小于?"),
    like("包含?"),
    likeLeft("以?开头"),
    likeRight("以?结尾"),
    and("并且"),
    or("或者");

    private final String text;
    //private final String desc;

    ConditionLogic(String text) {
        this.text = text;
    }

}
