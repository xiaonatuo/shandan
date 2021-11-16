package com.keyware.shandan.controller;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
@RequestMapping()
public class OauthController {

    @Autowired
    private ConsumerTokenServices consumerTokenServices;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/user/info")
    public Principal userInfo(Principal principal){
        System.out.println("principal = " + principal);
        return principal;
    }

    @DeleteMapping("/oauth/token/revoke")
    public Boolean revokeToken(String access_token){
        return consumerTokenServices.revokeToken(access_token);
    }

}
