package com.keyware.shandan.bianmu.controller;

import com.keyware.shandan.common.util.RsaUtil;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.system.entity.SysMenu;
import com.keyware.shandan.system.entity.SysRole;
import com.keyware.shandan.system.entity.SysShortcutMenu;
import com.keyware.shandan.system.entity.SysUser;
import com.keyware.shandan.system.service.SysRoleService;
import com.keyware.shandan.system.service.SysShortcutMenuService;
import com.keyware.shandan.system.service.SysUserService;
import com.keyware.shandan.system.utils.MenuUtil;
import com.keyware.shandan.system.utils.SysSettingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * BianmuIndexController
 * </p>
 *
 * @author Administrator
 * @since 2021/6/17
 */
@RestController
@RequestMapping("/")
public class BianmuIndexController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysShortcutMenuService sysShortcutMenuService;

    @GetMapping("index")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("index");

        //系统信息
        modelAndView.addObject("sys", SysSettingUtil.getCurrentSysSetting());

        //登录用户
        SysUser user = sysUserService.findByLoginName(SecurityUtil.getLoginUser().getUsername()).getData();
        user.setPassword(null);//隐藏部分属性
        modelAndView.addObject("loginUser", user);

        //登录用户系统菜单
        List<SysRole> roles = sysRoleService.getUserRoles(user.getUserId());
        Set<SysMenu> menuList = new HashSet<>();
        for (SysRole role : roles) {
            menuList.addAll(role.getMenuList());
        }

        modelAndView.addObject("menuList", MenuUtil.getChildBySysMenuVo("BIANMU", menuList));

        //登录用户快捷菜单
        List<SysShortcutMenu> shortcutMenuList = sysShortcutMenuService.findByUserId(user.getUserId()).getData();
        modelAndView.addObject("shortcutMenuList", shortcutMenuList);

        //后端公钥
        String publicKey = RsaUtil.getPublicKey();
        modelAndView.addObject("publicKey", publicKey);
        if (roles.isEmpty()) {
            modelAndView.addObject("ERROR", "没有配置角色");
        }
        return modelAndView;
    }
}
