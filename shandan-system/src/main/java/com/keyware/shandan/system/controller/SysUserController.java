package com.keyware.shandan.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.system.utils.SysSettingUtil;
import com.keyware.shandan.system.entity.SysUser;
import com.keyware.shandan.system.service.SysOrgService;
import com.keyware.shandan.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/sys/sysUser/")
public class SysUserController extends BaseController<SysUserService, SysUser, String> {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysOrgService sysOrgService;

    @Autowired
    private SecurityUtil securityUtil;

    @GetMapping("/")
    public ModelAndView user(){
        return new ModelAndView("sys/user/user","initPassword", SysSettingUtil.getSysSetting().getUserInitPassword());
    }

    @GetMapping("edit")
    public ModelAndView edit(ModelAndView modelAndView, String userId, String orgId, String orgName){
        SysUser user = new SysUser();
        user.setValid(true);
        user.setLimitMultiLogin(true);
        user.setOrgId(orgId);
        if(StringUtils.isNotBlank(userId)){
            user = sysUserService.getById(userId);
            if(user != null){
                user.setOrg(sysOrgService.getById(user.getOrgId()));
                if (user.getOrg() != null) {
                    orgName = user.getOrg().getOrgShortName();
                    if(StringUtils.isBlank(orgName)){
                        orgName = user.getOrg().getOrgName();
                    }
                }

            }
        }

        modelAndView.setViewName("sys/user/userEdit");
        modelAndView.addObject("user", user);
        modelAndView.addObject("orgName", orgName);
        modelAndView.addObject("message", user == null ? "未查询到该用户" : "");

        return modelAndView;
    }

    @PostMapping("resetPassword")
    public Result<SysUser> resetPassword(SysUser user){
        return sysUserService.resetPassword(user.getUserId());
    }

    @PostMapping("pageOnLine")
    public Result<Page<SysUser>> pageOnLine(SysUser sysUserVo){
        ArrayList<SysUser> sysUserVoList = new ArrayList<>();
        List<Object> allPrincipals = securityUtil.sessionRegistryGetAllPrincipals();
        for (Object allPrincipal : allPrincipals) {
            SysUser userVo = new SysUser();
            User user = (User) allPrincipal;
            userVo.setLoginName(user.getUsername());
            sysUserVoList.add(userVo);
        }
        Page<SysUser> page = new Page<>();
        page.setCurrent(1);//页码
        page.setSize(10000);//页面大小
        page.setRecords(sysUserVoList);//分页结果
        page.setTotal(sysUserVoList.size());//总记录数
        return Result.of(page);
    }

    @DeleteMapping("forced/{loginName}")
    public Result<String> forced( @PathVariable("loginName") String loginName) {
        securityUtil.sessionRegistryRemoveUserByLoginName(loginName);
        return Result.of("操作成功");
    }

}
