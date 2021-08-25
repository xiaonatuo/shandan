package com.keyware.shandan.system.utils;

import com.keyware.shandan.system.entity.SysSetting;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统设置工具类
 * 系统启动时获取数据库数据，设置到公用静态集合sysSettingMap
 * 更新系统设置时同步更新公用静态集合sysSettingMap
 */
public class SysSettingUtil {

    //使用线程安全的ConcurrentHashMap来存储系统设置
    private static final ConcurrentHashMap<String, SysSetting> sysSettingMap = new ConcurrentHashMap<>();

    private static String currentSysId;

    public static void setCurrentSysId(String sysId) {
        currentSysId = sysId;
    }

    //从公用静态集合sysSettingMap获取系统设置
    public static SysSetting getCurrentSysSetting() {
        return sysSettingMap.get(currentSysId);
    }

    public static SysSetting getSysSetting(String sysId) {
        return sysSettingMap.get(sysId);
    }

    //更新公用静态集合sysSettingMap
    public static void setSysSettingMap(SysSetting setting) {
        sysSettingMap.put(setting.getId(), setting);
    }
}
