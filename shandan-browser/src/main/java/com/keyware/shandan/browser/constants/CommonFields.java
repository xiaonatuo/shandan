package com.keyware.shandan.browser.constants;

import java.util.*;

/**
 * 公共字段常量类
 *
 * @author Administrator
 * @since 2021/7/7
 */
public class CommonFields {
    private final static Map<String, String> FIELDS = new TreeMap<>();
    static {
        FIELDS.put("entryStaff", "录入人员");
        FIELDS.put("equipmentModel", "装备型号");
        FIELDS.put("inputDate", "收文时间");
        FIELDS.put("source", "文件来源");
        FIELDS.put("taskCode", "任务代号");
        FIELDS.put("taskNature", "任务性质");
        FIELDS.put("troopCode", "部队代号");
        FIELDS.put("targetNumber", "目标编号");
        FIELDS.put("missileNumber", "导弹编号");
    }

    public static String get(String key){
        return FIELDS.get(key);
    }

    public static Set<Map.Entry<String, String>> entrySet(){
        return FIELDS.entrySet();
    }
}
