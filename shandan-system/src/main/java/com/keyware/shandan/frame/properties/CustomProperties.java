package com.keyware.shandan.frame.properties;

import com.keyware.shandan.common.enums.SystemTypes;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 系统定义属性
 *
 * @author GuoXin
 * @since 2021/6/29
 */
@Data
@Configuration
@ConfigurationProperties("project")
public class CustomProperties {
    /**
     * 系统ID
     */
    private SystemTypes id;
    /**
     * 系统名称
     */
    private String appName;

    /**
     * 是否启用验证码
     */
    private Boolean captchaEnable = false;
    /**
     * 是否启用记住我
     */
    private Boolean rememberMeEnable = false;

    /**
     * 文件存储配置
     */
    private FileStorageProperties fileStorage;

    /**
     * 数据分类编目服务地址(弃用)
     */
    @Deprecated
    private String bianmuServer = "http://localhost:8080/";

    /**
     * 文件存储配置属性
     */
    @Data
    public static class FileStorageProperties {
        /**
         * 存储位置
         */
        @Deprecated
        private String location = "local";
        /**
         * 存储路径
         */
        private String path;
        /**
         * 文件服务映射别名
         */
        private String uploadFileMapper = "/upload/**";
    }
}
