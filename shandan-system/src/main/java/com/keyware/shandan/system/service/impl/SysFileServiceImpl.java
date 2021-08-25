package com.keyware.shandan.system.service.impl;

import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.common.util.DateUtil;
import com.keyware.shandan.common.util.PoiFileReadUtil;
import com.keyware.shandan.common.util.UUIDUtil;
import com.keyware.shandan.frame.properties.CustomProperties;
import com.keyware.shandan.system.entity.SysFile;
import com.keyware.shandan.system.mapper.SysFileMapper;
import com.keyware.shandan.system.queue.provider.EsSysFileProvider;
import com.keyware.shandan.system.service.SysFileService;
import org.springframework.beans.factory.annotation.Autowired;
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

        if(sysFile.getFileType().equalsIgnoreCase("txt")){
            PoiFileReadUtil.convertToUTF8(file, f ->{
                try {
                    f.transferTo(new File(storagePath + "/" + sysFile.getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }else{
            file.transferTo(new File(storagePath + "/" + sysFile.getPath()));
        }
        save(sysFile);
        return sysFile;
    }

    @Override
    public void clearFiles() {
        File file = new File(customProperties.getFileStorage().getPath());
        file.deleteOnExit();
    }
}
