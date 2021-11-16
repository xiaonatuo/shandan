package com.keyware.shandan.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@EnableAuthorizationServer
@Configuration
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients()
                .tokenKeyAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(inMemoryClientDetailsService());
    }


    @Bean
    public ClientDetailsService inMemoryClientDetailsService() throws Exception {
        return new InMemoryClientDetailsServiceBuilder()
                // client oa application
                .withClient("browser")
                .secret(passwordEncoder.encode("secret"))
                .scopes("all")
                .authorizedGrantTypes("authorization_code", "refresh_token")
                .redirectUris("http://localhost:8090/browser/login")
                .accessTokenValiditySeconds(7200)
                .autoApprove(true)

                .and()

                // client crm application
                .withClient("control")
                .secret(passwordEncoder.encode("control123456"))
                .scopes("all")
                .authorizedGrantTypes("authorization_code", "refresh_token")
                .redirectUris("http://localhost:8100/control/login")
                .accessTokenValiditySeconds(7200)
                .autoApprove(true)

                .and()
                .withClient("bianmu")
                .secret(passwordEncoder.encode("bianmu123456"))
                .scopes("all")
                .authorizedGrantTypes("authorization_code", "refresh_token")
                .redirectUris("http://localhost:8080/bianmu/login")
                .accessTokenValiditySeconds(7200)
                .autoApprove(true)
                .and()
                .build();
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.accessTokenConverter(jwtAccessTokenConverter())
                .tokenStore(jwtTokenStore());
    }

    @Bean
    public JwtTokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey("123456");
        return jwtAccessTokenConverter;
    }

}
