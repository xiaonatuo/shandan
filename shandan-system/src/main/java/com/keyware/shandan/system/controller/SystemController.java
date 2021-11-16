package com.keyware.shandan.system.controller;

import com.keyware.shandan.common.util.ErrorUtil;
import com.keyware.shandan.common.util.RsaUtil;
import com.keyware.shandan.common.util.VerifyCodeImageUtil;
import com.keyware.shandan.frame.properties.CustomProperties;
import com.keyware.shandan.system.utils.SysSettingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Administrator
 * @since 2021/6/17
 */
@Slf4j
@Controller
@RequestMapping("/")
public class SystemController {
    /**
     * 端口
     */
    @Value("${server.port:8080}")
    private String port;


    @Autowired
    private CustomProperties customProperties;

    /**
     * 跳转登录页面
     */
    @GetMapping("loginPage")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView("login");

        // 是否启用验证码
        modelAndView.addObject("captchaEnable", customProperties.getCaptchaEnable());

        // 是否启用记住我
        modelAndView.addObject("rememberMeEnable", customProperties.getRememberMeEnable());

        //系统信息
        modelAndView.addObject("sys", SysSettingUtil.getCurrentSysSetting());

        modelAndView.addObject("appName", customProperties.getAppName());

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
        return new ModelAndView("monitor.html", "port", port);
    }

    /**
     * 跳转实时日志
     */
    @GetMapping("logging")
    public ModelAndView logging() {
        return new ModelAndView("logging.html", "port", port);
    }
}
