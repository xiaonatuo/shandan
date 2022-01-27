package com.keyware.shandan.system.controller;

import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.system.entity.SysDict;
import com.keyware.shandan.system.service.SysDictService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/sys/dict")
public class SysDictController extends BaseController<SysDictService, SysDict, String> {


    @Autowired
    private SysDictService sysDictService;

    /**
     * 跳转模块首页
     *
     * @param view 视图
     * @return 视图
     */
    @GetMapping("/")
    public ModelAndView view(ModelAndView view) {
        view.setViewName("sys/dict/dict");
        return view;
    }

    /**
     * 跳转编辑页
     *
     * @param view 视图
     * @param id   ID
     * @return 视图
     */
    @GetMapping("/edit")
    public ModelAndView editView(ModelAndView view, String id) {
        view.setViewName("sys/dict/dictEdit");
        SysDict dict = new SysDict();
        Boolean b = StringUtils.isNotBlank(id);
        if (Boolean.TRUE.equals(b)) {
            dict = sysDictService.getById(id);
        }
        view.addObject("dict", dict);
        return view;
    }
}
