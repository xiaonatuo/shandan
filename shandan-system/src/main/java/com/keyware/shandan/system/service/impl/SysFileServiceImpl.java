package com.keyware.shandan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.bianmu.entity.DirectoryVo;
import com.keyware.shandan.bianmu.enums.DirectoryType;
import com.keyware.shandan.bianmu.enums.ReviewStatus;
import com.keyware.shandan.bianmu.service.DirectoryService;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.common.util.DateUtil;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.common.util.UUIDUtil;
import com.keyware.shandan.frame.properties.CustomProperties;
import com.keyware.shandan.system.entity.SysFile;
import com.keyware.shandan.system.entity.SysFileChunk;
import com.keyware.shandan.system.mapper.SysFileChunkMapper;
import com.keyware.shandan.system.mapper.SysFileMapper;
import com.keyware.shandan.system.service.SysFileService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
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
    @Transactional(rollbackFor = Exception.class)
    public SysFile uploadFileChunk(@NotNull MultipartFile uploadFile, SysFile fileInfo, @NotNull SysFileChunk chunk) throws Exception {
        SysFile file = getFileByMd5AndFirst(chunk.getFileMd5());
        if (file == null) {
            file = new SysFile();
            // 没有查询到说明是第一次上传，先保存文件信息
            BeanUtils.copyProperties(fileInfo, file);
            // 首次上传不保存entityId，在合并文件的时候更新
            file.setEntityId("");
            String originalFilename = file.getFileName();
            int index = originalFilename.lastIndexOf(".");
            file.setFileName(originalFilename.substring(0, index));
            file.setFileSuffix(originalFilename.substring(index));
            file.setFileSize(file.getFileSize() / 1024d / 1024d);
            file.setFileType(file.getFileSuffix().substring(1));
            file.setMD5(chunk.getFileMd5());
            file.setPath("");
            file.setIsChunk(true);
            file.setIsMerge(false);
            file.setIsFirst(true);
            save(file);
        }

        //保存分片文件到临时存储目录
        if (getFileChunkByMd5(file.getMD5(), chunk.getChunkMd5()) == null) {
            generateFileChunkPath(file, chunk);
            chunk.setChunkName(file.getFileName() + "_" + chunk.getChunkIndex());
            chunk.setFileId(file.getId());
            File transfer = new File(getFileChunkAbsolutePath(chunk));
            transfer.deleteOnExit();
            uploadFile.transferTo(transfer);

            fileChunkMapper.insert(chunk);
            file.setCurrentChunkIndex(chunk.getChunkIndex());
            updateById(file);

            // 判断是否是最后一片分片
            if (chunk.isLast()) {
                List<SysFileChunk> chunks = getFileChunksByFileMd5(chunk.getFileMd5());
                // 文件信息保存之后，该属性会丢失，需要重新赋值
                file.setFileFullName(fileInfo.getFileFullName());
                // 合并文件
                mergeFileChunk(file, chunks);
            }
        }
        return file;
    }

    /**
     * 根据路径创建目录数据
     *
     * @param path 路径
     */
    private String createDirectoryByPath(String path, String parentId) throws Exception {
        if (StringUtils.isNotBlank(path)) {
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 2);
            }

            String currentParentId = parentId;
            DirectoryVo existsDir = null;
            StringBuilder queryPath = new StringBuilder();
            for (String node : path.split("/")) {
                if (StringUtils.isNotBlank(node)) {
                    queryPath.append("/").append(node);
                    existsDir = directoryService.getByPath(queryPath.toString());
                    if (existsDir == null) {
                        existsDir = createDirectory(currentParentId, node);
                    } else if (existsDir.getId().equals(parentId)) {
                        continue;
                    }
                    currentParentId = existsDir.getId();
                }
            }
            return currentParentId;
        }
        return null;
    }

    /**
     * 创建单级目录
     *
     * @param parentId 父目录ID
     * @param name     目录名称
     * @throws Exception -
     */
    private DirectoryVo createDirectory(String parentId, String name) throws Exception {
        DirectoryVo dir = new DirectoryVo();
        dir.setDirectoryName(name);
        dir.setParentId(parentId);
        dir.setDirectoryType(DirectoryType.DIRECTORY);
        directoryService.updateOrSave(dir);
        return dir;
    }

    @Override
    public SysFile getFileByMd5AndFirst(@NotNull String md5) {
        QueryWrapper<SysFile> query = new QueryWrapper<>();
        query.eq("MD5", md5).eq("IS_FIRST", true);
        return getOne(query);
    }

    @Override
    public List<SysFile> getFilesByMd5(String md5) {
        QueryWrapper<SysFile> query = new QueryWrapper<>();
        query.eq("MD5", md5);
        return list(query);
    }

    @Override
    public SysFile getFileByMd5AndNameAndEntityId(String fileMd5, String name, String entityId) {
        QueryWrapper<SysFile> query = new QueryWrapper<>();
        query.eq("MD5", fileMd5).eq("FILE_NAME", name).eq("ENTITY_ID", entityId);
        return getOne(query);
    }

    @Override
    public SysFile getFileByMd5AndName(String fileMd5, String name) {
        QueryWrapper<SysFile> query = new QueryWrapper<>();
        query.eq("MD5", fileMd5).eq("FILE_NAME", name);
        return getOne(query);
    }

    @Override
    public SysFile getFileByMd5AndEntityId(String fileMd5, String entityId) {
        QueryWrapper<SysFile> query = new QueryWrapper<>();
        query.eq("MD5", fileMd5).eq("ENTITY_ID", entityId);
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
    public Result<Boolean> deleteById(String id) {
        SysFile file = getById(id);
        // 删除存储的文件
        deleteFolder(getFileAbsolutePath(file));
        // 删除数据的记录
        return super.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFile mergeFileChunk(@NotNull SysFile file, @NotNull List<SysFileChunk> chunks) throws IOException {
        generateFilePath(file);
        File targetFile = new File(getFileAbsolutePath(file));
        targetFile.deleteOnExit();
        targetFile.createNewFile();
        FileOutputStream outputStream = null;
        // 根据chunkIndex重新进行排序
        chunks.sort(Comparator.comparingInt(SysFileChunk::getChunkIndex));
        // 开始合并文件分片
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

    @Override
    public Boolean autoCreateDirAndUpdateFile(@NotNull String fileId, @NotNull String currDirId, @NotNull String filePath) throws Exception {
        if (StringUtils.isBlank(filePath)) {
            throw new Exception("文件全路径名称获取失败：null");
        }
        DirectoryVo root = directoryService.getById(currDirId);
        if (root == null) {
            throw new Exception("资源目录未找到，目录ID：" + currDirId);
        }
        SysFile file = getById(fileId);
        if (file == null) {
            throw new Exception("文件信息未找到，文件ID：" + fileId);
        }

        if (filePath.contains("/")) {
            String path = filePath;
            int index1 = filePath.lastIndexOf("/"), index2 = filePath.lastIndexOf(".");
            if (index1 < index2) {
                // 去掉文件名
                path = filePath.substring(0, index1);
                String name = filePath.substring(index1 + 1);
                file.setFileName(name.substring(0, name.lastIndexOf(".")));
                file.setFileSuffix(name.substring(name.lastIndexOf(".")));
                file.setFileType(name.substring(name.lastIndexOf(".") + 1));
            }
            if (path.endsWith("/")) {
                // 去掉末尾的斜线
                path = filePath.substring(0, filePath.lastIndexOf("/"));
            }
            String fullPath = root.getDirectoryPath() + "/" + path;
            String entityId = createDirectoryByPath(fullPath, currDirId);
            file.setId(null);
            file.setEntityId(entityId);
            file.setIsFirst(false);
            save(file);
            return true;
        }
        return false;
    }

    /**
     * 删除文件分片
     *
     * @param file 文件信息
     */
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

    /**
     * 删除文件分片的临时文件
     *
     * @param file 文件信息
     * @throws IOException -
     */
    private void deleteChunkFolder(@NotNull SysFile file) throws IOException {
        if (StringUtils.isNotBlank(file.getMD5())) {
            String path = generateFileChunkPath(file, new SysFileChunk());
            path = customProperties.getFileStorage().getTempPath() + "/" + path;
            deleteFolder(path);
        }
    }

    /**
     * 删除指定路径的目录
     *
     * @param path 文件路径
     */
    private void deleteFolder(String path) {
        File file = new File(path);
        if (file.exists()) {
            File[] children = file.listFiles();
            if (file.isDirectory() && children != null) {
                for (File f : children) {
                    deleteFolder(f.getAbsolutePath());
                }
            } else {
                file.delete();
            }
        }
    }
}
