package com.keyware.shandan.bianmu.business.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.bianmu.business.entity.DirectoryMetadataVo;
import com.keyware.shandan.bianmu.business.entity.DirectoryVo;
import com.keyware.shandan.bianmu.business.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.business.service.DirectoryMetadataService;
import com.keyware.shandan.bianmu.business.service.DirectoryService;
import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    @GetMapping("/")
    public ModelAndView index(ModelAndView modelAndView) {
        modelAndView.setViewName("business/directory/directory");
        return modelAndView;
    }

    /**
     * 获取目录树
     *
     * @return
     */
    @GetMapping("/tree")
    public Result<DirectoryVo> tree() {
        return Result.of(directoryService.getById("ROOT"));
    }

    /**
     * 保存目录与元数据关系
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
        List<DirectoryMetadataVo> dmList = Arrays.stream(metadataIds.split(","))
                .map(metaId -> {
                    DirectoryMetadataVo vo = new DirectoryMetadataVo(directoryId, metaId);
                    // 避免重复关联，先删除，再保存
                    directoryMetadataService.remove(new QueryWrapper<>(vo));
                    return vo;
                })
                .collect(Collectors.toList());

        return Result.of(directoryMetadataService.saveBatch(dmList));
    }

    /**
     * 移除目录和元数据关系
     * @param directoryId
     * @param metadataId
     * @return
     */
    @PostMapping("/remove/metadata")
    public Result<Boolean> removeMetadata(String directoryId, String metadataId) {
        if (StringUtils.isBlank(directoryId) || StringUtils.isBlank(metadataId)) {
            return Result.of(null, false, "参数错误");
        }
        return Result.of(directoryMetadataService.remove(new QueryWrapper<>(new DirectoryMetadataVo(directoryId, metadataId))));
    }

    /**
     * 获取子级目录
     *
     * @param directory
     * @return
     */
    @GetMapping("/tree/children")
    public Result<Object> treeChildren(DirectoryVo directory) {
        List<JSONObject> result;
        directory.setParentId(StringUtils.isBlank(directory.getId()) ? "-" : directory.getId());
        directory.setId(null);
        List<DirectoryVo> directoryList = directoryService.list(new QueryWrapper<>(directory));
        if (directoryList.size() == 0) {
            List<MetadataBasicVo> metadataBasicVoList = directoryService.directoryMetadata(directory.getParentId());
            result = metadataBasicVoList.stream().map(vo -> {
                JSONObject json = treeJson(vo, "metadata", true);
                json.put("id", vo.getId());
                json.put("title", vo.getMetadataName());
                json.put("parentId", directory.getParentId());
                return json;
            }).collect(Collectors.toList());
        } else {
            result = directoryList.stream().map(vo -> {
                JSONObject json = treeJson(vo, "directory", false);
                json.put("id", vo.getId());
                json.put("title", vo.getDirectoryName());
                json.put("parentId", vo.getParentId());
                return json;
            }).collect(Collectors.toList());
        }
        return Result.of(result);
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
