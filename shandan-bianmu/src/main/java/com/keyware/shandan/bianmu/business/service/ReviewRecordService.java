package com.keyware.shandan.bianmu.business.service;

import com.keyware.shandan.bianmu.business.entity.ReviewRecordVo;
import com.keyware.shandan.common.service.IBaseService;

/**
 * <p>
 * 审核记录表 服务类
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-03
 */
public interface ReviewRecordService extends IBaseService<ReviewRecordVo, String> {

    /**
     * 审核操作
     * @param entityId 数据ID
     * @param entityType 数据对象类型
     * @param status 审核状态
     * @param opinion 审核意见
     * @return
     */
    Boolean review(String entityId, String entityType, String status, String opinion);
}
