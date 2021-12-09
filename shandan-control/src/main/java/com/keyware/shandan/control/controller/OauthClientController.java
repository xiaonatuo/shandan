package com.keyware.shandan.control.controller;

import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.frame.config.component.HttpRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * Oauth2客户端前端控制器
 *
 * @author Guoxin
 * @since 2021/11/30
 */
@RestController
@RequestMapping("/sys/oauth/client")
public class OauthClientController {

    @Autowired
    private HttpRestTemplate restTemplate;


    @GetMapping("/")
    public ModelAndView client(ModelAndView mov){
        mov.setViewName("client/client");
        return mov;
    }

    @GetMapping("/list")
    public Result<Object> list(){

        return Result.of(null);
    }

    @GetMapping("/{clientId}")
    public Result<Object> get(@PathVariable String clientId){
        return Result.of(null);
    }

    public Result<Object> save(){
        return Result.of(null);
    }

    public Result<Object> delete(){
        return Result.of(null);
    }
}
