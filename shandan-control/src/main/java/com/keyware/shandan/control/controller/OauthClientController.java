package com.keyware.shandan.control.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.datasource.entity.DBUserTableVo;
import com.keyware.shandan.datasource.entity.DataSourceVo;
import com.keyware.shandan.datasource.service.DataSourceService;
import com.keyware.shandan.frame.config.component.HttpRestTemplate;
import com.keyware.shandan.system.entity.OauthClientDetails;
import com.keyware.shandan.system.service.OauthClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * Oauth2客户端前端控制器
 *
 * @author Guoxin
 * @since 2021/11/30
 */
@RestController
@RequestMapping("/sys/oauth/client")
public class OauthClientController extends BaseController<OauthClientDetailsService, OauthClientDetails, Object> {

    @Autowired
    private  OauthClientDetailsService oauthClientDetailsService;


    /**
     * 跳转客户端管理首页
     * @param mov
     * @return
     */
    @GetMapping("/")
    public ModelAndView client(ModelAndView mov){
        mov.setViewName("client/client");
        return mov;
    }

    /**
     * 跳转客户端编辑页
     * @param
     * @return
     */
    @GetMapping("/edit")
    public ModelAndView edit(ModelAndView modelAndView){
        modelAndView.setViewName("client/clientEdit");
        return modelAndView;
    }


    /**
     * 根据客户端ID查询所有数据表
     * @param id
     * @return
     */
    @PostMapping("/table/list")
    public Result<OauthClientDetails> tableList(OauthClientDetails table, String id){
        if(StringUtils.isBlank(id)){
            return Result.of(null, false, "id 参数不能为空");
        }
        OauthClientDetails oauthClientDetails = oauthClientDetailsService.getById(id);
        if(oauthClientDetails == null){
            return Result.of(null, false, "客户端不存在");
        }
        return Result.of(oauthClientDetails);
    }





}
