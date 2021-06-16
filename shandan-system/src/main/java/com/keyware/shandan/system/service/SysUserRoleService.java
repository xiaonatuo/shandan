package com.keyware.shandan.system.service;

import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.IBaseService;
import com.keyware.shandan.system.entity.SysUserRole;

import java.util.List;

/**
 * <p>
 * 系统用户角色关系表 服务类
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
public interface SysUserRoleService extends IBaseService<SysUserRole, String> {
    Result<List<SysUserRole>> findByUserId(String userId);

    Result<Boolean> saveAllByUserId(String userId, String roleIdList);
}
