package com.keyware.shandan.system.mapper;

import com.keyware.shandan.common.mapper.IBaseMapper;
import com.keyware.shandan.system.entity.SysMenu;
import com.keyware.shandan.system.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 系统角色表 Mapper 接口
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
@Mapper
public interface SysRoleMapper extends IBaseMapper<SysRole> {

    List<SysMenu> queryMenuList(String roleId);
}
