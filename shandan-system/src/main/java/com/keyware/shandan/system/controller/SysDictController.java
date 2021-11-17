package com.keyware.shandan.system.controller;

import org.springframework.web.bind.annotation.*;
import com.keyware.shandan.common.controller.BaseController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.keyware.shandan.system.service.SysDictService;
import com.keyware.shandan.system.entity.SysDict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * 系统数据字典表 前端控制器
 * </p>
 *
 * @author GuoXin
 * @since 2021-11-17
 */
@RestController
@RequestMapping("/sys/dict")
public class SysDictController extends BaseController<SysDictService, SysDict, Integer> {


    @Autowired
    private SysDictService sysDictService;

    @GetMapping("/")
    public ModelAndView view(ModelAndView view){
        view.setViewName("");
        return view;
    }
}
