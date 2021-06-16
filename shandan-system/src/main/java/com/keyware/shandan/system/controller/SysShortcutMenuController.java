package com.keyware.shandan.system.controller;

import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.system.entity.SysShortcutMenu;
import com.keyware.shandan.system.service.SysShortcutMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys/sysShortcutMenu/")
public class SysShortcutMenuController extends BaseController<SysShortcutMenuService, SysShortcutMenu, String> {
    @Autowired
    private SysShortcutMenuService sysShortcutMenuService;
}
