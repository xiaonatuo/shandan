package com.keyware.shandan.system.mapper;

import com.keyware.shandan.common.mapper.IBaseMapper;
import com.keyware.shandan.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 系统菜单表 Mapper 接口
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
@Mapper
public interface SysMenuMapper extends IBaseMapper<SysMenu> {

    List<SysMenu> listChildren(String parentId);
}
