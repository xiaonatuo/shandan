package com.keyware.shandan.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.enums.DataPermisScope;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.system.entity.SysPermissions;
import com.keyware.shandan.system.service.SysPermissionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.keyware.shandan.common.controller.BaseController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统权限表 前端控制器
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-19
 */
@RestController
@RequestMapping("/sys/permissions")
public class SysPermissionsController extends BaseController<SysPermissionsService, SysPermissions, String> {

    @Autowired
    private SysPermissionsService sysPermissionsService;

    @GetMapping("/")
    public ModelAndView index(ModelAndView modelAndView) {

        modelAndView.setViewName("/sys/permissions/permissions");
        return modelAndView;
    }


    @GetMapping("/edit")
    public ModelAndView edit(ModelAndView modelAndView, String permisId) {
        SysPermissions permis = new SysPermissions();
        if (StringUtils.isNotBlank(permisId)) {
            permis = sysPermissionsService.getById(permisId);
        }

        modelAndView.addObject("permisScopes", Arrays.stream(DataPermisScope.values()).map(value -> {
            JSONObject json = new JSONObject();
            json.put("name", value.name());
            json.put("remark", value.getRemark());
            return json;
        }).collect(Collectors.toList()));
        modelAndView.addObject("permis", permis);
        modelAndView.setViewName("/sys/permissions/permissionsEdit");
        return modelAndView;
    }

    /**
     * 获取指定类型配置项
     *
     * @param type org || dir
     * @return -
     */
    @GetMapping("/config/get/{type}/{id}")
    public Result<List<String>> getConfigs(@PathVariable String type, @PathVariable String id) {
        if("org".equalsIgnoreCase(type)){
            return Result.of(sysPermissionsService.getOrgConfigs(id));
        }
        if ("dir".equalsIgnoreCase(type)) {
            return Result.of(sysPermissionsService.getDirConfigs(id));
        }
        return Result.of(null, false, "配置类型错误");
    }

    /**
     * 配置机构权限
     *
     * @param permisId 权限ID
     * @param orgId    机构ID
     * @return -
     */
    @PostMapping("/config/org")
    public Result<Object> configOrg(String permisId, String orgIds) {
        if(StringUtils.isBlank(permisId)){
            return Result.of(null, false, "权限ID不能为空");
        }
        return Result.of(sysPermissionsService.configOrg(permisId, orgIds));
    }

    /**
     * 配置目录权限
     *
     * @param permisId 权限ID
     * @param dirId    目录ID
     * @return
     */
    @PostMapping("/config/dir")
    public Result<Object> configDir(String permisId, String dirIds) {
        if(StringUtils.isBlank(permisId)){
            return Result.of(null, false, "权限ID不能为空");
        }
        return Result.of(sysPermissionsService.configDir(permisId, dirIds));
    }
}
