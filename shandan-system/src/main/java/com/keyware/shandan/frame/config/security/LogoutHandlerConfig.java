package com.keyware.shandan.frame.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 注销处理
 */
@Component
public class LogoutHandlerConfig implements LogoutHandler {
    @Autowired
    private SecurityUtil securityUtil;

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        //剔除退出用户
        Object principal = authentication.getPrincipal();
        if (principal !=null){
            securityUtil.sessionRegistryRemoveUser((User)principal);
        }
    }
}
