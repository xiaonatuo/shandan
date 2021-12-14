package com.keyware.shandan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

/**
 * 应用桌面系统
 *
 * @author Guoxin
 * @since 2021/7/21
 */
@SpringBootApplication
public class DesktopApplication {
    public static void main(String[] args) {
        SpringApplication.run(DesktopApplication.class, args);
    }
    /**
     * 解决不能注入session注册表问题
     */
    @Bean
    SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}
