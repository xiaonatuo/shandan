package com.keyware.shandan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.system.entity.SysShortcutMenu;
import com.keyware.shandan.system.mapper.SysShortcutMenuMapper;
import com.keyware.shandan.system.service.SysShortcutMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户快捷菜单表 服务实现类
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
@Service
public class SysShortcutMenuServiceImpl extends BaseServiceImpl<SysShortcutMenuMapper, SysShortcutMenu, String> implements SysShortcutMenuService {

    @Autowired
    private SysShortcutMenuMapper sysShortcutMenuMapper;

    @Override
    public Result<Boolean> deleteById(String id) {
        List<String> idList = new ArrayList<>();
        idList.add(id);

        List<SysShortcutMenu> menuList = listAllChildByParentId(id);
        idList.addAll(menuList.stream().map(SysShortcutMenu::getShortcutMenuId).collect(Collectors.toList()));
        boolean ok = super.removeByIds(idList);
        return Result.of(ok, ok);
    }

    @Override
    public Result<List<SysShortcutMenu>> findByUserId(String userId) {
        return Result.of(sysShortcutMenuMapper.listByUserId(userId));
    }

    /**
     * 根据父菜单Id递归查询子菜单列表
     * @param parentId
     * @return
     */
    public List<SysShortcutMenu> listAllChildByParentId(String parentId){
        List<SysShortcutMenu> allChild = new ArrayList<>();

        SysShortcutMenu childCondition = new SysShortcutMenu();
        childCondition.setShortcutMenuParentId(parentId);
        List<SysShortcutMenu> child = super.list(new QueryWrapper<>(childCondition));
        if(child != null && child.size() != 0){
            allChild.addAll(child);
            child.forEach(menu -> {
                allChild.addAll(listAllChildByParentId(menu.getShortcutMenuParentId()));
            });
        }

        return allChild;
    }
}
