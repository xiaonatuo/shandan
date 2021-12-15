package com.keyware.shandan.system.service;

import com.keyware.shandan.system.entity.DesktopSetting;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
     * 保存应用桌面设置
     *
     * @param setting 设置
     * @return
     */
    Boolean saveSetting(DesktopSetting.Setting setting);

    /**
     * 上传图标
     *
     * @param file
     * @return
     */
    String uploadIcon(MultipartFile file) throws IOException;
}
