package com.keyware.shandan.system.service.impl;

import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.common.util.DateUtil;
import com.keyware.shandan.common.util.UUIDUtil;
import com.keyware.shandan.system.entity.SysFile;
import com.keyware.shandan.system.mapper.SysFileMapper;
import com.keyware.shandan.system.service.SysFileService;
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

    @Value("${bianmu.file-storage.location}")
    private String storageLocation;

    @Value("${bianmu.file-storage.path}")
    private String storagePath;

    @Override
    public SysFile uploadFiles(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        SysFile sysFile = new SysFile(file);
        sysFile.setLocation(storageLocation);
        if(!storageLocation.equalsIgnoreCase("nas")){
            String path = DateUtil.getFormatNowDate("yyyy/MM/DD");
            String fileNewName = UUIDUtil.getUUID() + sysFile.getFileSuffix();
            // 检查并创建目录
            File fileDir = new File(storagePath + "/" +path);
            if(!fileDir.exists()) {
                if(!fileDir.mkdirs()){
                    throw new IOException("文件目录创建失败");
                }
            }
            sysFile.setPath(path + "/" + fileNewName);
            file.transferTo(new File(storagePath + "/" + sysFile.getPath()));
        }

        save(sysFile);
        return sysFile;
    }
}
