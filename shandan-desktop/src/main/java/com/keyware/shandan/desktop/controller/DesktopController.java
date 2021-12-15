package com.keyware.shandan.desktop.controller;

import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.RsaUtil;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.desktop.entity.AppInfo;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.system.entity.SysUser;
import com.keyware.shandan.system.entity.SysUserClient;
import com.keyware.shandan.system.service.SysUserClientService;
import com.keyware.shandan.system.service.SysUserService;
import com.keyware.shandan.system.utils.SysSettingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private SysUserClientService sysUserClientService;

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
        modelAndView.addObject("appList", dealEntiy(user));
        return modelAndView;
    }


    /**
     * 根据用户登录信息，查询桌面展示权限
     * @param user
     * @return
     */
    public List<AppInfo> dealEntiy(SysUser user){
        List<AppInfo> appInfoList = new ArrayList<>();
        if(!Objects.isNull(user)) {
            List<SysUserClient> userClients = sysUserClientService.findByUserId(user.getUserId()).getData();
            if (!userClients.isEmpty() && userClients != null) {
                userClients.forEach(userClient -> {
                    AppInfo appInfo = new AppInfo();
                    appInfo.setId(userClient.getClient().getId());
                    appInfo.setIcon(userClient.getClient().getIcon());
                    if(StringUtils.isBlank(userClient.getClient().getTitle())){
                        appInfo.setTitle("请在数据管控系统维护客户端名称");
                    }else{
                        appInfo.setTitle(userClient.getClient().getTitle());
                    }
                    appInfo.setUrl(userClient.getClient().getWebServerRedirectUri());
                    appInfo.setTarget(userClient.getClient().getTarget());
                    appInfoList.add(appInfo);
                });
            }
        }
        return  appInfoList;
    }

}
