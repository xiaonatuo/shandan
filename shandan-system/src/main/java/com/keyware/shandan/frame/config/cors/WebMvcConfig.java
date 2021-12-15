package com.keyware.shandan.frame.config.cors;

import com.keyware.shandan.frame.properties.CustomProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private CustomProperties customProperties;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index");
        WebMvcConfigurer.super.addViewControllers(registry);
    }

    /**
     * 文件上传资源虚拟映射
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resourceHandler = customProperties.getFileStorage().getUploadFileMapper();
        String resourceLocations = customProperties.getFileStorage().getPath();
        registry.addResourceHandler(resourceHandler).addResourceLocations("file:///" + resourceLocations + "/");
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
                .addMapping("/upload/**")
                //允许调用的域，例如：http://172.16.12.156:8888
                .allowedOrigins("*")
                //接口调用方式，POST、GET等
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

}
