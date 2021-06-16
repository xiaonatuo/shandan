package com.keyware.shandan.system.mapper;

import com.keyware.shandan.common.mapper.IBaseMapper;
import com.keyware.shandan.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 系统用户角色关系表 Mapper 接口
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
@Mapper
public interface SysUserRoleMapper extends IBaseMapper<SysUserRole> {


    /**
     * 根据userId查询用户角色关系集合
     * @param userId
     * @return
     */
    List<SysUserRole> listByUserId(String userId);
}
