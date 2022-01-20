package com.keyware.shandan.system.utils;

import com.keyware.shandan.system.entity.SysMenu;
import com.keyware.shandan.common.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单工具类
 */
public class MenuUtil {

    /**
     * 递归获取子节点
     * @param id 父节点id
     * @param allMenu 所有菜单列表
     * @return 每个根节点下，所有子菜单列表
     */
    public static List<SysMenu> getChildBySysMenuVo(String id, Collection<SysMenu> allMenu){
        //子菜单
        List<SysMenu> childList = new ArrayList<>();
        if(allMenu != null ){
            if (StringUtils.isBlank(id)) {
                // 如果父id为空，则遍历所有菜单，只要父id在集合中找不到，则为根节点
                childList.addAll(allMenu.stream().filter(menu -> allMenu.stream().noneMatch(m -> m.getMenuId().equals(menu.getMenuParentId()))).collect(Collectors.toList()));
            }else{
                childList.addAll(allMenu.stream().filter(menu -> id.equals(menu.getMenuParentId())).collect(Collectors.toList()));
            }
            return childList.stream().peek(menu -> menu.setChildren(getChildBySysMenuVo(menu.getMenuId(), allMenu))).sorted(Comparator.comparingInt(SysMenu::getSortWeight)).collect(Collectors.toList());
        }

        return childList;
    }
}
