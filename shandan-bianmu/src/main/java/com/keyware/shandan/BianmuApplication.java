package com.keyware.shandan;

import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.system.utils.MenuUtil;
import com.keyware.shandan.system.utils.SysSettingUtil;
import com.keyware.shandan.common.util.*;
import com.keyware.shandan.system.entity.*;
import com.keyware.shandan.system.service.SysRoleService;
import com.keyware.shandan.system.service.SysSettingService;
import com.keyware.shandan.system.service.SysShortcutMenuService;
import com.keyware.shandan.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@EnableAsync//开启异步调用
@SpringBootApplication
public class BianmuApplication {

    public static void main(String[] args) {
        SpringApplication.run(BianmuApplication.class, args);
    }

    /**
     * 解决不能注入session注册表问题
     */
    @Bean
    SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}

@Slf4j
@Controller
@RequestMapping("/")
@Configuration
class IndexController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysSettingService sysSettingService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysShortcutMenuService sysShortcutMenuService;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${bianmu.captcha-enable}")
    private Boolean captchaEnable;


    /**
     * 端口
     */
    @Value("${server.port}")
    private String port;

    /**
     * 启动成功
     */
    @Bean
    public ApplicationRunner applicationRunner() {
        return applicationArguments -> {
            try {
                //系统启动时获取数据库数据，设置到公用静态集合sysSettingMap
                SysSetting setting = sysSettingService.list().get(0);
                SysSettingUtil.setSysSettingMap(setting);

                //获取本机内网IP
                log.info("启动成功：" + "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + contextPath);
            } catch (UnknownHostException e) {
                //输出到日志文件中
                log.error(ErrorUtil.errorInfoToString(e));
            }
        };
    }

    /**
     * 跳转登录页面
     */
    @GetMapping("loginPage")
    public ModelAndView login(){
        ModelAndView modelAndView = new ModelAndView("login");

        // 是否启用验证码
        modelAndView.addObject("captchaEnable", captchaEnable);

        //系统信息
        modelAndView.addObject("sys", SysSettingUtil.getSysSetting());

        //后端公钥
        String publicKey = RsaUtil.getPublicKey();
        modelAndView.addObject("publicKey", publicKey);

        return modelAndView;
    }

    /**
     * 跳转首页
     */
    @GetMapping("")
    public void index1(HttpServletResponse response){
        //内部重定向
        try {
            response.sendRedirect("/index");
        } catch (IOException e) {
            //输出到日志文件中
            log.error(ErrorUtil.errorInfoToString(e));
        }
    }
    @GetMapping("index")
    public ModelAndView index(){
        ModelAndView modelAndView = new ModelAndView("index");

        //系统信息
        modelAndView.addObject("sys", SysSettingUtil.getSysSetting());

        //登录用户
        SysUser user = sysUserService.findByLoginName(SecurityUtil.getLoginUser().getUsername()).getData();
        user.setPassword(null);//隐藏部分属性
        modelAndView.addObject( "loginUser", user);

        //登录用户系统菜单
        List<SysRole> roles = sysRoleService.getUserRoles(user.getUserId());
        List<SysMenu> menuList = new ArrayList<>();
        for(SysRole role : roles){
            menuList.addAll(role.getMenuList());
        }

        modelAndView.addObject("menuList", MenuUtil.getChildBySysMenuVo("", menuList));

        //登录用户快捷菜单
        List<SysShortcutMenu> shortcutMenuList = sysShortcutMenuService.findByUserId(user.getUserId()).getData();
        modelAndView.addObject("shortcutMenuList",shortcutMenuList);

        //后端公钥
        String publicKey = RsaUtil.getPublicKey();
        modelAndView.addObject("publicKey", publicKey);

        return modelAndView;
    }

    /**
     * 获取验证码图片和文本(验证码文本会保存在HttpSession中)
     */
    @RequestMapping("getVerifyCodeImage")
    public void getVerifyCodeImage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //设置页面不缓存
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.getOutputStream();
        String verifyCode = VerifyCodeImageUtil.generateTextCode(VerifyCodeImageUtil.TYPE_NUM_UPPER, 4, null);

        //将验证码放到HttpSession里面
        request.getSession().setAttribute("verifyCode", verifyCode);

        //设置输出的内容的类型为JPEG图像
        response.setContentType("image/jpeg");
        BufferedImage bufferedImage = VerifyCodeImageUtil.generateImageCode(verifyCode, 90, 30, 3, true, Color.WHITE, Color.BLACK, null);

        //写给浏览器
        ImageIO.write(bufferedImage, "JPEG", response.getOutputStream());
    }

    /**
     * 跳转实时系统硬件监控
     */
    @GetMapping("monitor")
    public ModelAndView monitor() {
        return new ModelAndView("monitor.html","port",port);
    }

    /**
     * 跳转实时日志
     */
    @GetMapping("logging")
    public ModelAndView logging() {
        return new ModelAndView("logging.html","port",port);
    }
}
