package com.keyware.shandan.config;

import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * ClientDetailsService
 *
 * @author Administrator
 * @since 2021/11/30
 */
@Service
public class OauthClientDetailsService extends JdbcClientDetailsService {

    public OauthClientDetailsService(DataSource dataSource, PasswordEncoder passwordEncoder) {
        super(dataSource);
        setPasswordEncoder(passwordEncoder);
    }

}
