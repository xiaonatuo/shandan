package com.keyware.shandan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@SpringBootApplication
public class OauthApplication {
    public static void main(String[] args) {
        SpringApplication.run(OauthApplication.class, args);
    }

    @Controller
    @RequestMapping
    static class IndexController{

        @GetMapping("/index")
        public ModelAndView index(Principal principal){
            ModelAndView modelAndView = new ModelAndView("index");
            modelAndView.addObject("principal", principal);
            return modelAndView;
        }
    }
}
