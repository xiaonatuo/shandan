package com.keyware.shandan.system.service.impl;

import com.keyware.shandan.common.entity.BaseEntity;
import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.system.entity.SysPermissions;
import com.keyware.shandan.system.mapper.SysPermissionsMapper;
import com.keyware.shandan.system.service.SysPermissionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统权限表 服务实现类
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-19
 */
@Service
public class SysPermissionsServiceImpl extends BaseServiceImpl<SysPermissionsMapper, SysPermissions, String> implements SysPermissionsService {

    @Autowired
    private SysPermissionsMapper permissionsMapper;

    /**
     * @return 获取机构权限配置
     */
    @Override
    public List<SysPermissions> list() {
        return super.list().stream().sorted(Comparator.comparing(BaseEntity::getCreateTime)).collect(Collectors.toList());
    }

    /**
     * @param permisId 权限ID
     * @return 获取机构权限配置
     */
    @Override
    public List<String> getOrgConfigs(String permisId) {
        return permissionsMapper.selectOrgConfig(permisId);
    }

    /**
     * @param permisId 权限ID
     * @return 获取机构权限配置
     */
    @Override
    public List<String> getDirConfigs(String permisId) {
        return permissionsMapper.selectDirConfig(permisId);
    }

    /**
     * 配置机构权限，先删除，再保存
     *
     * @param permisId 权限ID
     * @param orgIds   机构ID
     * @return 是否成功
     */
    @Override
    public Boolean configOrg(String permisId, String orgIds) {
        permissionsMapper.removeOrgConfig(permisId);
        if (StringUtils.hasText(orgIds)) {
            String[] ids = orgIds.split(",");
            String userId = SecurityUtil.getLoginSysUser().getUserId();
            Arrays.stream(ids).forEach(orgId -> permissionsMapper.saveOrgConfig(permisId, orgId, userId));
        }
        return true;
    }

    /**
     * 配置目录权限，先删除，再保存
     *
     * @param permisId 权限ID
     * @param dirIds   目录ID
     * @return 是否成功
     */
    @Override
    public Boolean configDir(String permisId, String dirIds) {
        permissionsMapper.removeDirConfig(permisId);
        if (StringUtils.hasText(dirIds)) {
            String[] ids = dirIds.split(",");
            String userId = SecurityUtil.getLoginSysUser().getUserId();
            Arrays.stream(ids).forEach(dirId -> permissionsMapper.saveDirConfig(permisId, dirId, userId));
        }
        return true;
    }
}
