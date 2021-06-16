package com.keyware.shandan.system.service;

import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.IBaseService;
import com.keyware.shandan.system.entity.SysShortcutMenu;

import java.util.List;

/**
 * <p>
 * 用户快捷菜单表 服务类
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
public interface SysShortcutMenuService extends IBaseService<SysShortcutMenu, String> {

    Result<List<SysShortcutMenu>> findByUserId(String userId);
}
