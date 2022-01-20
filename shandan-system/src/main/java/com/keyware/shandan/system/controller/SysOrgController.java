package com.keyware.shandan.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.system.entity.SysOrg;
import com.keyware.shandan.system.service.SysOrgService;
import com.keyware.shandan.system.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.keyware.shandan.common.controller.BaseController;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 部门表 前端控制器
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-11
 */
@RestController
@RequestMapping("/sys/org")
public class SysOrgController extends BaseController<SysOrgService, SysOrg, String> {

    @Autowired
    private SysOrgService sysOrgService;

    @GetMapping("/")
    public ModelAndView menu() {
        return new ModelAndView("sys/org/org");
    }

    /**
     * 编辑页面
     * @param modelAndView
     * @param org
     * @return
     */
    @GetMapping("/edit")
    public ModelAndView edit(ModelAndView modelAndView, SysOrg org) {
        if (StringUtils.isNotBlank(org.getId())) {
            SysOrg vo = sysOrgService.getById(org.getId());
            if (vo != null) {
                org = vo;
            }
        } else if (StringUtils.isNotBlank(org.getOrgParentId())) {
            SysOrg parent = sysOrgService.getById(org.getOrgParentId());
            if (parent != null) {
                org.setOrgParentId(parent.getId());
                org.setOrgParentName(parent.getOrgName());
            }
        }
        modelAndView.addObject("org", org);
        modelAndView.setViewName("sys/org/orgEdit");
        return modelAndView;
    }

    /**
     * 获取部门树
     * @param parentId
     * @return
     */
    @GetMapping("/tree")
    public Result<List<SysOrg>> getOrgTree(String parentId) {
        return Result.of(sysOrgService.tree(StringUtil.stringReplace(parentId)));
    }

}
