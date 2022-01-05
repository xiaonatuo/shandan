package com.keyware.shandan.bianmu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.bianmu.entity.DirectoryMetadataVo;
import com.keyware.shandan.bianmu.entity.DirectoryVo;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.enums.DirectoryType;
import com.keyware.shandan.bianmu.enums.ReviewStatus;
import com.keyware.shandan.bianmu.service.DirectoryMetadataService;
import com.keyware.shandan.bianmu.service.DirectoryService;
import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.system.entity.SysFile;
import com.keyware.shandan.system.service.SysFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 目录表 前端控制器
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-01
 */
@RestController
@RequestMapping("/business/directory")
public class DirectoryController extends BaseController<DirectoryService, DirectoryVo, String> {

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private DirectoryMetadataService directoryMetadataService;

    @Autowired
    private SysFileService sysFileService;

    @GetMapping("/")
    public ModelAndView index(ModelAndView modelAndView) {
        modelAndView.setViewName("business/directory/directory");
        return modelAndView;
    }

    /**
     * 保存目录与数据资源关系
     *
     * @param directoryId
     * @param metadataIds
     * @return
     */
    @PostMapping("/save/metadata")
    public Result<Object> saveMetadata(String directoryId, String metadataIds) {
        if (StringUtils.isBlank(directoryId) || StringUtils.isBlank(metadataIds)) {
            return Result.of(null, false, "参数错误");
        }

        DirectoryVo dir = directoryService.getById(directoryId);
        if (dir == null) {
            return Result.of(null, false, "目录不存在");
        }

        List<DirectoryMetadataVo> dmList = Arrays.stream(metadataIds.split(","))
                .map(metaId -> {
                    DirectoryMetadataVo vo = new DirectoryMetadataVo(directoryId, metaId);
                    // 避免重复关联，先删除，再保存
                    directoryMetadataService.remove(new QueryWrapper<>(vo));
                    return vo;
                })
                .collect(Collectors.toList());

        if (directoryMetadataService.saveBatch(dmList)) {
            if (dir.getReviewStatus() != ReviewStatus.UN_SUBMIT) {
                dir.setReviewStatus(ReviewStatus.UN_SUBMIT);
                directoryService.saveOrUpdate(dir);
            }
        }

        return Result.of(dir);
    }

    /**
     * 移除目录和数据资源关系
     *
     * @param directoryId
     * @param metadataId
     * @return
     */
    @PostMapping("/remove/metadata")
    public Result<Object> removeMetadata(String directoryId, String metadataId) {
        if (StringUtils.isBlank(directoryId) || StringUtils.isBlank(metadataId)) {
            return Result.of(null, false, "参数错误");
        }

        DirectoryVo dir = directoryService.getById(directoryId);
        if (dir == null) {
            return Result.of(null, false, "目录不存在");
        }

        if (directoryMetadataService.remove(new QueryWrapper<>(new DirectoryMetadataVo(directoryId, metadataId)))) {
            if (dir.getReviewStatus() != ReviewStatus.UN_SUBMIT) {
                dir.setReviewStatus(ReviewStatus.UN_SUBMIT);
                directoryService.saveOrUpdate(dir);
            }
        }

        return Result.of(dir);
    }

    @PostMapping("/remove/file")
    public Result<Object> removeFile(String fileId) {
        if (StringUtils.isBlankAny(fileId)) {
            return Result.of(null, false, "参数错误");
        }

        SysFile file = sysFileService.getById(fileId);
        if (file == null) {
            return Result.of(null, false, "文件不存在");
        }

        DirectoryVo dir = directoryService.getById(file.getEntityId());
        if (dir == null) {
            return Result.of(null, false, "目录不存在");
        }

        if (sysFileService.removeById(fileId)) {
            if (dir.getReviewStatus() != ReviewStatus.UN_SUBMIT){
                dir.setReviewStatus(ReviewStatus.UN_SUBMIT);
                directoryService.saveOrUpdate(dir);
            }
        }

        return Result.of(dir);
    }

    /**
     * 获取子级目录
     *
     * @param directory
     * @return
     */
    @GetMapping("/tree/children")
    public Result<Object> treeChildren(DirectoryVo directory) {
        directory.setParentId(StringUtils.isBlank(directory.getId()) ? "-" : directory.getId());
        directory.setId(null);

        DirectoryVo parentDir = directoryService.getById(directory.getParentId());
        List<JSONObject> result;
        List<DirectoryVo> directoryList = directoryService.list(new QueryWrapper<>(directory));
        if (parentDir != null && parentDir.getDirectoryType() == DirectoryType.METADATA) {
            // 查询数据资源
            List<MetadataBasicVo> metadataBasicVoList = directoryService.directoryMetadata(directory.getParentId());
            result = metadataBasicVoList.stream().filter(Objects::nonNull).map(vo -> {
                JSONObject json = treeJson(vo, "metadata", true);
                json.put("id", vo.getId());
                json.put("title", StringUtils.isBlank(vo.getMetadataComment()) ? vo.getMetadataName() : vo.getMetadataComment());
                json.put("parentId", directory.getParentId());
                json.put("iconClass", "dtree-icon-sort");
                return json;
            }).collect(Collectors.toList());

            // 查询文件
            SysFile q = new SysFile();
            q.setEntityId(parentDir.getId());
            List<SysFile> files = sysFileService.list(new QueryWrapper<>(q));
            result.addAll(files.stream().map(vo -> {
                JSONObject json = treeJson(vo, "metadata", true);
                json.put("id", vo.getId());
                json.put("title", vo.getFileName() + vo.getFileSuffix());
                json.put("parentId", directory.getParentId());
                json.put("iconClass", "dtree-icon-normal-file");
                return json;
            }).collect(Collectors.toList()));
        } else {
            result = directoryList.stream().map(vo -> {
                JSONObject json = treeJson(vo, "directory", false);
                json.put("id", vo.getId());
                json.put("title", vo.getDirectoryName());
                json.put("parentId", vo.getParentId());
                if (vo.getDirectoryType() == DirectoryType.METADATA) {
                    json.put("iconClass", "dtree-icon-fenzhijigou");
                } else {
                    json.put("iconClass", "dtree-icon-wenjianjiazhankai");
                }
                return json;
            }).collect(Collectors.toList());
        }
        return Result.of(result);
    }

    /**
     * 保存目录文件关系
     *
     * @param directoryId 目录ID
     * @param fileIds     文件ID
     * @return
     */
    @PostMapping("/save/file")
    public Result<Object> saveFile(String directoryId, String fileIds) {
        if (StringUtils.isBlankAny(directoryId, fileIds)) {
            return Result.of(null, false, "参数错误");
        }

        DirectoryVo dir = directoryService.getById(directoryId);
        if (dir == null) {
            return Result.of(null, false, "目录不存在");
        }

        List<SysFile> files = Arrays.stream(fileIds.split(",")).map(fid -> {
            SysFile file = new SysFile();
            file.setEntityId(directoryId);
            file.setId(fid);
            return file;
        }).collect(Collectors.toList());
        // 更新文件的entityId
        if (sysFileService.updateBatchById(files)) {
            if (dir.getReviewStatus() != ReviewStatus.UN_SUBMIT) {
                // 更新目录状态
                dir.setReviewStatus(ReviewStatus.UN_SUBMIT);
                directoryService.saveOrUpdate(dir);
            }
        }

        return Result.of(dir);
    }

    /**
     * 生成树的json对象
     *
     * @param vo
     * @param type
     * @param last
     * @return
     */
    private JSONObject treeJson(Object vo, String type, Boolean last) {
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put("spread", false);
        json.put("last", last);
        json.put("basicData", JSON.toJSON(vo));
        json.put("children", new ArrayList<>());
        return json;
    }
}
