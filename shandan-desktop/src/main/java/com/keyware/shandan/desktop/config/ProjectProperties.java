package com.keyware.shandan.desktop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("project")
public class ProjectProperties {
    private String uploadPath;


}
