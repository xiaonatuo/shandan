package com.keyware.shandan.system.service;

import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.IBaseService;
import com.keyware.shandan.system.entity.SysMenu;

import java.util.List;

/**
 * <p>
 * 系统菜单表 服务类
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
public interface SysMenuService extends IBaseService<SysMenu, String> {

    Result<List<SysMenu>> listByTier(SysMenu menu);
}
