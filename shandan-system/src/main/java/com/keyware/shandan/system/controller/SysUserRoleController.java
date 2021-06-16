package com.keyware.shandan.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.system.entity.SysUserRole;
import com.keyware.shandan.system.service.SysRoleService;
import com.keyware.shandan.system.service.SysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sys/userRole/")
public class SysUserRoleController extends BaseController<SysUserRoleService, SysUserRole, String> {
    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysRoleService sysRoleService;



    @PostMapping("saveAllByUserId")
    public Result<Boolean> saveAllByUserId(SysUserRole userRole) {
        return sysUserRoleService.saveAllByUserId(userRole.getUserId(), userRole.getRoleIdList());
    }

    @PostMapping("save")
    public Result<SysUserRole> save(SysUserRole userRole) {
        //如果存在就删除，如果不存在就保存
        List<SysUserRole> list = sysUserRoleService.list(new QueryWrapper<>(userRole));
        if(list.size() > 0){
            sysUserRoleService.remove(new QueryWrapper<>(userRole));
        }else{
            sysUserRoleService.save(userRole);
        }
        return Result.of(userRole, true);
    }
}
