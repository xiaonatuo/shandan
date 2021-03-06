package com.keyware.shandan;

import com.keyware.shandan.common.enums.SystemTypes;
import com.keyware.shandan.common.util.RsaUtil;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.frame.properties.CustomProperties;
import com.keyware.shandan.system.entity.SysMenu;
import com.keyware.shandan.system.entity.SysRole;
import com.keyware.shandan.system.entity.SysSetting;
import com.keyware.shandan.system.entity.SysUser;
import com.keyware.shandan.system.service.SysRoleService;
import com.keyware.shandan.system.service.SysUserService;
import com.keyware.shandan.system.utils.MenuUtil;
import com.keyware.shandan.system.utils.SysSettingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

/**
 * 数据管控子系统启动类
 *
 * @author GuoXin
 * @since 2021/7/27
 */
@SpringBootApplication
@EnableElasticsearchRepositories
public class ControlApplication {
    public static void main(String[] args) {
        SpringApplication.run(ControlApplication.class, args);
    }

    /**
     * 解决不能注入session注册表问题
     */
    @Bean
    SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }


    /**
     * 首页前端控制器
     */
    @Controller
    @RequestMapping
    public class IndexController{

        @Autowired
        private CustomProperties customProperties;

        @Autowired
        private SysRoleService sysRoleService;

        @Autowired
        private SysUserService sysUserService;

        @GetMapping("/index")
        public ModelAndView index(ModelAndView view){
            view.setViewName("index");

            //登录用户
            SysUser user = sysUserService.findByLoginName(SecurityUtil.getLoginUser().getUsername()).getData();
            user.setPassword(null);//隐藏部分属性
            view.addObject("loginUser", user);

            Map<String, String> setting = new HashMap<>();
            setting.put("sysApiEncrypt", String.valueOf(true));
            //系统信息
            view.addObject("sys", setting);
            //后端公钥
            String publicKey = RsaUtil.getPublicKey();
            view.addObject("publicKey", publicKey);

            // 数据分类编目服务地址
            SysSetting bianmuSetting = SysSettingUtil.getSysSetting(SystemTypes.BIANMU.name());
            view.addObject("bianmuAddress", bianmuSetting.getSysAddress());

            //登录用户系统菜单
            List<SysRole> roles = sysRoleService.getUserRoles(user.getUserId());
            Set<SysMenu> menuList = new HashSet<>();
            for (SysRole role : roles) {
                menuList.addAll(role.getMenuList());
            }

            view.addObject("menuList", MenuUtil.getChildBySysMenuVo("CONTROL", menuList));

            view.addObject("appName", SysSettingUtil.getCurrentSysSetting().getSysName());
            view.addObject("user", SecurityUtil.getLoginSysUser());
            return view;
        }
    }
}
