package com.keyware.shandan.system.service.impl;

import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.system.utils.SysSettingUtil;
import com.keyware.shandan.system.entity.SysSetting;
import com.keyware.shandan.system.mapper.SysSettingMapper;
import com.keyware.shandan.system.service.SysSettingService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统设置表 服务实现类
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
@Service
public class SysSettingServiceImpl extends BaseServiceImpl<SysSettingMapper, SysSetting, String> implements SysSettingService {

    @Override
    public Result<SysSetting> updateOrSave(SysSetting entity) {
        Result<SysSetting> result = super.updateOrSave(entity);
        SysSettingUtil.setSysSettingMap(entity);
        return result;
    }
}
