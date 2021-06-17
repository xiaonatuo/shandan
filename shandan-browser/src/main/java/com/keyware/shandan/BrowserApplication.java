package com.keyware.shandan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

/**
 * 启动类
 *
 * @author GuoXin
 * @since 2021/6/16
 */
@SpringBootApplication
public class BrowserApplication {
    public static void main(String[] args) {
        SpringApplication.run(BrowserApplication.class, args);
    }

    /**
     * 解决不能注入session注册表问题
     */
    @Bean
    SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}
