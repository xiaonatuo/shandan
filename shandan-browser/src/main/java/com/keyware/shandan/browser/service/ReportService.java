package com.keyware.shandan.browser.service;

import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.browser.entity.ReportVo;
import com.keyware.shandan.browser.entity.SearchConditionVo;

import java.util.List;
import java.util.Map;

/**
 * 统计报表服务接口
 *
 * @author GuoXin
 * @since 2021/12/20
 */
public interface ReportService {

    /**
     * @param metadata 数据资源实体
     * @param condition 条件参数
     * @return 条件查询的sql语句
     * @throws Exception -
     */
    String getConditionsQuerySql(MetadataBasicVo metadata, SearchConditionVo condition) throws Exception;

    /**
     * 获取统计sql
     *
     * @param report 统计参数
     * @return 统计sql语句
     * @throws Exception -
     */
    String getStatisticsSql(ReportVo report) throws Exception;

    /**
     * 开始统计查询
     *
     * @param report 统计参数
     * @return 统计数据
     * @throws Exception -
     */
    Map<String, Object> statisticsQuery(ReportVo report) throws Exception;
}
