package com.keyware.shandan.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.system.entity.SysNotification;
import com.keyware.shandan.system.service.SysNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
