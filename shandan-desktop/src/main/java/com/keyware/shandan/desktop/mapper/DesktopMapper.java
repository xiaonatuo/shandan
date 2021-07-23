package com.keyware.shandan.desktop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.keyware.shandan.desktop.entity.DesktopSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * NoBeanMapper
 *
 * @author Administrator
 * @since 2021/7/22
 */
@Mapper
public interface DesktopMapper extends BaseMapper<DesktopSetting.Setting> {

    /**
     * 管理员认证
     *
     * @param pwd 密码
     * @return 是否认证成功
     */
    @Select("select count(1) from SYS_USER where LOGIN_NAME = 'admin' and PASSWORD = #{pwd}")
    int adminAuth(String pwd);

    /**
     * 查询应用桌面设置
     *
     * @return 应用桌面设置
     */
    @Select("select * from SYS_DESKTOP_SETTING")
    List<DesktopSetting.Setting> getSettings();

}
