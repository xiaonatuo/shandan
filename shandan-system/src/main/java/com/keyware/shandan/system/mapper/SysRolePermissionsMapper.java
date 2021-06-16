package com.keyware.shandan.system.mapper;

import com.keyware.shandan.common.mapper.IBaseMapper;
import com.keyware.shandan.system.entity.SysRolePermissions;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 系统角色与权限关系表 Mapper 接口
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-19
 */
@Mapper
public interface SysRolePermissionsMapper extends IBaseMapper<SysRolePermissions> {

}
