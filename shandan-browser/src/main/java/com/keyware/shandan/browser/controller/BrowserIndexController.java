package com.keyware.shandan.browser.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * 首页 前端控制器
 * </p>
 *
 * @author GuoXin
 * @since 2021/6/17
 */
@RestController
@RequestMapping("/")
public class BrowserIndexController {

    @GetMapping("/index")
    public ModelAndView index(){
        return new ModelAndView("index");
    }
}
