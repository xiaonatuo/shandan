package com.keyware.shandan.bianmu.service;

import com.keyware.shandan.bianmu.dto.DirCopyDTO;
import com.keyware.shandan.bianmu.entity.DirectoryMetadataVo;
import com.keyware.shandan.bianmu.entity.DirectoryVo;
import com.keyware.shandan.bianmu.enums.ReviewStatus;
import com.keyware.shandan.common.util.StreamUtil;
import com.keyware.shandan.system.entity.SysFile;
import com.keyware.shandan.system.service.SysFileService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ResourceCopyService {
    @Autowired
    private DirectoryMetadataService directoryMetadataService;
    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private SysFileService fileService;

    private DirectoryVo target;
    private List<DirectoryVo> copyDirs = new ArrayList<>();
    private List<DirectoryMetadataVo> copyMetas = new ArrayList<>();
    private List<SysFile> copyFiles = new ArrayList<>();
    private List<DirectoryVo> rootDirs = new ArrayList<>();
    private String endStr;

    /**
     * 初始化相关数据
     *
     * @param dto 数据
     */
    public void initData(@NonNull DirCopyDTO dto) {
        clear();
        target = directoryService.getById(dto.getTargetId());
        List<String> dirIds = new ArrayList<>();
        List<DirectoryMetadataVo> dmvList = new ArrayList<>();
        List<String> fileIds = new ArrayList<>();
        dto.getItems().forEach(item -> {
            switch (item.getType()) {
                case directory:
                    dirIds.add(item.getId());
                    break;
                case metadata:
                    dmvList.add(new DirectoryMetadataVo(item.getParentDirId(), item.getId()));
                    break;
                case file:
                    fileIds.add(item.getId());
            }
        });
        if(dirIds.size()>0){
            copyDirs.addAll(directoryService.listByIds(dirIds));
        }
        copyMetas.addAll(dmvList);
        if(fileIds.size() > 0){
            copyFiles.addAll(fileService.listByIds(fileIds));
        }

        rootDirs.addAll(StreamUtil.as(copyDirs).filter(dir -> !dirIds.contains(dir.getParentId())).toList());
    }

    private void copyDirectory() {
        rootDirs.forEach(dir -> {
            DirectoryVo newDir = newDirectory(dir);
            newDir.setParentId(target.getId());
            newDir.setId(dir.getId() + endStr);
            newDir.setDirectoryPath(target.getDirectoryPath() + "/" + newDir.getDirectoryName());
            try {
                directoryService.saveOrUpdate(newDir);

                List<DirectoryVo> newChilds = StreamUtil.as(copyDirs).filter(cd -> !cd.getDirectoryPath().equals(dir.getDirectoryPath()) && cd.getDirectoryPath().startsWith(dir.getDirectoryPath())).map(cd -> {
                    DirectoryVo newChild = new DirectoryVo();
                    BeanUtils.copyProperties(cd, newChild);
                    newChild.setId(cd.getId() + endStr);
                    newChild.setParentId(cd.getParentId() + endStr);
                    newChild.setReviewStatus(ReviewStatus.UN_SUBMIT);
                    newChild.setDirectoryPath(cd.getDirectoryPath().replace(dir.getDirectoryPath(), newDir.getDirectoryPath()));
                    return newChild;
                }).toList();
                directoryService.saveBatch(newChilds);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private DirectoryVo newDirectory(DirectoryVo source) {
        DirectoryVo target = new DirectoryVo();
        BeanUtils.copyProperties(source, target);
        target.setReviewStatus(ReviewStatus.UN_SUBMIT);
        return target;
    }

    private void copyMeta() {
        StreamUtil.as(copyMetas).forEach(dmv -> {
            if (isJoinRoot(dmv.getDirectoryId())) {
                dmv.setDirectoryId(dmv.getDirectoryId() + endStr);
            }else{
                dmv.setDirectoryId(target.getId());
            }
            directoryMetadataService.save(dmv);
        });
    }

    private void copyFiles() {
        StreamUtil.as(copyFiles).forEach(file -> {
            file.setId(null);
            if(isJoinRoot(file.getEntityId())){
                file.setEntityId(file.getEntityId() + endStr);
            }else{
                file.setEntityId(target.getId());
            }
            fileService.save(file);
        });
    }

    private boolean isJoinRoot(String dirId){
        return StreamUtil.as(copyDirs).map(DirectoryVo::getId).anyMatch(dirId::equals);
    }

    /**
     * 开始复制
     */
    public void copy() {
        copyDirectory();
        copyMeta();
        copyFiles();
    }

    private void clear() {
        target = null;
        copyDirs.clear();
        copyMetas.clear();
        copyFiles.clear();
        rootDirs.clear();
        endStr = getEndStr();
    }

    private String getEndStr(){
        return "_cp_"+System.currentTimeMillis() ;
    }
}
