package com.keyware.shandan.system.service.impl;

import com.keyware.shandan.common.util.MD5Util;
import com.keyware.shandan.common.util.UUIDUtil;
import com.keyware.shandan.frame.properties.CustomProperties;
import com.keyware.shandan.system.entity.DesktopSetting;
import com.keyware.shandan.system.mapper.DesktopMapper;
import com.keyware.shandan.system.service.DesktopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 应用桌面服务接口实现类
 *
 * @author GuoXin
 * @since 2021/7/22
 */
@Service
public class DesktopServiceImpl implements DesktopService {

    @Autowired
    private DesktopMapper desktopMapper;

    @Autowired
    private CustomProperties customProperties;

    @Override
    public Boolean auth(String pwd) {
        return desktopMapper.adminAuth(MD5Util.getMD5(pwd)) > 0;
    }

    @Override
    public Map<String, Object> getSetting() {
        return null;
    }

    @Override
    public Boolean saveSetting(DesktopSetting.Setting setting) {
        DesktopSetting.Setting set = desktopMapper.selectById(setting.getKey());
        if (set == null) {
            desktopMapper.insert(setting);
        } else {
            desktopMapper.updateById(setting);
        }
        return true;
    }

    @Override
    public String uploadIcon(MultipartFile file) throws IOException {
        return upload(file);
    }

    private String upload(MultipartFile multipartFile) throws IOException {
        String uploadFile = customProperties.getFileStorage().getPath();
        File dir = new File(uploadFile);
        if (!dir.mkdirs()) {
            throw new IOException("目录创建失败：" + uploadFile);
        }

        String fileName = multipartFile.getOriginalFilename();
        if (fileName == null) {
            throw new IOException();
        }
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        String path = "/" + UUIDUtil.getUUID() + fileSuffix;
        File tempFile = new File(uploadFile + path);
        multipartFile.transferTo(tempFile);
        return path;
    }
}
