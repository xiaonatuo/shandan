package com.keyware.shandan.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 公共字段枚举类
 *
 * @author GuoXin
 * @since 2021/9/16
 */
@Getter
@AllArgsConstructor
public enum CommonFields {
    TASKCODE("任务代号"),
    TASKNATURE("任务性质"),
    INPUTDATE("收文日期"),
    EQUIPMENTMODEL("装备型号"),
    SOURCE("文件来源"),
    ENTRYSTAFF("录入人员"),
    TROOPCODE("部队代号"),
    TARGETNUMBER("目标/靶标类型"),
    MISSILENUMBER("导弹编号");

    private final String desc;
}
