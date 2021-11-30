package com.keyware.shandan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
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
    @ResponseBody
    public Boolean revokeToken(@RequestBody String access_token){
        return consumerTokenServices.revokeToken(access_token);
    }

}
