package com.keyware.shandan.system.service;

import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.IBaseService;
import com.keyware.shandan.system.entity.SysUser;

/**
 * <p>
 * 系统用户表 服务类
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
public interface SysUserService extends IBaseService<SysUser, String> {

    Result<SysUser> findByLoginName(String username);

    Result<SysUser> resetPassword(String userId);

    Result<Boolean> updatePassword(String oldPassword, String newPassword);

    Result<SysUser> updateUser(SysUser sysUser);

}
