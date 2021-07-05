package com.keyware.shandan.browser.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.browser.entity.ConditionVo;

/**
 * 数据检索服务类
 *
 * @author GuoXin
 * @since 2021/7/1
 */
public interface SearchService {

    /**
     * 全文检索
     *
     * @param condition 条件
     * @return -
     */
    Page<Object> esSearch(ConditionVo condition);
}
