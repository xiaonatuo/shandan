package com.keyware.shandan.browser.service.builders.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.browser.entity.ReportVo;
import com.keyware.shandan.browser.service.builders.ReportAggregation;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.HistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedHistogram;

/**
 * 数值范围直方图类型报表聚合构建类
 *
 * @author GuoXin
 * @since 2021/7/13
 */
public class NumberHistogramReportAggregation extends ReportAggregation<ParsedHistogram> {

    public NumberHistogramReportAggregation(SearchRequest request, ReportVo report) {
        super(request, report);
    }

    @Override
    public void setAlias() {
        alias = report.getFieldX() + "_histogram";
    }

    @Override
    public AggregationBuilder builder() {
        HistogramAggregationBuilder builder = AggregationBuilders
                .histogram(alias)
                .field(report.getFieldX())
                .interval(report.getNumberInterval());
        subAggregation(builder);
        return builder;
    }

    @Override
    public JSONObject parse() {
        ParsedHistogram histogram = getAggregations();
        JSONArray xAxis = new JSONArray();
        JSONArray series = new JSONArray();
        histogram.getBuckets().forEach(bucket -> {
            xAxis.add(bucket.getKeyAsString());
            series.add(bucket.getDocCount());
        });
        JSONObject json = new JSONObject();
        json.put("xAxis", xAxis);
        json.put("series", series);
        return json;
    }
}
