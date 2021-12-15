package com.keyware.shandan.bianmu.confing;

import com.keyware.shandan.frame.properties.CustomProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * BianmuWebMvcConfig
 *
 * @author GuoXin
 * @since 2021/7/31
 */
@Configuration
public class BianmuWebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private CustomProperties customProperties;

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
}
