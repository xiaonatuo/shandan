package com.keyware.shandan.control.controller;

import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.system.entity.OauthClientDetails;
import com.keyware.shandan.system.service.DesktopService;
import com.keyware.shandan.system.service.OauthClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

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

    @Autowired
    private DesktopService desktopService;


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
     * 上传图标
     *
     * @param file
     * @return
     */
    @PostMapping("/upload/icon")
    public Result<String> iconUpload(MultipartFile file) {
        try {
            return Result.of("images" + desktopService.uploadIcon(file));
        } catch (IOException e) {
            e.printStackTrace();
            return Result.of(null, false, "上传失败");
        }
    }


}
