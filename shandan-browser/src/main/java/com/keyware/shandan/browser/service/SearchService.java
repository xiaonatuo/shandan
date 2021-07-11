package com.keyware.shandan.browser.service;

import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.browser.entity.ConditionVo;
import com.keyware.shandan.browser.entity.PageVo;
import com.keyware.shandan.browser.entity.ReportVo;

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

    /**
     * 数据统计
     * @param report 统计类型
     * @return
     */
    JSONObject report(ReportVo report) throws IOException;
}
