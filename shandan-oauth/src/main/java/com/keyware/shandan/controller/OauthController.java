package com.keyware.shandan.controller;

import com.keyware.shandan.beans.ClientDetailsDTO;
import com.keyware.shandan.config.OauthClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping()
public class OauthController {

    @Autowired
    private ConsumerTokenServices consumerTokenServices;

    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
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
