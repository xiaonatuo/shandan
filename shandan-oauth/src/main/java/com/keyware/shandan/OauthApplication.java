package com.keyware.shandan;

import com.keyware.shandan.common.constants.SystemConstants;
import com.keyware.shandan.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@SpringBootApplication
public class OauthApplication {
    public static void main(String[] args) {
        SpringApplication.run(OauthApplication.class, args);
    }

    @Controller
    @RequestMapping
    static class IndexController {

        @Autowired
        private DictService dictService;

        @GetMapping("/index")
        public ModelAndView index(Principal principal) {
            ModelAndView modelAndView = new ModelAndView("index");
            modelAndView.addObject("principal", principal);
            modelAndView.addObject("desktopAddress", dictService.getDesktopUrl());
            return modelAndView;
        }

        @RequestMapping(value = "/login", method = {RequestMethod.GET})
        public ModelAndView login(ModelAndView mav) {
            mav.setViewName("login");
            mav.addObject("logoName", dictService.getDictByTypeAndCode(SystemConstants.DICT_TYPE_PUBLIC_SETTING, SystemConstants.OAUTH_LOGIN_TEXT).getDictValue());
            return mav;
        }
    }
}
