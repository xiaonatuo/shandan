package com.keyware.shandan.frame.config.cors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${bianmu.file-upload-resource}")
    private String resourceHandler;

    @Value("${bianmu.file-storage.path}")
    private String resourceLocations;

    /**
     * 文件上传资源虚拟映射
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(resourceHandler).addResourceLocations("file:///" + resourceLocations);
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }

    /**
     * cors安全跨域配置
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                //需要跨域的接口，例如：/openApi/*
                .addMapping("/openApi/*")
                //允许调用的域，例如：http://172.16.12.156:8888
                .allowedOrigins("*")
                //接口调用方式，POST、GET等
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

}
