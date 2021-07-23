package com.keyware.shandan.desktop.services;

import com.keyware.shandan.desktop.entity.AppInfo;
import com.keyware.shandan.desktop.entity.DesktopSetting;

import java.util.List;
import java.util.Map;

/**
 * 应用桌面服务接口
 *
 * @author GuoXin
 * @since 2021/7/22
 */
public interface DesktopService {

    /**
     * 管理员认证
     *
     * @param pwd 密码
     * @return
     */
    Boolean auth(String pwd);

    /**
     * 获取应用桌面设置
     *
     * @return
     */
    Map<String, Object> getSetting();

    /**
     * 获取应用列表
     *
     * @return
     */
    List<AppInfo> appList();

    /**
     * 保存应用
     *
     * @param appInfo 应用信息
     * @return
     */
    Boolean saveApp(AppInfo appInfo);

    /**
     * 保存应用桌面设置
     *
     * @param setting 设置
     * @return
     */
    Boolean saveSetting(DesktopSetting.Setting setting);
}
