package com.keyware.shandan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.system.entity.SysNotification;
import com.keyware.shandan.system.entity.SysNotificationUnread;
import com.keyware.shandan.system.mapper.SysNotificationMapper;
import com.keyware.shandan.system.service.SysNotificationService;
import com.keyware.shandan.system.service.SysNotificationUnreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统通知表 服务实现类
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-11
 */
@Service
public class SysNotificationServiceImpl extends BaseServiceImpl<SysNotificationMapper, SysNotification, String> implements SysNotificationService {

    @Autowired
    private SysNotificationMapper sysNotificationMapper;

    @Autowired
    private SysNotificationUnreadService unreadService;

    @Override
    public Page<SysNotification> page(Page<SysNotification> page, QueryWrapper<SysNotification> wrapper) {
        wrapper.like("RECEIVE_ID", "%" +SecurityUtil.getLoginSysUser().getUserId() + "%");
        return super.page(page, wrapper);
    }

    /**
     * 未读通知分页列表
     *
     * @param page
     * @return
     */
    @Override
    public Page<SysNotification> unreadPage(Page<SysNotification> page) {

        return sysNotificationMapper.selectUnreadNotificationPageByUserId(page, SecurityUtil.getLoginSysUser().getUserId());
    }

    /**
     * 发送通知
     *
     * @param notification 通知
     * @return
     */
    @Override
    public Boolean sendNotification(SysNotification notification) {
        if(save(notification)){
            String[] ids = notification.getReceiveId().split(",");
            unreadService.saveBatch(Arrays.stream(ids).map(id -> new SysNotificationUnread(id, notification.getId())).collect(Collectors.toList()));
        }
        return true;
    }

    /**
     * 设置已读
     * @param notificationId
     * @return
     */
    @Override
    public Boolean read(String notificationId) {

        return unreadService.remove(new QueryWrapper<>(new SysNotificationUnread(SecurityUtil.getLoginSysUser().getUserId(), notificationId)));
    }
}
