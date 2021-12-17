package com.keyware.shandan.bianmu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.bianmu.entity.DirectoryVo;
import com.keyware.shandan.bianmu.service.DirectoryMetadataService;
import com.keyware.shandan.bianmu.service.DirectoryService;
import com.keyware.shandan.bianmu.utils.DirectoryUtil;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.entity.TreeVo;
import com.keyware.shandan.common.enums.SystemTypes;
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
     * 获取目录树
     *
     * @return
     */
    @GetMapping("/tree")
    public Result<List<TreeVo>> tree(String id) {
        DirectoryVo parent = null;
        QueryWrapper<DirectoryVo> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(id) && !"-".equals(id)) {
            parent = directoryService.getById(id);
            if (parent == null) {
                return Result.of(null, false, "目录未找到");

            }
            wrapper.likeRight("DIRECTORY_PATH", parent.getDirectoryPath());
        }

        List<DirectoryVo> directoryList = directoryService.list(wrapper);

        // 如果父目录存在，则从查询到的集合中将自己过滤掉
        if(parent != null){
            directoryList = directoryList.stream().filter(dir-> !id.equals(dir.getId())).collect(Collectors.toList());
        }
        return Result.of(TreeUtil.buildDirTree(directoryList.stream().map(DirectoryUtil::Dir2Tree).collect(Collectors.toList())));
    }

    @GetMapping("/details/{id}")
    public ModelAndView details(ModelAndView mav, @PathVariable String id) {
        mav.setViewName("business/directory/details");

        DirectoryVo dir = directoryService.getById(id);
        mav.addObject("directory", dir);

        SysSetting bianmuSetting = SysSettingUtil.getSysSetting(SystemTypes.BIANMU.name());
        mav.addObject("bianmuAddress", bianmuSetting.getSysAddress());

        String[] path = dir.getDirectoryPath().split("/");
        JSONArray dirArray = new JSONArray();
        for (int i = 1; i < path.length; i++) {
            JSONObject node = new JSONObject();
            node.put("id", i + "");
            node.put("title", path[i]);
            node.put("type", "directory");
            node.put("spread", true);
            node.put("parentId", (i - 1) + "");
            node.put("iconClass", "dtree-icon-wenjianjiazhankai");
            if(i == 1) node.put("parentId", "-");
            if(i == path.length-1) {
                node.put("id", dir.getId());
                node.put("iconClass", "dtree-icon-fenzhijigou");
                node.put("type", "metadata");
                node.put("last", true);
                node.put("basicData", JSON.toJSON(dir));
            }
            dirArray.add(node);
        }

        mav.addObject("treeData", buildTree(dirArray, "-"));
        return mav;
    }


    private List<Object> buildTree(JSONArray dirArray, String parentId) {
        JSONArray children = new JSONArray();
        children.addAll(dirArray.stream().filter(node -> ((JSONObject) node).getString("parentId").equals(parentId)).collect(Collectors.toList()));
        return children.stream().map(node -> {
            JSONObject nodeJson = (JSONObject)node;
            nodeJson.put("children", buildTree(dirArray, nodeJson.getString("id")));
            return nodeJson;
        }).collect(Collectors.toList());
    }
}
