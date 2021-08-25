package com.keyware.shandan.system.controller;

import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.MD5Util;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.system.entity.SysSetting;
import com.keyware.shandan.system.entity.SysUser;
import com.keyware.shandan.system.service.SysSettingService;
import com.keyware.shandan.system.service.SysUserService;
import com.keyware.shandan.system.utils.SysSettingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/sys/sysSetting/")
public class SysSettingController extends BaseController<SysSettingService, SysSetting, String> {
    @Autowired
    private SysSettingService sysSettingService;

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("setting")
    public ModelAndView setting() {
        return new ModelAndView("sys/setting/setting", "sys", SysSettingUtil.getCurrentSysSetting());
    }

    /**
     * 验证密码
     * @param password
     * @return
     */
    @PostMapping("pwd/verify")
    public Result<Object> pwdVerify(String password) {
        if (StringUtils.isBlank(password)) {
            return Result.of(null, false, "密码不能为空");
        }
        SysUser user = sysUserService.findByLoginName("sa").getData();
        if (user == null) {
            throw new RuntimeException("获取当前用户信息异常");
        }
        if (user.getPassword().equals(MD5Util.getMD5(password))) {
            return Result.of(true, true);
        }
        return Result.of(null, false, "验证失败，密码错误");
    }

    /**
     * 数据清除
     * @return
     */
    @PostMapping("data/clear")
    public Result<Object> dataClear(String type) {
        return Result.of(sysSettingService.dataClear(type));
    }
}
