package com.keyware.shandan.desktop.controller;

import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.RsaUtil;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.desktop.entity.AppInfo;
import com.keyware.shandan.desktop.entity.DesktopSetting;
import com.keyware.shandan.desktop.services.AppInfoService;
import com.keyware.shandan.desktop.services.DesktopService;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.system.entity.SysUser;
import com.keyware.shandan.system.service.SysUserService;
import com.keyware.shandan.system.utils.SysSettingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 应用桌面前端控制器
 *
 * @author GuoXin
 * @since 2021/7/22
 */
@RestController
@RequestMapping("/")
public class DesktopController {

    @Autowired
    private DesktopService desktopService;

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private SysUserService sysUserService;


    /**
     * 首页
     *
     * @param modelAndView -
     * @return -
     */
    @GetMapping("/index")
    public ModelAndView index(ModelAndView modelAndView) {
        //跳转登录首页
        modelAndView.setViewName("index");
        //系统信息
        modelAndView.addObject("sys", SysSettingUtil.getCurrentSysSetting());
        //后端公钥
        String publicKey = RsaUtil.getPublicKey();
        modelAndView.addObject("publicKey", publicKey);
        //登录用户信息
        SysUser user = sysUserService.findByLoginName(SecurityUtil.getLoginUser().getUsername()).getData();
        user.setPassword(null);//隐藏部分属性
        modelAndView.addObject("loginUser", user);
        modelAndView.addObject("user", user);
        //桌面展示信息
        modelAndView.addObject("appList", appInfoService.list());
        return modelAndView;
    }

    /**
     * 管理员身份认证
     *
     * @param pwd 密码
     * @return
     */
    @PostMapping("desktop/auth")
    public Result<Object> auth(String pwd) {
        if (StringUtils.isBlank(pwd)) {
            return Result.of(false, false, "密码不能为空");
        }
        return Result.of(desktopService.auth(pwd));
    }

    /**
     * @return 应用列表
     */
    @GetMapping("desktop/apps")
    public List<AppInfo> getAppList() {
        return appInfoService.list();
    }

    /**
     * 查询应用信息
     *
     * @param id 应用ID
     * @return -
     */
    @GetMapping("desktop/app/{id}")
    public Result<AppInfo> getAppInfo(@PathVariable String id) {
        return Result.of(appInfoService.getById(id));
    }

    /**
     * 保存应用信息
     *
     * @param appInfo 应用信息
     * @return 保存结果
     */
    @PostMapping("desktop/app/save")
    public Result<Object> appSave(AppInfo appInfo) {
        return Result.of(appInfoService.saveOrUpdate(appInfo));
    }

    @DeleteMapping("desktop/app/{id}")
    public Result<Object> appDelete(@PathVariable String id) {
        return Result.of(appInfoService.removeById(id));
    }

    /**
     * @return 应用桌面设置
     */
    @GetMapping("desktop/setting")
    public Map<String, Object> getSetting() {
        return desktopService.getSetting();
    }

    /**
     * 保存应用桌面设置
     *
     * @param setting 设置
     * @return 保存结果
     */
    @PostMapping("desktop/setting/save")
    public Result<Object> settingSave(DesktopSetting.Setting setting) {
        return Result.of(desktopService.saveSetting(setting));
    }

    /**
     * 上传图标
     *
     * @param file
     * @return
     */
    @PostMapping("desktop/upload/icon")
    public Result<String> iconUpload(MultipartFile file) {

        try {
            return Result.of("images" + desktopService.uploadIcon(file));
        } catch (IOException e) {
            e.printStackTrace();
            return Result.of(null, false, "上传失败");
        }
    }
}
