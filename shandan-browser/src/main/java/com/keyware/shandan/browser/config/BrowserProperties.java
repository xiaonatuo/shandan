package com.keyware.shandan.browser.config;

import com.keyware.shandan.frame.properties.CustomProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Data
@Component
@ConfigurationProperties(prefix = "project.browser")
public class BrowserProperties {
    private String dbtoolAddress;
}
