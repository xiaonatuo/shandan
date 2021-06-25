package com.keyware.shandan.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.keyware.shandan.common.mapper.IBaseMapper;
import com.keyware.shandan.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 系统用户表 Mapper 接口
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
@Mapper
public interface SysUserMapper extends IBaseMapper<SysUser> {
    @Override
    @Select("select * from SYS_USER ${ew.customSqlSegment}")
    @ResultMap("ResultMapWithOrg")
    <E extends IPage<SysUser>> E selectPage(E page, @Param(Constants.WRAPPER) Wrapper<SysUser> queryWrapper);
}
