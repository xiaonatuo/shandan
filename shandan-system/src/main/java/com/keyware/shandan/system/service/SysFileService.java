package com.keyware.shandan.system.service;

import com.keyware.shandan.system.entity.SysFile;
import com.keyware.shandan.common.service.IBaseService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * <p>
 * 系统文件表 服务类
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-07
 */
public interface SysFileService extends IBaseService<SysFile, String> {

    SysFile uploadFiles(MultipartFile file, SysFile sysFile) throws IOException;

    void clearFiles();
}
