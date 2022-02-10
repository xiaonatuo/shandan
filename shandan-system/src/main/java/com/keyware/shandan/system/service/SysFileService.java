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

    /**
     * 根据文件MD5获取第一次上传的文件
     *
     * @param md5 md5值
     * @return 文件
     */
    SysFile getFileByMd5AndFirst(String md5);

    /**
     * 根据MD5值获取文件信息集合
     *
     * @param md5 MD5值
     * @return 文件信息集合
     */
    List<SysFile> getFilesByMd5(String md5);

    /**
     * 根据文件的MD5值和文件名和实体ID获取文件信息
     *
     * @param fileMd5  文件MD5值
     * @param name     文件名
     * @param entityId 实体ID
     * @return 文件信息
     */
    SysFile getFileByMd5AndNameAndEntityId(String fileMd5, String name, String entityId);

    /**
     * 根据文件的MD5值和文件名获取文件信息
     *
     * @param fileMd5 文件MD5值
     * @param name    文件名
     * @return 文件信息
     */
    SysFile getFileByMd5AndName(String fileMd5, String name);

    /**
     * 根据文件的MD5值和实体ID获取文件信息
     *
     * @param fileMd5  文件MD5值
     * @param entityId 实体ID
     * @return 文件信息
     */
    SysFile getFileByMd5AndEntityId(String fileMd5, String entityId);

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
     * 单文件上传
     *
     * @param file    上传的文件
     * @param sysFile 文件信息
     * @return 文件信息
     * @throws Exception -
     */
    SysFile uploadFiles(MultipartFile file, SysFile sysFile) throws Exception;

    /**
     * 文件分片上传
     *
     * @param file     上传的文件分片
     * @param fileInfo 文件信息
     * @param chunk    文件分片的信息
     * @throws Exception -
     * @return
     */
    SysFile uploadFileChunk(MultipartFile file, SysFile fileInfo, SysFileChunk chunk) throws Exception;


    /**
     * 合并文件
     *
     * @param file   原始文件信息
     * @param chunks 文件分片集合
     * @return 合并后的文件信息
     * @throws IOException -
     */
    SysFile mergeFileChunk(SysFile file, List<SysFileChunk> chunks) throws IOException;

    /**
     * 清除文件
     */
    void clearFiles();

    /**
     * 检查并自动创建目录层级，并保存文件目录关系
     * @param fileId 文件ID
     * @param currDirId 当前目录ID
     * @param filePath 要生成的目录路径
     * @return 是否成功
     * @throws Exception -
     */
    Boolean autoCreateDirAndUpdateFile(String fileId, String currDirId, String filePath) throws Exception;
}
