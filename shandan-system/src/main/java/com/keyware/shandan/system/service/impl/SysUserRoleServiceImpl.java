package com.keyware.shandan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.system.entity.SysUserRole;
import com.keyware.shandan.system.mapper.SysUserRoleMapper;
import com.keyware.shandan.system.service.SysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统用户角色关系表 服务实现类
 * </p>
 *
 * @author
 * @since 2021-05-07
 */
@Service
public class SysUserRoleServiceImpl extends BaseServiceImpl<SysUserRoleMapper, SysUserRole, String> implements SysUserRoleService {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public Result<List<SysUserRole>> findByUserId(String userId) {
        List<SysUserRole> userRoles = sysUserRoleMapper.listByUserId(userId);
        return Result.of(userRoles);
    }

    @Override
    public Result<Boolean> saveAllByUserId(String userId, String roleIdList) {
        if (!StringUtils.isNotBlank(userId)) {
            return Result.of(false, false, "参数userId不能为null");
        }
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        super.remove(new QueryWrapper<>(userRole));

        List<SysUserRole> userRoleList = Arrays.stream(roleIdList.split(",")).map(roleId -> {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            return ur;
        }).collect(Collectors.toList());

        boolean ok = super.saveBatch(userRoleList);

        return Result.of(ok, ok, ok ? "保存成功" : "保存失败");
    }
}
