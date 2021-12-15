package com.keyware.shandan.desktop.config;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc配置
 *
 * @author GuoXin
 * @since 2021/7/23
 */
//@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


   /* *//**
     * 配置默认跳转首页
     *
     * @param registry -
     *//*
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/desktop/index");
        WebMvcConfigurer.super.addViewControllers(registry);
    }*/


}
