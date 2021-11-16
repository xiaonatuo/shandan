package com.keyware.shandan.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    public static final String[] MATCHERS_PERMITALL_URL = {
            "/login",
            "/logout",
            "/loginPage",
            "/favicon.ico",
            "/js/**",
            "/css/**",
            "/webjars/**",
            "/getVerifyCodeImage",
            "/error/*",
            "/upload/**",
            "/assets/**",
            "/sys/file/download/**",
            "/images/**"
    };
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserOauthDetailsService userOauthDetailsService;

    @Value("${oauth.logout.successUrl:/login?logout}")
    private String logoutUrl;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userOauthDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/assets/**", "/js/**", "/css/**", "/images/**");

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/login")
                .successForwardUrl("/")
                .and()
                .authorizeRequests()
                .antMatchers(MATCHERS_PERMITALL_URL).permitAll()
                .anyRequest()
                .authenticated()
                .and().csrf().disable().cors();

        http.logout().logoutSuccessUrl(logoutUrl);
    }
}
