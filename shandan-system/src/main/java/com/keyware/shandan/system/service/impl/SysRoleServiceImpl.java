package com.keyware.shandan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.frame.config.security.MyFilterInvocationSecurityMetadataSource;
import com.keyware.shandan.system.entity.SysRole;
import com.keyware.shandan.system.entity.SysUserRole;
import com.keyware.shandan.system.mapper.SysRoleMapper;
import com.keyware.shandan.system.service.SysRoleService;
import com.keyware.shandan.system.service.SysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统角色表 服务实现类
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
@Service
public class SysRoleServiceImpl extends BaseServiceImpl<SysRoleMapper, SysRole, String> implements SysRoleService {

    private final static String[] SYS_ROLE = {"ROLE_USER", "ROLE_ADMIN", "ROLE_SA", "3fb1c570496d4c09ab99b8d31b06ccc", "3fb1c570496d4c09ab99b8d31b06xxx", "3fb1c570496d4c09ab99b8d31b06zzz"};

    @Autowired
    private MyFilterInvocationSecurityMetadataSource myFilterInvocationSecurityMetadataSource;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Override
    public Result<SysRole> updateOrSave(SysRole entity) throws Exception {
        Result<SysRole> result = super.updateOrSave(entity);
        myFilterInvocationSecurityMetadataSource.setRequestMap(super.list());
        return result;
    }

    @Override
    public Result<Boolean> deleteById(String id) {
        if (Arrays.asList(SYS_ROLE).contains(id)) {
            return Result.of(null, false, "系统默认角色，不可删除");
        }
        SysUserRole userRole = new SysUserRole();
        userRole.setRoleId(id);
        sysUserRoleService.remove(new QueryWrapper<>(userRole));

        Result<Boolean> result = super.deleteById(id);

        myFilterInvocationSecurityMetadataSource.setRequestMap(super.list());
        return result;
    }

    /**
     * 获取用户角色列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<SysRole> getUserRoles(String userId) {
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setUserId(userId);
        List<SysUserRole> userRoleList = sysUserRoleService.list(new QueryWrapper<>(sysUserRole));

        if (userRoleList.size() == 0) {
            return new ArrayList<>();
        }
        return listByIds(userRoleList.stream().map(SysUserRole::getRoleId).collect(Collectors.toList()));
    }
}
