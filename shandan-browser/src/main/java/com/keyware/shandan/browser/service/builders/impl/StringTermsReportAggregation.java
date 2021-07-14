package com.keyware.shandan.browser.service.builders.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.browser.entity.ReportVo;
import com.keyware.shandan.browser.service.builders.ReportAggregation;
import com.keyware.shandan.common.util.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;

/**
 * 字符串类型报表聚合构建类
 *
 * @author GuoXin
 * @since 2021/7/13
 */
public class StringTermsReportAggregation extends ReportAggregation<ParsedStringTerms> {
    public StringTermsReportAggregation(SearchRequest request, ReportVo report) {
        super(request, report);
    }

    @Override
    public void setAlias() {
        alias = report.getFieldX() + "_terms";
    }

    @Override
    public AggregationBuilder builder() {
        return AggregationBuilders.terms(alias).field(report.getFieldX());
    }

    @Override
    public JSONObject parse() {
        ParsedStringTerms terms = getAggregations();
        JSONArray array = new JSONArray();
        terms.getBuckets().forEach(bucket -> {
            JSONObject json = new JSONObject();
            json.put("name", StringUtils.isBlank(bucket.getKeyAsString()) ? "未知" : bucket.getKeyAsString());
            json.put("value", bucket.getDocCount());
            array.add(json);
        });
        JSONObject json = new JSONObject();
        json.put("series", array);
        return json;
    }
}
