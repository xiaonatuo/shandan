package com.keyware.shandan.system.service;

import com.keyware.shandan.system.entity.SysPermissions;
import com.keyware.shandan.common.service.IBaseService;

import java.util.List;

/**
 * <p>
 * 系统权限表 服务类
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-19
 */
public interface SysPermissionsService extends IBaseService<SysPermissions, String> {

    /**
     * @param permisId 权限ID
     * @return 获取机构权限配置
     */
    List<String> getOrgConfigs(String permisId);

    /**
     * @param permisId 权限ID
     * @return 获取机构权限配置
     */
    List<String> getDirConfigs(String permisId);

    /**
     * @param permisId 权限ID
     * @param orgId    机构ID
     * @return 是否成功
     */
    Boolean configOrg(String permisId, String orgId);

    /**
     * @param permisId 权限ID
     * @param dirId    目录ID
     * @return 是否成功
     */
    Boolean configDir(String permisId, String dirId);
}
