package com.keyware.shandan.frame.config.security;

import com.keyware.shandan.system.entity.SysRole;
import com.keyware.shandan.system.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CaptchaFilterConfig captchaFilterConfig;

    @Autowired
    private UserConfig userConfig;

    @Autowired
    private PasswordConfig passwordConfig;

    @Autowired
    private LoginFailureHandlerConfig loginFailureHandlerConfig;

    @Autowired
    private LoginSuccessHandlerConfig loginSuccessHandlerConfig;

    @Autowired
    private LogoutHandlerConfig logoutHandlerConfig;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private MyFilterInvocationSecurityMetadataSource myFilterInvocationSecurityMetadataSource;

    @Autowired
    private AjaxAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private DataSource dataSource;

    //无需权限访问的URL，不建议用/**/与/*.后缀同时去适配，有可以会受到CaptchaFilterConfig判断的影响
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
            "/sys/file/download/**"
    };

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                //用户认证处理
                .userDetailsService(userConfig)
                //密码处理
                .passwordEncoder(passwordConfig);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 关闭csrf防护
                .csrf().disable()
                .headers().frameOptions().disable()
                .and();

        http
                //登录处理
                .addFilterBefore(captchaFilterConfig, UsernamePasswordAuthenticationFilter.class)
                .formLogin()
                .loginProcessingUrl("/login")
                //未登录时默认跳转页面
                .loginPage("/loginPage")
                .failureHandler(loginFailureHandlerConfig)
                .successHandler(loginSuccessHandlerConfig)
                .permitAll()
                .and();
        http
                //登出处理
                .logout()
                .addLogoutHandler(logoutHandlerConfig)
                .logoutUrl("/logout")
                .logoutSuccessUrl("/loginPage")
                .permitAll()
                .and();
        http
                //定制url访问权限，动态权限读取，参考：https://www.jianshu.com/p/0a06496e75ea
                .addFilterAfter(dynamicallyUrlInterceptor(), FilterSecurityInterceptor.class)
                .authorizeRequests()

                //无需权限访问
                .antMatchers(MATCHERS_PERMITALL_URL).permitAll()

                //其他接口需要登录后才能访问
                .anyRequest().authenticated()
                .and();

        http
                //开启记住我
                .rememberMe()
                .tokenValiditySeconds(60 * 60 * 24)
                .tokenRepository(persistentTokenRepository())
                .userDetailsService(userConfig)
                .and();

        http.exceptionHandling()
                .defaultAuthenticationEntryPointFor(authenticationEntryPoint, new AjaxRequestMatcher());
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        /*JdbcTokenRepositoryImpl persistentTokenRepository = new JdbcTokenRepositoryImpl();
        persistentTokenRepository.setDataSource(dataSource);
        return persistentTokenRepository;*/
        return new InMemoryTokenRepositoryImpl();
    }

    //配置filter
    @Bean
    public DynamicallyUrlInterceptor dynamicallyUrlInterceptor(){
        //首次获取
        List<SysRole> roleList = sysRoleService.list();
        myFilterInvocationSecurityMetadataSource.setRequestMap(roleList);
        //初始化拦截器并添加数据源
        DynamicallyUrlInterceptor interceptor = new DynamicallyUrlInterceptor();
        interceptor.setSecurityMetadataSource(myFilterInvocationSecurityMetadataSource);

        //配置RoleVoter决策
        List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<>();
        decisionVoters.add(new RoleVoter());

        //设置认证决策管理器
        interceptor.setAccessDecisionManager(new MyAccessDecisionManager(decisionVoters));
        return interceptor;
    }
}