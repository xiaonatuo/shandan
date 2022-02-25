package com.keyware.shandan.bianmu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.bianmu.entity.DirectoryVo;
import com.keyware.shandan.bianmu.entity.MetadataAndFileVo;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.enums.ReviewStatus;
import com.keyware.shandan.bianmu.service.DirectoryMetadataService;
import com.keyware.shandan.bianmu.service.DirectoryService;
import com.keyware.shandan.bianmu.utils.DirectoryUtil;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.entity.TreeVo;
import com.keyware.shandan.common.enums.SystemTypes;
import com.keyware.shandan.common.util.StreamUtil;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.common.util.TreeUtil;
import com.keyware.shandan.system.entity.SysSetting;
import com.keyware.shandan.system.utils.SysSettingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资源目录共用控制器
 *
 * @author GuoXin
 * @since 2021/12/15
 */
@RestController
@RequestMapping("/business/directory")
public class DirectoryCommonController {

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private DirectoryMetadataService directoryMetadataService;


    /**
     * 获取树形结构的资源目录数据
     *
     * @param id     父级目录ID
     * @param reviewStatus 审核状态
     * @param browser      是否综合浏览
     * @param all          是否包含数据资源
     * @return 结果集
     */
    @GetMapping("/tree")
    public Result<List<TreeVo>> tree(String id, String reviewStatus, boolean browser, boolean all) {
        if (StringUtils.isBlank(id)) {
            id = "-";
        }

        QueryWrapper<DirectoryVo> wrapper = new QueryWrapper<>();
        // 目录ID存在，则查询该目录节点下的所有目录数据
        DirectoryVo parentDir = directoryService.getById(id);
        if (parentDir == null) {
            return Result.of(null, false, "目录未找到");
        }
        wrapper.likeRight("DIRECTORY_PATH", parentDir.getDirectoryPath() + "/");

        // 判断审核条件
        if (StringUtils.isNotBlank(reviewStatus)) {
            wrapper.eq("REVIEW_STATUS", reviewStatus);
        }

        List<DirectoryVo> directoryList = directoryService.list(wrapper);

        // 如果是综合浏览的请求，则需要判断每个目录的父级目录总是否有未通过审核的，如果有则过滤掉
        if (browser) {
            // 查询所有未审核通过的目录
            QueryWrapper<DirectoryVo> unPassQuery = new QueryWrapper<>();
            unPassQuery.in("REVIEW_STATUS", ReviewStatus.SUBMITTED, ReviewStatus.UN_SUBMIT, ReviewStatus.FAIL, ReviewStatus.REJECTED);
            List<DirectoryVo> unPassList = directoryService.list(unPassQuery);
            List<String> unPassIds = unPassList.stream().map(DirectoryVo::getId).collect(Collectors.toList());
            // 判断目录的父级是否存在于未审核通过的目录中，如果存在于，则过滤掉
            directoryList = directoryList.stream().filter(item -> !unPassIds.contains(item.getParentId())).collect(Collectors.toList());
        }
        // 转换为Dtree对象
        List<TreeVo> treeVoList = StreamUtil.as(directoryList).map(DirectoryUtil::dir2Tree).toList();

        // 包含数据资源
        if (all) {
            List<MetadataAndFileVo> metadataBasicVoList = directoryMetadataService.getMetadataAndFileListByDir(id);
            treeVoList.addAll(StreamUtil.as(metadataBasicVoList).map(DirectoryUtil::metaAndFile2Tree).toList());
        }

        final String finalId = id;
        // 构建树形结构
        treeVoList = StreamUtil.as(TreeUtil.buildDirTree(treeVoList, id))
                // 过滤根节点的垃圾数据
                .filter(dir -> (dir.getId().equals(finalId) || dir.getParentId().equals(finalId))).toList();

        List<TreeVo> rootList = new ArrayList<>();
        TreeVo root = DirectoryUtil.dir2Tree(parentDir);
        root.setChildren(treeVoList);
        rootList.add(root);
        return Result.of(rootList);
    }

    @GetMapping("/details/{id}")
    public ModelAndView details(ModelAndView mav, @PathVariable String id) {
        mav.setViewName("business/directory/details");

        DirectoryVo dir = directoryService.getById(id);
        mav.addObject("directory", dir);

        SysSetting bianmuSetting = SysSettingUtil.getSysSetting(SystemTypes.BIANMU.name());
        mav.addObject("bianmuAddress", bianmuSetting.getSysAddress());

        // 获取父级目录
        List<DirectoryVo> dirList = directoryService.parentLists(dir);
        dirList.addAll(directoryService.childrenLists(dir));
        dirList.add(dir);

        List<TreeVo> treeVoList = StreamUtil.as(dirList).map(DirectoryUtil::dir2Tree).toList();
        mav.addObject("treeData", TreeUtil.buildDirTree(treeVoList, id));
        return mav;
    }
}
