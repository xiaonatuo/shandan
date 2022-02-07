package com.keyware.shandan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.keyware.shandan.system.entity.SysFileChunk;
import com.keyware.shandan.system.mapper.SysFileChunkMapper;
import com.keyware.shandan.system.mapper.SysFileMapper;
import com.keyware.shandan.system.queue.provider.EsSysFileProvider;
import com.keyware.shandan.system.service.SysFileService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

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

    @Autowired
    private SysFileChunkMapper fileChunkMapper;

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

        generateFilePath(sysFile);

        file.transferTo(new File(getFileAbsolutePath(sysFile)));

        if (save(sysFile)) {
            if (dir != null && dir.getReviewStatus() != ReviewStatus.UN_SUBMIT) {
                dir.setReviewStatus(ReviewStatus.UN_SUBMIT);
                directoryService.saveOrUpdate(dir);
            }
        }

        return sysFile;
    }

    @Override
    @Transactional
    public void uploadFileChunk(@NotNull MultipartFile uploadFile, SysFile fileInfo, @NotNull SysFileChunk chunk) throws Exception {
        SysFile file = getFileByMd5(chunk.getFileMd5());
        if (file == null) {
            // 没有查询到说明是第一次上传，先保存文件信息
            file = fileInfo;
            String originalFilename = file.getFileName();
            int index = originalFilename.lastIndexOf(".");
            file.setFileName(originalFilename.substring(0, index));
            file.setFileSuffix(originalFilename.substring(index));
            file.setFileSize(file.getFileSize() / 1024d / 1024d);
            file.setFileType(file.getFileSuffix().substring(1));
            file.setPath("");
            file.setIsChunk(true);
            file.setIsMerge(false);
            file.setMD5(chunk.getFileMd5());
            save(file);
        }

        generateFileChunkPath(file, chunk);
        chunk.setChunkName(file.getFileName() + "_" + chunk.getChunkIndex());
        chunk.setFileId(file.getId());
        uploadFile.transferTo(new File(getFileChunkAbsolutePath(chunk)));

        fileChunkMapper.insert(chunk);
        file.setCurrentChunkIndex(chunk.getChunkIndex());
        updateById(file);
    }

    @Override
    public SysFile getFileByMd5(@NotNull String md5) {
        QueryWrapper<SysFile> query = new QueryWrapper<>();
        query.eq("MD5", md5);
        return getOne(query);
    }

    @Override
    public SysFileChunk getFileChunkByMd5(@NotNull String fileMd5, @NotNull String chunkMd5) {
        QueryWrapper<SysFileChunk> query = new QueryWrapper<>();
        query.eq("FILE_MD5", fileMd5).eq("CHUNK_MD5", chunkMd5);
        return fileChunkMapper.selectOne(query);
    }

    @Override
    public List<SysFileChunk> getFileChunksByFileMd5(String fileMd5) {
        QueryWrapper<SysFileChunk> query = new QueryWrapper<>();
        query.eq("FILE_MD5", fileMd5);
        return fileChunkMapper.selectList(query);
    }

    @Override
    @Transactional
    public SysFile mergeFileChunk(@NotNull SysFile file, @NotNull List<SysFileChunk> chunks) throws IOException {
        generateFilePath(file);
        File targetFile = new File(getFileAbsolutePath(file));
        targetFile.deleteOnExit();
        targetFile.createNewFile();
        FileOutputStream outputStream = null;
        for (SysFileChunk chunk : chunks) {
            File chunkFile = new File(getFileChunkAbsolutePath(chunk));
            outputStream = new FileOutputStream(targetFile, true);
            FileUtils.copyFile(chunkFile, outputStream);
            outputStream.close();
        }
        // 更新文件信息
        file.setIsMerge(true);
        updateById(file);
        // 删除文件分片数据
        deleteFileChunkByFile(file);
        // 删除文件分片临时文件
        deleteChunkFolder(file);
        return file;
    }


    @Override
    public void clearFiles() {
        File file = new File(customProperties.getFileStorage().getPath());
        file.deleteOnExit();
    }

    private void deleteFileChunkByFile(SysFile file) {
        QueryWrapper<SysFileChunk> deleteFileChunkQuery = new QueryWrapper<>();
        deleteFileChunkQuery.eq("FILE_MD5", file.getMD5());
        fileChunkMapper.delete(deleteFileChunkQuery);
    }

    /**
     * 生成文件的路径名称（包含文件名称及后缀）
     *
     * @param file 文件信息
     * @return 文件存储路径（文件夹）
     */
    private String generateFilePath(SysFile file) throws IOException {
        String path = DateUtil.getFormatNowDate("yyyy/MM/dd");
        String fileNewName = UUIDUtil.getUUID() + file.getFileSuffix();

        File temp = new File(customProperties.getFileStorage().getPath() + "/" + path);
        if (!temp.exists()) {
            if (!temp.mkdirs()) {
                throw new IOException("目录创建失败：" + temp.getPath());
            }
        }
        file.setPath(path + "/" + fileNewName);
        return path;
    }

    /**
     * 获取文件的绝对路径
     *
     * @param file 文件存储信息
     * @return 文件的绝对路径
     */
    private String getFileAbsolutePath(@NotNull SysFile file) {
        if (StringUtils.isBlank(file.getPath())) {
            return "";
        }
        return customProperties.getFileStorage().getPath() + "/" + file.getPath();
    }

    /**
     * 生成文件分片的路径名称
     *
     * @param file  文件信息
     * @param chunk 文件分片信息
     * @return 文件存储路径（文件夹）
     * @throws IOException -
     */
    private String generateFileChunkPath(SysFile file, SysFileChunk chunk) throws IOException {
        String path = file.getMD5();
        File temp = new File(customProperties.getFileStorage().getTempPath() + "/" + path);
        if (!temp.exists()) {
            if (!temp.mkdirs()) {
                throw new IOException("目录创建失败：" + temp.getPath());
            }
        }
        String fileName = chunk.getChunkMd5() + "_temp.chunk";
        chunk.setChunkPath(path + "/" + fileName);
        return path;
    }

    /**
     * 获取文件分片的绝对路径
     *
     * @param chunk 文件分片信息
     * @return 绝对路径
     */
    private String getFileChunkAbsolutePath(@NotNull SysFileChunk chunk) {
        if (StringUtils.isBlank(chunk.getChunkPath())) {
            return "";
        }
        return customProperties.getFileStorage().getTempPath() + "/" + chunk.getChunkPath();
    }

    private boolean deleteChunkFolder(@NotNull SysFile file) throws IOException {
        if (StringUtils.isBlank(file.getMD5())) {
            return false;
        }
        String path = generateFileChunkPath(file, new SysFileChunk());
        path = customProperties.getFileStorage().getTempPath() + "/" + path;
        return deleteFolder(path);
    }

    private boolean deleteFolder(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        if (!file.isFile()) {
            File[] files = file.listFiles();
            for (int i = 0; i < (files != null ? files.length : 0); i++) {
                String root = files[i].getAbsolutePath();
                deleteFolder(root);
            }
        } else {
            file.delete();
        }
        return true;
    }
}
