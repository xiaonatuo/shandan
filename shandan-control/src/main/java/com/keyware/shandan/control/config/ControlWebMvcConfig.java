package com.keyware.shandan.control.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web资源配置
 *
 * @author GuoXin
 * @since 2021/7/27
 */
@Configuration
public class ControlWebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/index").setViewName("forward:/control/index");
        WebMvcConfigurer.super.addViewControllers(registry);
    }
}
