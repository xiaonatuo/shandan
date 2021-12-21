package com.keyware.shandan.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.system.entity.SysShortcutMenu;
import com.keyware.shandan.system.entity.SysUser;
import com.keyware.shandan.system.service.SysShortcutMenuService;
import com.keyware.shandan.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 登录用户访问 业务
 */
@RestController
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysShortcutMenuService sysShortcutMenuService;

    @GetMapping("userinfo")
    public ModelAndView userinfo() {
        SysUser sysUserVo = sysUserService.findByLoginName(SecurityUtil.getLoginUser().getUsername()).getData();
        sysUserVo.setPassword(null);
        return new ModelAndView("user/userinfo", "user", sysUserVo);
    }

    @GetMapping("shortcMenu")
    public ModelAndView shortcMenu() {
        return new ModelAndView("user/shortcMenu");
    }

    /**
     * 修改密码
     */
    @PostMapping("updatePassword")
    public Result<Boolean> updatePassword(String oldPassword, String newPassword) {
        return sysUserService.updatePassword(oldPassword, newPassword);
    }

    /**
     * 修改部分用户属性
     */
    @PostMapping("updateUser")
    public Result<SysUser> updateUser(SysUser sysUserVo) {
        return sysUserService.updateUser(sysUserVo);
    }

    /**
     * 分层级
     */
    @PostMapping("shortcutMenuListByTier")
    public Result<List<SysShortcutMenu>> shortcutMenuListByTier() {
        SysUser sysUserVo = sysUserService.findByLoginName(SecurityUtil.getLoginUser().getUsername()).getData();
        return sysShortcutMenuService.findByUserId(sysUserVo.getUserId());
    }

    /**
     * 保存
     */
    @PostMapping("shortcutMenuSave")
    public Result<SysShortcutMenu> shortcutMenuSave(SysShortcutMenu shortcutMenu) throws Exception {
        SysUser user = sysUserService.findByLoginName(SecurityUtil.getLoginUser().getUsername()).getData();
        shortcutMenu.setUserId(user.getUserId());
        return sysShortcutMenuService.updateOrSave(shortcutMenu);
    }

    @DeleteMapping("shortcutMenuDelete/{id}")
    public Result<Boolean> shortcutMenuDelete(@PathVariable("id") String id) {
        SysUser sysUserVo = sysUserService.findByLoginName(SecurityUtil.getLoginUser().getUsername()).getData();
        SysShortcutMenu sysShortcutMenuVo = new SysShortcutMenu();
        sysShortcutMenuVo.setUserId(sysUserVo.getUserId());
        List<SysShortcutMenu> sysShortcutMenuVoList = sysShortcutMenuService.list(new QueryWrapper<>(sysShortcutMenuVo));

        //判断是不是自己的便捷菜单
        boolean flag = false;
        for (SysShortcutMenu shortcutMenu : sysShortcutMenuVoList) {
            if (shortcutMenu.getShortcutMenuId().equals(id)) {
                flag = true;
                break;
            }
        }

        if(flag){
            return sysShortcutMenuService.deleteById(id);
        }else{
            return Result.of(null,false,"请不要删除别人个性菜单！");
        }
    }
}
