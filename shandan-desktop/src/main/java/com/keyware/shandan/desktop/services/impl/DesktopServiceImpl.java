package com.keyware.shandan.desktop.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.common.util.MD5Util;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.common.util.UUIDUtil;
import com.keyware.shandan.desktop.config.ProjectProperties;
import com.keyware.shandan.desktop.entity.AppInfo;
import com.keyware.shandan.desktop.entity.DesktopSetting;
import com.keyware.shandan.desktop.mapper.AppInfoMapper;
import com.keyware.shandan.desktop.mapper.DesktopMapper;
import com.keyware.shandan.desktop.services.AppInfoService;
import com.keyware.shandan.desktop.services.DesktopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
    private AppInfoMapper appInfoMapper;

    @Autowired
    private DesktopMapper desktopMapper;

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private ProjectProperties projectProperties;


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
        if(set == null){
            desktopMapper.insert(setting);
        }else{
            desktopMapper.updateById(setting);
        }
        return true;
    }

    @Override
    public String uploadIcon(MultipartFile file) throws IOException {
        return upload(file);
    }

    private String upload(MultipartFile multipartFile) throws IOException {
        String uploadFile = projectProperties.getUploadPath();
        File dir = new File(uploadFile);
        dir.mkdirs();

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
