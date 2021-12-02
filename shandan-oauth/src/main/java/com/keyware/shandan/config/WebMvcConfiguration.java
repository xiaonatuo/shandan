package com.keyware.shandan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index");
        WebMvcConfigurer.super.addViewControllers(registry);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                //需要跨域的接口，例如：/openApi/*
                .addMapping("/**")
                //允许调用的域，例如：http://172.16.12.156:8888
                .allowedOrigins("*")
                //接口调用方式，POST、GET等
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
