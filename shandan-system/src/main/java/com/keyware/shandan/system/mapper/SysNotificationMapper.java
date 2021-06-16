package com.keyware.shandan.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.system.entity.SysNotification;
import com.keyware.shandan.common.mapper.IBaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 系统通知表 Mapper 接口
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-11
 */
@Mapper
public interface SysNotificationMapper extends IBaseMapper<SysNotification> {

    /**
     * 查询未读通知分页数据
     * @param page
     * @return
     */
    Page<SysNotification> selectUnreadNotificationPageByUserId(Page<SysNotification> page, String userId);
}
