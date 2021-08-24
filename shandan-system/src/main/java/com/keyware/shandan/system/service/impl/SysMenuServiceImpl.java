package com.keyware.shandan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.system.utils.MenuUtil;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.system.entity.SysMenu;
import com.keyware.shandan.system.mapper.SysMenuMapper;
import com.keyware.shandan.system.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统菜单表 服务实现类
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
@Service
public class SysMenuServiceImpl extends BaseServiceImpl<SysMenuMapper, SysMenu, String> implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    private final static String[] systemMenus = {"ROOT", "BIANMU", "BROWSER", "CONTROL", "DESKTOP"};

    @Override
    public Page<SysMenu> page(Page<SysMenu> page, QueryWrapper<SysMenu> wrapper) {
        SysMenu entity = wrapper.getEntity();
        if (StringUtils.isNotBlank(entity.getMenuParentId())) {
            wrapper.setEntity(null);
            wrapper.eq("MENU_PARENT_ID", entity.getMenuParentId()).or().eq("MENU_ID", entity.getMenuParentId());
        }
        return super.page(page, wrapper);
    }

    /**
     * 递归删除菜单树
     *
     * @param id
     * @return
     */
    @Override
    public Result<Boolean> deleteById(String id) {
        if (Arrays.asList(systemMenus).contains(id)) {
            return Result.of(null, false, "系统默认菜单，不可删除");
        }
        List<SysMenu> menuList = listAllChildByParentId(id);
        List<String> idList = new ArrayList<>();
        idList.add(id);
        idList.addAll(menuList.stream().map(SysMenu::getMenuId).collect(Collectors.toList()));
        boolean ok = super.removeByIds(idList);
        return Result.of(ok, ok);
    }

    @Override
    public Result<List<SysMenu>> listByTier(SysMenu menu) {
        List<SysMenu> menuList = super.list(new QueryWrapper<>(menu));
        return Result.of(MenuUtil.getChildBySysMenuVo("", menuList));
    }

    /**
     * 根据父菜单Id递归查询子菜单列表
     *
     * @param parentId
     * @return
     */
    public List<SysMenu> listAllChildByParentId(String parentId) {
        List<SysMenu> allChild = new ArrayList<>();

        SysMenu childCondition = new SysMenu();
        childCondition.setMenuParentId(parentId);
        List<SysMenu> child = super.list(new QueryWrapper<>(childCondition));
        if (child != null && child.size() != 0) {
            allChild.addAll(child);
            child.forEach(menu -> {
                allChild.addAll(listAllChildByParentId(menu.getMenuId()));
            });
        }

        return allChild;
    }
}
