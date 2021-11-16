package com.keyware.shandan.frame.config.security;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 注销处理
 */
@Component
public class LogoutHandlerConfig implements LogoutHandler {
    @Autowired
    private SecurityUtil securityUtil;

    @Value("${oauth.server}")
    private String oauthServer;

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        //剔除退出用户
        Object principal = authentication.getPrincipal();
        if (principal != null) {
            if (principal instanceof User) {
                securityUtil.sessionRegistryRemoveUser((User) principal);
            }

            try {

                httpServletResponse.sendRedirect(oauthServer + "/logout");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
