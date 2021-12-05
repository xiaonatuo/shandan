package com.keyware.shandan.controller;

import com.keyware.shandan.common.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证服务扩展接口
 */
@RestController
@RequestMapping("/oauth")
public class OauthController {

    @Autowired
    private ConsumerTokenServices consumerTokenServices;

    @Autowired
    private JwtTokenStore jwtTokenStore;

    /**
     * 获取用户认证信息
     * @param access_token 请求token
     * @return 用户认证信息
     */
    @GetMapping("/user/info")
    public Result<Authentication> userInfo(@RequestParam String access_token){
        OAuth2Authentication authentication = jwtTokenStore.readAuthentication(access_token);
        if(authentication == null) return Result.of(null, false, "用户信息未找到");
        return Result.of(authentication.getUserAuthentication());
    }

    /**
     * 移除token，使token失效
     * @param access_token 请求token
     * @return 成功状态
     */
    @GetMapping("/token/revoke")
    public Result<Object> revokeToken(@RequestParam String access_token){
        return Result.of(consumerTokenServices.revokeToken(access_token));
    }

}
