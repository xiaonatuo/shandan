package com.keyware.shandan.desktop.controller;

import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.RsaUtil;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.desktop.entity.AppInfo;
import com.keyware.shandan.desktop.entity.DesktopSetting;
import com.keyware.shandan.desktop.services.DesktopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用桌面前端控制器
 *
 * @author GuoXin
 * @since 2021/7/22
 */
@RestController
@RequestMapping("/desktop")
public class DesktopController {

    @Autowired
    private DesktopService desktopService;

    /**
     * 首页
     *
     * @param modelAndView -
     * @return -
     */
    @GetMapping("/index")
    public ModelAndView index(ModelAndView modelAndView) {
        modelAndView.setViewName("index");

        Map<String, String> setting = new HashMap<>();
        setting.put("sysApiEncrypt", "Y");
        //系统信息
        modelAndView.addObject("sys", setting);
        //后端公钥
        String publicKey = RsaUtil.getPublicKey();
        modelAndView.addObject("publicKey", publicKey);
        return modelAndView;
    }

    /**
     * 管理员身份认证
     *
     * @param pwd 密码
     * @return
     */
    @PostMapping("/auth")
    public Result<Object> auth(String pwd) {
        if (StringUtils.isBlank(pwd)) {
            return Result.of(false, false, "密码不能为空");
        }
        return Result.of(desktopService.auth(pwd));
    }

    /**
     * @return 应用列表
     */
    @GetMapping("/apps")
    public List<AppInfo> getAppList() {
        return desktopService.appList();
    }

    /**
     * 保存应用信息
     *
     * @param appInfo 应用信息
     * @return 保存结果
     */
    @PostMapping("/app/save")
    public Result<Object> appSave(AppInfo appInfo) {
        return Result.of(desktopService.saveApp(appInfo));
    }

    /**
     * @return 应用桌面设置
     */
    @GetMapping("/setting")
    public Map<String, Object> getSetting() {
        return desktopService.getSetting();
    }

    /**
     * 保存应用桌面设置
     *
     * @param setting 设置
     * @return 保存结果
     */
    @PostMapping("/setting/save")
    public Result<Object> settingSave(DesktopSetting.Setting setting) {
        return Result.of(desktopService.saveSetting(setting));
    }

}
