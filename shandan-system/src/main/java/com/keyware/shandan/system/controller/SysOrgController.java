package com.keyware.shandan.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.system.entity.SysOrg;
import com.keyware.shandan.system.service.SysOrgService;
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
                org.setOrgParentId(org.getId());
                org.setOrgParentName(org.getOrgName());
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
        QueryWrapper<SysOrg> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(parentId)) {
            wrapper.like("ORG_PATH", "%" + parentId + "%");
        }
        List<SysOrg> list = sysOrgService.list(wrapper);

        return Result.of(generateTree(list));
    }

    /**
     * 构建部门树
     * @param list
     * @return
     */
    private List<SysOrg> generateTree(List<SysOrg> list){
        Set<SysOrg> allOrgSet = new HashSet<>(list);

        list.stream()
                // 找到当前部门列表的根部门
                .filter(org -> list.stream().noneMatch(o -> o.getId().equals(org.getOrgParentId())))
                // 根据当前根部门查找所有父部门
                .forEach(org -> allOrgSet.addAll(sysOrgService.listByIds(Arrays.asList(org.getOrgPath().split("\\|")))));

        return allOrgSet.stream()
                // 找到根部门
                .filter(org -> allOrgSet.stream().noneMatch(o -> o.getId().equals(org.getOrgParentId())))
                // 递归子部门
                .peek(org-> findChildren(org, new ArrayList<>(allOrgSet)))
                // 转换List
                .collect(Collectors.toList());
    }

    /**
     * 递归子部门
     * @param org
     * @param list
     */
    private void findChildren(SysOrg org, List<SysOrg> list){
        org.setChildren(list.stream().filter(o -> org.getId().equals(o.getOrgParentId())).collect(Collectors.toList()));
        if(org.getChildren().size() > 0){
            org.getChildren().stream().peek(o -> findChildren(o, list)).collect(Collectors.toList());
        }
    }
}
