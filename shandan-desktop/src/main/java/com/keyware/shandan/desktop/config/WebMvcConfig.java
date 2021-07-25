package com.keyware.shandan.desktop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc配置
 *
 * @author GuoXin
 * @since 2021/7/23
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ProjectProperties projectProperties;

    /**
     * 配置默认跳转首页
     *
     * @param registry -
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/desktop/index");
        WebMvcConfigurer.super.addViewControllers(registry);
    }

    /**
     * 文件上传资源虚拟映射
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = projectProperties.getUploadPath();
        registry.addResourceHandler("/images/**").addResourceLocations("file:///" + uploadPath + "/");
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }
}
