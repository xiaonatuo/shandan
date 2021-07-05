package com.keyware.shandan.browser.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.bianmu.entity.DirectoryVo;
import com.keyware.shandan.bianmu.service.DirectoryService;
import com.keyware.shandan.browser.entity.ConditionVo;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.RsaUtil;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.frame.properties.CustomProperties;
import com.keyware.shandan.system.entity.SysUser;
import com.keyware.shandan.system.service.SysUserService;
import com.keyware.shandan.system.utils.SysSettingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Autowired
    private CustomProperties customProperties;

    @Autowired
    private SysUserService sysUserService;

    /**
     * 首页路由
     *
     * @param modelAndView
     * @return
     */
    @GetMapping("/index")
    public ModelAndView index(ModelAndView modelAndView) {
        modelAndView.setViewName("index");
        modelAndView.addObject("appName", customProperties.getAppName());
        modelAndView.addObject("user", SecurityUtil.getLoginSysUser());
        //系统信息
        modelAndView.addObject("sys", SysSettingUtil.getSysSetting());
        //后端公钥
        String publicKey = RsaUtil.getPublicKey();
        modelAndView.addObject("publicKey", publicKey);
        //登录用户
        SysUser user = sysUserService.findByLoginName(SecurityUtil.getLoginUser().getUsername()).getData();
        user.setPassword(null);//隐藏部分属性
        modelAndView.addObject( "loginUser", user);
        return modelAndView;
    }

    /**
     * 查询目录树
     *
     * @param id
     * @return
     */
    @GetMapping("/browser/dir/tree")
    public Result<Object> dirTree(String id) {
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
