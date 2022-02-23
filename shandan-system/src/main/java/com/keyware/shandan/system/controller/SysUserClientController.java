package com.keyware.shandan.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.StreamUtil;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.system.entity.SysUserClient;
import com.keyware.shandan.system.service.OauthClientDetailsService;
import com.keyware.shandan.system.service.SysUserClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * <p>
 * 系统用户与Oauth2客户端关系表 前端控制器
 * </p>
 *
 * @author fengzhi
 * @since 2021-12-13
 */
@RestController
@RequestMapping("/sys/userClient/")
public class SysUserClientController extends BaseController<SysUserClientService, SysUserClient, String> {

    @Autowired
    private SysUserClientService sysUserClientService;

    @Autowired
    private OauthClientDetailsService oauthClientDetailsService;


    @GetMapping("/layer")
    public ModelAndView roleLayer(ModelAndView modelAndView, String userId) {
        List<SysUserClient> userClientList = sysUserClientService.findByUserId(userId).getData();
        List<String> userClientIds = userClientList.stream().map(SysUserClient::getClientId).collect(Collectors.toList());
        modelAndView.addObject("userId", userId);
        modelAndView.addObject("userClientIds", userClientIds);
        modelAndView.addObject("clientList", StreamUtil.as(oauthClientDetailsService.list()).filter(item -> !"desktop".equals(item.getId())).toList());
        modelAndView.setViewName("sys/client/clientLayer");
        return modelAndView;
    }

    @PostMapping("saveAllByUserId")
    public Result<Boolean> saveAllByUserId(SysUserClient userClient) {
        return sysUserClientService.saveAllByUserId(userClient.getUserId(), userClient.getClientIdList());
    }

    @Override
    @PostMapping("save")
    public Result<SysUserClient> save(SysUserClient sysUserClient) {
        //如果存在就删除，如果不存在就保存
        List<SysUserClient> list = sysUserClientService.list(new QueryWrapper<>(sysUserClient));
        if(list.size() > 0){
            sysUserClientService.remove(new QueryWrapper<>(sysUserClient));
        }else{
            //创建时间
            sysUserClient.setCreateTime(new Date());
            //登录用户
            sysUserClient.setCreateUser(SecurityUtil.getLoginUser().getUsername());
            sysUserClientService.save(sysUserClient);
        }
        return Result.of(sysUserClient, true);
    }

}
