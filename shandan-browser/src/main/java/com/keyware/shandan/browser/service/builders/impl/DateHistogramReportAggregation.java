package com.keyware.shandan.browser.service.builders.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.browser.entity.ReportVo;
import com.keyware.shandan.browser.service.builders.ReportAggregation;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期直方图类型报表聚合构建类
 *
 * @author GuoXin
 * @since 2021/7/13
 */
public class DateHistogramReportAggregation extends ReportAggregation<ParsedDateHistogram> {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public DateHistogramReportAggregation(SearchRequest request, ReportVo report) {
        super(request, report);
    }

    @Override
    public void setAlias() {
        alias = report.getFieldX() + "_histogram";
    }

    @Override
    public AggregationBuilder builder() {
        DateHistogramAggregationBuilder builder = AggregationBuilders.dateHistogram(alias).field(report.getFieldX());
        switch (report.getDateInterval()) {
            case "minute":
                builder.dateHistogramInterval(DateHistogramInterval.MINUTE);
                break;
            case "hour":
                builder.dateHistogramInterval(DateHistogramInterval.HOUR);
                break;
            case "day":
                builder.dateHistogramInterval(DateHistogramInterval.DAY);
                break;
            case "week":
                builder.dateHistogramInterval(DateHistogramInterval.WEEK);
                break;
            case "month":
                builder.dateHistogramInterval(DateHistogramInterval.MONTH);
                break;
            case "year":
                builder.dateHistogramInterval(DateHistogramInterval.YEAR);
        }
        subAggregation(builder);
        return builder;
    }

    @Override
    public JSONObject parse() {
        ParsedDateHistogram histogram = getAggregations();

        JSONArray xAxis = new JSONArray();
        JSONArray series = new JSONArray();
        histogram.getBuckets().forEach(bucket -> {
            DateTime time = (DateTime) bucket.getKey();
            Date date = time.toLocalDateTime().toDate();

            xAxis.add(sdf.format(date));
            series.add(bucket.getDocCount());
        });
        JSONObject json = new JSONObject();
        json.put("xAxis", xAxis);
        json.put("series", series);
        return json;
    }
}
