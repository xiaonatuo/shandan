package com.keyware.shandan.config;

import com.keyware.shandan.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    public static final String[] MATCHERS_PERMITALL_URL = {
            "/oauth/**",
            "/login",
            "/logout",
            "/loginPage",
            "/favicon.ico",
            "/js/**",
            "/css/**",
            "/webjars/**",
            "/user/**",
            "/error/*",
            "/upload/**",
            "/assets/**",
            "/sys/file/download/**",
            "/images/**"
    };
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private InMemoryTokenStore inMemoryTokenStore;

    @Autowired
    private UserOauthDetailsService userOauthDetailsService;

    @Value("${oauth.logout.success-url:login?logout}")
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
                .defaultSuccessUrl("/")
                .and()
                .authorizeRequests()
                .antMatchers(MATCHERS_PERMITALL_URL).permitAll()
                .anyRequest()
                .authenticated()
                .and().csrf().disable().cors();

        http.logout().addLogoutHandler(((request, response, authentication) -> {
            String author = request.getHeader("Authorization");
            if(StringUtils.isNotBlank(author)){
                author = author.replaceFirst("Bearer ", "");
                OAuth2AccessToken token = inMemoryTokenStore.readAccessToken(author);
                if(token != null){
                    inMemoryTokenStore.removeAccessToken(token);
                    inMemoryTokenStore.removeRefreshToken(token.getRefreshToken());
                    OAuth2Authentication oauth2Auth = inMemoryTokenStore.readAuthentication(author);
                    Authentication authen = oauth2Auth.getUserAuthentication();
                    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
                    logoutHandler.logout(request, response, authen);
                }
            }
        })).logoutSuccessHandler((request, response, authentication) -> {
            System.out.println("http = " + http);

            response.sendRedirect(logoutUrl);
        });
    }

}
