package com.keyware.shandan.browser.service;

import com.keyware.shandan.browser.entity.ConditionVo;
import com.keyware.shandan.browser.entity.PageVo;

import java.io.IOException;

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
    PageVo esSearch(ConditionVo condition) throws IOException;
}
