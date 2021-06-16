package com.keyware.shandan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.system.utils.SysSettingUtil;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.common.util.*;
import com.keyware.shandan.frame.annotation.DataPermissions;
import com.keyware.shandan.system.entity.SysShortcutMenu;
import com.keyware.shandan.system.entity.SysUser;
import com.keyware.shandan.system.entity.SysUserRole;
import com.keyware.shandan.system.mapper.SysUserMapper;
import com.keyware.shandan.system.service.SysShortcutMenuService;
import com.keyware.shandan.system.service.SysUserRoleService;
import com.keyware.shandan.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 *
 * @author
 * @since 2021-05-07
 */
@Service
public class SysUserServiceImpl extends BaseServiceImpl<SysUserMapper, SysUser, String> implements SysUserService {

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysShortcutMenuService sysShortcutMenuService;

    @Override
    public Result<Boolean> deleteById(String id) {
        // 删除权限关系
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(id);
        sysUserRoleService.remove(new QueryWrapper<>(userRole));


        //删除个性菜单
        SysShortcutMenu menu = new SysShortcutMenu();
        menu.setUserId(id);
        sysShortcutMenuService.remove(new QueryWrapper<>(menu));

        return super.deleteById(id);
    }

    @Override
    @DataPermissions
    public Page<SysUser> page(Page<SysUser> page, QueryWrapper<SysUser> wrapper) {
        Page<SysUser> userPage = super.page(page, wrapper);
        userPage.setRecords(userPage.getRecords().stream().map(sysUser -> {
            sysUser.setPassword(null);
            return sysUser;
        }).collect(Collectors.toList()));

        return userPage;
    }

    @Override
    public Result<SysUser> findByLoginName(String username) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("LOGIN_NAME", username);
        SysUser user = super.getOne(wrapper);
        if (user == null) {
            user = new SysUser();
        }
        return Result.of(user);
    }

    @Override
    public Result<SysUser> updateOrSave(SysUser entity) {
        if (!StringUtils.isNotBlank(entity.getUserId())) {
            SysUser userCondition = new SysUser();
            userCondition.setLoginName(entity.getLoginName());
            List list = super.list(new QueryWrapper<>(userCondition));
            if (list != null && list.size() > 0) {
                return Result.of(entity, false, "用户名已存在");
            }
            //需要设置初始密码
            entity.setPassword(MD5Util.getMD5(SysSettingUtil.getSysSetting().getUserInitPassword()));
        }
        return super.updateOrSave(entity);
    }

    @Override
    public Result<SysUser> resetPassword(String userId) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setPassword(MD5Util.getMD5(SysSettingUtil.getSysSetting().getUserInitPassword()));
        boolean flag = super.update(new QueryWrapper<>(user));
        if (flag) {
            user.setPassword(null);
            return Result.of(user);
        }
        return null;
    }

    @Override
    public Result<Boolean> updatePassword(String oldPassword, String newPassword) {
        SysUser user = findByLoginName(SecurityUtil.getLoginUser().getUsername()).getData();
        boolean ok = false;
        if (user.getPassword().equals(MD5Util.getMD5(oldPassword))) {
            user.setPassword(MD5Util.getMD5(newPassword));

            user.setLastChangePwdTime(new Date());

            ok = super.update(new QueryWrapper<>(user));

        }

        return Result.of(ok, ok, ok ? "修改成功" : "修改失败，你输入的原密码错误！");
    }

    @Override
    public Result<SysUser> updateUser(SysUser sysUser) {
        SysUser user = findByLoginName(SecurityUtil.getLoginUser().getUsername()).getData();
        if (user == null) {
            return Result.of(null, false, "修改失败，用户不存在");
        }
        CopyUtil.copyNotNullProperties(sysUser, user);
        boolean ok = super.update(new QueryWrapper<>(user));
        return Result.of(user, ok, ok ? "修改成功" : "修改失败");
    }
}
