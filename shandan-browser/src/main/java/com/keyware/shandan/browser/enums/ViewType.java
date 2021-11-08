package com.keyware.shandan.browser.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 视图类型
 */
@Getter
@AllArgsConstructor
public enum ViewType {

    //年度视图
    year("INPUTDATE"),
    //部队编号视图
    troop("TROOPCODE"),
    //装备型号视图
    equipment("EQUIPMENTMODEL"),
    //任务代号视图
    task("TASKCODE"),
    //部门视图
    org("");

    private String field;

}
