package com.keyware.shandan.system.service.impl;

import com.keyware.shandan.common.entity.BaseEntity;
import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.system.entity.SysPermissions;
import com.keyware.shandan.system.mapper.SysPermissionsMapper;
import com.keyware.shandan.system.service.SysPermissionsService;
import org.springframework.stereotype.Service;

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
    @Override
    public List<SysPermissions> list() {
        return super.list().stream().sorted(Comparator.comparing(BaseEntity::getCreateTime)).collect(Collectors.toList());
    }
}
