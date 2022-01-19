package com.keyware.shandan.system.service.impl;

import com.keyware.shandan.bianmu.entity.DirectoryVo;
import com.keyware.shandan.bianmu.enums.ReviewStatus;
import com.keyware.shandan.bianmu.service.DirectoryService;
import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.common.util.DateUtil;
import com.keyware.shandan.common.util.PoiFileReadUtil;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.common.util.UUIDUtil;
import com.keyware.shandan.frame.properties.CustomProperties;
import com.keyware.shandan.system.entity.SysFile;
import com.keyware.shandan.system.mapper.SysFileMapper;
import com.keyware.shandan.system.queue.provider.EsSysFileProvider;
import com.keyware.shandan.system.service.SysFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private DirectoryService directoryService;

    @Override
    @Transactional
    public SysFile uploadFiles(MultipartFile file, SysFile sysFile) throws Exception {
        if (file.isEmpty()) {
            return null;
        }
        DirectoryVo dir = null;
        if (StringUtils.isNotBlank(sysFile.getEntityId())) {
            dir = directoryService.getById(sysFile.getEntityId());
            if (dir == null) {
                throw new Exception("目录不存在");
            }
        }

        sysFile.setMultipartFile(file);
        //sysFile.setLocation(customProperties.getFileStorage().getLocation());

        String path = DateUtil.getFormatNowDate("yyyy/MM/dd");
        String fileNewName = UUIDUtil.getUUID() + sysFile.getFileSuffix();

        // 检查并创建目录
        String storagePath = customProperties.getFileStorage().getPath();
        File fileDir = new File(storagePath + "/" + path);
        if(!fileDir.exists()){
            if (!fileDir.mkdirs()) {
                throw new IOException("目录创建失败：" + fileDir.getPath());
            }
        }

        sysFile.setPath(path + "/" + fileNewName);

        file.transferTo(new File(storagePath + "/" + sysFile.getPath()));

        if (save(sysFile)) {
            if (dir != null && dir.getReviewStatus() != ReviewStatus.UN_SUBMIT) {
                dir.setReviewStatus(ReviewStatus.UN_SUBMIT);
                directoryService.saveOrUpdate(dir);
            }
        }

        return sysFile;
    }

    @Override
    public void clearFiles() {
        File file = new File(customProperties.getFileStorage().getPath());
        file.deleteOnExit();
    }
}
