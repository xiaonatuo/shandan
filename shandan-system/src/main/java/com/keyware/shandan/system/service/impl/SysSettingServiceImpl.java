package com.keyware.shandan.system.service.impl;

import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.frame.properties.CustomProperties;
import com.keyware.shandan.system.service.SysFileService;
import com.keyware.shandan.system.utils.SysSettingUtil;
import com.keyware.shandan.system.entity.SysSetting;
import com.keyware.shandan.system.mapper.SysSettingMapper;
import com.keyware.shandan.system.service.SysSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

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

    @Autowired
    private SysSettingMapper mapper;

    @Autowired
    private SysFileService fileService;

    @Override
    public Result<SysSetting> updateOrSave(SysSetting entity) {
        Result<SysSetting> result = super.updateOrSave(entity);
        SysSettingUtil.setSysSettingMap(entity);
        return result;
    }

    //数据清理
    @Override
    public Boolean dataClear(String type) {
        if (ClearType.ALL.equals(type)) {
            clearAllData();
        } else if (ClearType.DIR.equals(type)) {
            clearRelationData();
        } else {
            throw new RuntimeException("数据清理类型参数错误");
        }
        return true;
    }

    private void clearRelationData() {
        // 清理文件数据
        clearFiles();
        // 清理目录关系数据
        mapper.clearSql("truncate table BIANMU.\"B_DIRECTORY_METADATA\"");
        // 清理审核记录表
        mapper.clearSql("truncate table BIANMU.\"B_REVIEW_RECORD\"");
        // 清理目录数据
        mapper.clearSql("truncate table BIANMU.\"B_DIRECTORY\"");
        // 清理未读消息通知表
        mapper.clearSql("truncate table BIANMU.\"SYS_NOTIFICATION_UNREAD\"");
        // 清理消息通知表
        mapper.clearSql("truncate table BIANMU.\"SYS_NOTIFICATION\"");
    }

    private void clearAllData() {
        clearRelationData();
        // 清理元数据详情表
        mapper.clearSql("truncate table BIANMU.\"B_METADATA_DETAILS\"");
        // 清理元数据表
        mapper.clearSql("truncate table BIANMU.\"B_METADATA_BASIC\"");
        // 清理数据源表
        mapper.clearSql("truncate table BIANMU.\"B_DATA_SOURCE\"");
        // 清理角色权限表
        mapper.clearSql("truncate table BIANMU.\"SYS_ROLE_PERMISSIONS\"");
        // 清理自定义权限表
        mapper.clearSql("truncate table BIANMU.\"SYS_PERMIS_DIR\"");
        mapper.clearSql("truncate table BIANMU.\"SYS_PERMIS_ORG\"");
        mapper.clearSql("delete from BIANMU.\"SYS_PERMISSIONS\" where PERMIS_ID not in ('PERMIS_ONLY_ORG','PERMIS_ONLY_SELF','PERMIS_ORG_LEADER', 'PERMIS_ROOT')");
        // 清理用户
        mapper.clearSql("delete from BIANMU.\"SYS_USER\" where USER_ID not in('sa', 'admin')");
        // 清理组织结构
        mapper.clearSql("delete from BIANMU.\"SYS_ORG\" where ID != 'ROOT'");
        // 清理日志数据
        mapper.clearSql("truncate table BIANMU.\"SYS_OPERATE_LOG\"");
    }

    private void clearFiles() {
        mapper.clearSql("truncate table SYS_FILE");
        fileService.clearFiles();
    }

    private static class ClearType {
        public final static String ALL = "clear-all";
        public final static String DIR = "clear-dir";
    }
}
