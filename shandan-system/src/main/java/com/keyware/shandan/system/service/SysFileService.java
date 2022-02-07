package com.keyware.shandan.system.service;

import com.keyware.shandan.system.entity.SysFile;
import com.keyware.shandan.common.service.IBaseService;
import com.keyware.shandan.system.entity.SysFileChunk;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 系统文件表 服务类
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-07
 */
public interface SysFileService extends IBaseService<SysFile, String> {

    SysFile uploadFiles(MultipartFile file, SysFile sysFile) throws Exception;

    void uploadFileChunk(MultipartFile file, SysFile fileInfo, SysFileChunk chunk) throws Exception;

    /**
     * 根据文件MD5获取文件
     *
     * @param md5 md5值
     * @return 文件
     */
    SysFile getFileByMd5(String md5);

    /**
     * 根据文件MD5和文件分片MD5获取文件分片
     *
     * @param fileMd5  文件MD5
     * @param chunkMd5 文件分片MD5
     * @return 文件分片数据
     */
    SysFileChunk getFileChunkByMd5(String fileMd5, String chunkMd5);


    /**
     * 根据文件MD5获取文件分片集合
     *
     * @param fileMd5 文件MD5值
     * @return 文件分片的集合
     */
    List<SysFileChunk> getFileChunksByFileMd5(String fileMd5);

    /**
     * 合并文件
     *
     * @param file   原始文件信息
     * @param chunks 文件分片集合
     * @return 合并后的文件信息
     */
    SysFile mergeFileChunk(SysFile file, List<SysFileChunk> chunks) throws IOException;

    /**
     * 清除文件
     */
    void clearFiles();
}
