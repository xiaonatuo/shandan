package com.keyware.shandan.controller;

import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.bind.annotation.*;

/**
 * 认证服务扩展接口
 */
@RestController
@RequestMapping("/oauth")
public class OauthController {

    @Autowired
    private ConsumerTokenServices consumerTokenServices;

    /*@Autowired
    private JwtTokenStore jwtTokenStore;*/

    @Autowired
    private InMemoryTokenStore inMemoryTokenStore;

    /**
     * 获取用户认证信息
     * @param access_token 请求token
     * @return 用户认证信息
     */
    @GetMapping("/user/info")
    public Object userInfo(@RequestParam(required = false) String access_token, @RequestHeader String Authorization){
        if(StringUtils.isBlank(access_token) && StringUtils.isBlank(Authorization)){
            return Result.of(null, false, "access_token is null");
        }
        String token = StringUtils.isNotBlank(access_token) ? access_token : Authorization;
        token = token.replaceFirst("Bearer ", "");
        OAuth2Authentication authentication = inMemoryTokenStore.readAuthentication(token);
        if(authentication == null) return Result.of(null, false, "用户信息未找到");

        JSONObject result = (JSONObject) JSONObject.toJSON(Result.of(authentication.getUserAuthentication()));
        result.getJSONObject("data").put("principal", authentication.getName());
        result.putAll((JSONObject) JSONObject.toJSON(authentication));
        return result;
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
