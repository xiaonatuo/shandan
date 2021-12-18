package com.keyware.shandan.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.system.entity.SysNotification;
import com.keyware.shandan.system.entity.SysNotificationUnread;
import com.keyware.shandan.system.service.SysNotificationService;
import com.keyware.shandan.system.service.SysNotificationUnreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * 系统通知表 前端控制器
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-11
 */
@RestController
@RequestMapping("/sys/notification")
public class SysNotificationController extends BaseController<SysNotificationService, SysNotification, String> {

    @Autowired
    private SysNotificationService sysNotificationService;
    @Autowired
    private SysNotificationUnreadService sysNotificationUnreadService;

    @GetMapping("/")
    public ModelAndView index(){
        return new ModelAndView("sys/notification/notification");
    }

    @Override
    @GetMapping("/page")
    public Result<Page<SysNotification>> page(Page<SysNotification> page, SysNotification entity) {
        return super.page(page, entity);
    }

    /**
     * 未读通知分页列表
     * @param page
     * @return
     */
    @GetMapping("/page/unread")
    public Result<Page<SysNotification>> unreadList(Page<SysNotification> page){
        return Result.of(sysNotificationService.unreadPage(page));
    }

    /**
     * 通知设置已读
     * @param notificationId 通知ID
     * @return
     */
    @PostMapping("/read")
    public Result<Boolean> read(String notificationId){
        if(StringUtils.isBlank(notificationId)){
            return Result.of(false, false, "参数错误");
        }
        return Result.of(sysNotificationService.read(notificationId));
    }

    /**
     * 未读数据查询
     * @param id
     * @return
     */
    @GetMapping("unread/get/{id}")
    public Result<SysNotificationUnread> getUnread(@PathVariable("id") String id) {
        return sysNotificationUnreadService.get(id);
    }


    /**
     * 已读信息查询
     * @param modelAndView
     * @param id
     * @return
     */
    @GetMapping("/detail/{id}")
    public ModelAndView detail(ModelAndView modelAndView, @PathVariable("id")String id){
        Result<SysNotification> result = sysNotificationService.get(id);
        modelAndView.setViewName("sys/notification/notificationDetail");
        modelAndView.addObject("sysNotification", result.getData());

        return modelAndView;
    }


    /**
     * 未读信息查询
     * @param modelAndView
     * @param id
     * @return
     */
    @GetMapping("/unread/detail/{id}")
    public ModelAndView unreadDetail(ModelAndView modelAndView, @PathVariable("id")String id){
        Result<SysNotification> result = sysNotificationService.get(id);
        modelAndView.setViewName("sys/notification/notificationDetail");
        modelAndView.addObject("sysNotification", result.getData());
        return modelAndView;
    }

}
