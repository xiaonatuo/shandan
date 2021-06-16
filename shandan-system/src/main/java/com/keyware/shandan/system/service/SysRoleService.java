package com.keyware.shandan.system.service;

import com.keyware.shandan.common.service.IBaseService;
import com.keyware.shandan.system.entity.SysRole;

import java.util.List;

/**
 * <p>
 * 系统角色表 服务类
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
public interface SysRoleService extends IBaseService<SysRole, String> {
    List<SysRole> getUserRoles(String userId);
}
