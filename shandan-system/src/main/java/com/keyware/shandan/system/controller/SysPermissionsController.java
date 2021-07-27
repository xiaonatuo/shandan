package com.keyware.shandan.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.common.enums.DataPermisScope;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.system.entity.SysPermissions;
import com.keyware.shandan.system.service.SysPermissionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.keyware.shandan.common.controller.BaseController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
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
    public ModelAndView index(ModelAndView modelAndView){

        modelAndView.setViewName("/sys/permissions/permissions");
        return modelAndView;
    }


    @GetMapping("/edit")
    public ModelAndView edit(ModelAndView modelAndView, String permisId){
        SysPermissions permis = new SysPermissions();
        if(StringUtils.isNotBlank(permisId)){
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


}
