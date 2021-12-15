package com.keyware.shandan.control.config;

import com.keyware.shandan.system.utils.ProjectProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web资源配置
 *
 * @author GuoXin
 * @since 2021/7/27
 */
@Configuration
public class ControlWebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ProjectProperties projectProperties;

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
