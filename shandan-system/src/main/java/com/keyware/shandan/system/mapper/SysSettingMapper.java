package com.keyware.shandan.system.mapper;

import com.keyware.shandan.common.mapper.IBaseMapper;
import com.keyware.shandan.system.entity.SysSetting;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 系统设置表 Mapper 接口
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
@Mapper
public interface SysSettingMapper extends IBaseMapper<SysSetting> {

    @Update("${sql}")
    int clearSql(String sql);
}
