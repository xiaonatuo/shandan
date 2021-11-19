package com.keyware.shandan.system.controller;


import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.system.entity.SysDictType;
import com.keyware.shandan.system.service.SysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

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

    @Autowired
    private SysDictTypeService dictTypeService;

    @GetMapping("/")
    public ModelAndView view(ModelAndView view) {
        view.setViewName("");

        return view;
    }

    @DeleteMapping("/delete")
    public Result<Object> deleteByIds(String ids) {
        if (StringUtils.isBlank(ids)) {
            return Result.of(null, false, "参数不能为空");
        }
        dictTypeService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.of(true);
    }
}
