package com.keyware.shandan.system.controller;


import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.system.entity.SysDictType;
import com.keyware.shandan.system.service.SysDictTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
@RequestMapping("/sys/dict/type")
public class SysDictTypeController extends BaseController<SysDictTypeService, SysDictType, String> {


    @GetMapping("/")
    public ModelAndView view(ModelAndView view){
        view.setViewName("");

        return view;
    }
}
