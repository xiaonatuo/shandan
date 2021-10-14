package com.keyware.shandan.frame.config.security;


import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

/**
 * AjaxRequestMatcher
 *
 * @author Administrator
 * @since 2021/10/14
 */
public class AjaxRequestMatcher implements RequestMatcher {
    @Override
    public boolean matches(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With")) ||
                request.getHeader("Accept") != null &&
                        request.getHeader("Accept").contains("application/json");
    }
}
