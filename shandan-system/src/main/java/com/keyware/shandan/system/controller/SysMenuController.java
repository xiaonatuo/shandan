package com.keyware.shandan.system.controller;

import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.system.entity.SysMenu;
import com.keyware.shandan.system.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/sys/menu/")
public class SysMenuController extends BaseController<SysMenuService, SysMenu, String> {

    @Autowired
    private SysMenuService sysMenuService;

    @GetMapping("/")
    public ModelAndView menu(){
        return new ModelAndView("sys/menu/menu");
    }


    @GetMapping("/edit")
    public ModelAndView edit(ModelAndView modelAndView, SysMenu menu){
        if(StringUtils.isNotBlank(menu.getMenuId())){
            modelAndView.addObject("menu", sysMenuService.getById(menu.getMenuId()));
        }else if(StringUtils.isNotBlank(menu.getMenuParentId())){
            menu.setParentMenu(sysMenuService.getById(menu.getMenuParentId()));
            modelAndView.addObject("menu", menu);
        }
        modelAndView.setViewName("sys/menu/menuEdit");
        return modelAndView;
    }

    /**
     * 分层级
     */
    @PostMapping("listByTier")
    public Result<List<SysMenu>> listByTier(SysMenu menu) {
        return sysMenuService.listByTier(menu);
    }

}
