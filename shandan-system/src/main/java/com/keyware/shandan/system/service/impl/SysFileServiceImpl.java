package com.keyware.shandan.system.service.impl;

import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.common.util.DateUtil;
import com.keyware.shandan.common.util.UUIDUtil;
import com.keyware.shandan.frame.properties.CustomProperties;
import com.keyware.shandan.system.entity.SysFile;
import com.keyware.shandan.system.mapper.SysFileMapper;
import com.keyware.shandan.system.service.SysFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * <p>
 * 系统文件表 服务实现类
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-07
 */
@Service
public class SysFileServiceImpl extends BaseServiceImpl<SysFileMapper, SysFile, String> implements SysFileService {

    @Autowired
    private CustomProperties customProperties;

    @Override
    public SysFile uploadFiles(MultipartFile file, SysFile sysFile) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        sysFile.setMultipartFile(file);
        sysFile.setLocation(customProperties.getFileStorage().getLocation());

        String path = DateUtil.getFormatNowDate("yyyy/MM/dd");
        String fileNewName = UUIDUtil.getUUID() + sysFile.getFileSuffix();

        // 检查并创建目录
        String storagePath = customProperties.getFileStorage().getPath();
        File fileDir = new File(storagePath + "/" +path);
        fileDir.mkdirs();
        sysFile.setPath(path + "/" + fileNewName);
        file.transferTo(new File(storagePath + "/" + sysFile.getPath()));

        save(sysFile);
        return sysFile;
    }

    private synchronized void mkdirs(String path) throws IOException {
        String storagePath = customProperties.getFileStorage().getPath();
        File fileDir = new File(storagePath + "/" +path);
        if(!fileDir.exists()) {
            if(!fileDir.mkdirs()){
                throw new IOException("文件目录创建失败");
            }
        }
    }
}
