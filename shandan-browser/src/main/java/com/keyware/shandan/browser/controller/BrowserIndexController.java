package com.keyware.shandan.browser.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.bianmu.entity.DirectoryVo;
import com.keyware.shandan.bianmu.service.DirectoryService;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 首页 前端控制器
 * </p>
 *
 * @author GuoXin
 * @since 2021/6/17
 */
@RestController
@RequestMapping("/")
public class BrowserIndexController {

    @Autowired
    private DirectoryService directoryService;

    @Value("${bianmu.app-name}")
    private String appName;

    @GetMapping("/index")
    public ModelAndView index(ModelAndView modelAndView){
        modelAndView.setViewName("index");
        modelAndView.addObject("appName", appName);
        modelAndView.addObject("user", SecurityUtil.getLoginSysUser());
        return modelAndView;
    }

    @GetMapping("/browser/dir/tree")
    public Result<Object> dirTree(String id){
        DirectoryVo dir = new DirectoryVo();
        dir.setParentId(id);
        List<DirectoryVo> dirList = directoryService.list(new QueryWrapper<>(dir));
        return Result.of(dirList.stream().map(item -> treeJson(item, false)).collect(Collectors.toList()));
    }


    /**
     * 生成树的json对象
     *
     * @param vo
     * @param last
     * @return
     */
    private JSONObject treeJson(DirectoryVo vo, Boolean last) {
        JSONObject json = new JSONObject();
        json.put("id", vo.getId());
        json.put("title", vo.getDirectoryName());
        json.put("parentId", vo.getParentId());
        json.put("spread", false);
        json.put("last", last);
        json.put("basicData", JSON.toJSON(vo));
        json.put("children", new ArrayList<>());
        return json;
    }
}
