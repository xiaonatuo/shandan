package com.keyware.shandan.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.system.entity.SysRole;
import com.keyware.shandan.system.entity.SysRoleMenu;
import com.keyware.shandan.system.entity.SysRolePermissions;
import com.keyware.shandan.system.entity.SysUserRole;
import com.keyware.shandan.system.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sys/role/")
public class SysRoleController extends BaseController<SysRoleService, SysRole, String> {
    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Autowired
    private SysPermissionsService sysPermissionsService;

    @Autowired
    private SysRolePermissionsService sysRolePermissionsService;

    @GetMapping("/")
    public ModelAndView index(ModelAndView modelAndView) {
        modelAndView.setViewName("sys/role/role");
        modelAndView.addObject("permisList", sysPermissionsService.list());
        return modelAndView;
    }


    @GetMapping("/layer")
    public ModelAndView roleLayer(ModelAndView modelAndView, String userId) {
        List<SysUserRole> userRoleList = sysUserRoleService.findByUserId(userId).getData();
        List<String> userRoleIds = userRoleList.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        modelAndView.addObject("userId", userId);
        modelAndView.addObject("userRoleIds", userRoleIds);
        modelAndView.addObject("roleList", sysRoleService.list());
        modelAndView.setViewName("sys/role/roleLayer");
        return modelAndView;
    }

    /**
     * 查询角色菜单关系数据
     *
     * @param roleId
     * @return
     */
    @GetMapping("/menus/{roleId}")
    public Result<List<String>> getMenus(@PathVariable String roleId) {
        if (StringUtils.isBlank(roleId)) {
            return Result.of(null, false, "roleId 不能为空");
        }

        List<SysRoleMenu> roleMenus = sysRoleMenuService.list(new QueryWrapper<>(new SysRoleMenu(roleId, null)));
        return Result.of(roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList()));
    }

    /**
     * 查询角色权限关系数据
     *
     * @param permisId
     * @return
     */
    @GetMapping("/permis/{permisId}")
    public Result<List<SysRolePermissions>> getPermis(@PathVariable String permisId) {
        if (StringUtils.isBlank(permisId)) {
            return Result.of(null, false, "permisId 不能为空");
        }

        List<SysRolePermissions> roleMenus = sysRolePermissionsService.list(new QueryWrapper<>(new SysRolePermissions(permisId, null)));
        return Result.of(roleMenus);
    }

    /**
     * 保存角色菜单关系
     *
     * @param roleId
     * @param menuIds
     * @return
     */
    @PostMapping("/save/menus")
    public Result<Boolean> saveRoleMenu(@RequestBody String roleId, @RequestBody String menuIds) {
        if (StringUtils.isBlank(roleId)) {
            return Result.of(false, false, "roleId 不能为空");
        }
        //先删除
        SysRoleMenu roleMenu = new SysRoleMenu();
        roleMenu.setRoleId(roleId);
        sysRoleMenuService.remove(new QueryWrapper<>(roleMenu));

        //再保存, 如果菜单id为空就等于全部删除
        if (StringUtils.isNotBlank(menuIds)) {
            sysRoleMenuService.saveBatch(Arrays.stream(menuIds.split(",")).filter(id -> !"ROOT".equalsIgnoreCase(id)).map(menuId -> new SysRoleMenu(roleId, menuId)).collect(Collectors.toList()));
        }

        return Result.of(true, true);
    }


    /**
     * 保存角色权限关系
     *
     * @param rolePermis
     * @return
     */
    @PostMapping("/save/permis")
    public Result<Boolean> saveRolePermis(SysRolePermissions rolePermis) {
        if (StringUtils.isBlank(rolePermis.getRoleId())) {
            return Result.of(false, false, "roleId 不能为空");
        }
        QueryWrapper<SysRolePermissions> wrapper = new QueryWrapper<>(rolePermis);
        List<SysRolePermissions> list = sysRolePermissionsService.list(wrapper);
        if (list != null && list.size() > 0) {
            sysRolePermissionsService.remove(wrapper);
        } else {
            sysRolePermissionsService.save(rolePermis);
        }

        return Result.of(true, true);
    }
}

