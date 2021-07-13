package com.keyware.shandan.browser.service.builders;

import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.browser.entity.ReportVo;
import com.keyware.shandan.browser.service.builders.impl.DateHistogramReportAggregation;
import com.keyware.shandan.browser.service.builders.impl.NumberHistogramReportAggregation;
import com.keyware.shandan.browser.service.builders.impl.StringTermsReportAggregation;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 统计报表聚合抽象类
 *
 * @author GuoXin
 * @since 2021/7/13
 */
public abstract class ReportAggregation {
    /**
     * 达梦数据库日期类型集合
     */
    private final static String[] dm_date_types = {"DATE", "DATETIME", "DATETIME WITH TIME ZONE", "TIME", "TIMESTAMP", "TIME WITH TIME ZONE", "TIMESTAMP WITH TIME ZONE", "TIMESTAMP WITH LOCAL TIME ZONE"};
    /**
     * 达梦数据库数值类型集合
     */
    private final static String[] dm_number_types = {"NUMERIC", "NUMBER", "DECIMAL", "DEC", "INTEGER", "INT", "PLS_INTEGER", "BIGINT", "TINYINT", "BYTE", "SMALLINT", "FLOAT", "DOUBLE", "DOUBLE PRECISION"};

    protected ReportVo report;
    protected SearchRequest request;
    protected SearchResponse response;
    protected String alias;

    public ReportAggregation(SearchRequest request, ReportVo report) {
        this.request = request;
        this.report = report;
        setAlias();
    }

    public abstract void setAlias();

    /**
     * 构建聚合器
     *
     * @return -
     */
    public abstract AggregationBuilder builder();

    /**
     * 解析查询结果
     *
     * @return -
     */
    public abstract JSONObject parse();

    /**
     * 构建子聚合
     *
     * @param builder -
     */
    protected void subAggregation(AggregationBuilder builder) {
        switch (report.getAggregationType()) {
            case "sum": // 求和
                builder.subAggregation(AggregationBuilders.sum(report.getFieldY() + "_sum").field(report.getFieldY()));
                break;
            case "avg": // 平均数
                builder.subAggregation(AggregationBuilders.avg(report.getFieldY() + "_avg").field(report.getFieldY()));
                break;
            default: // 计数
                builder.subAggregation(AggregationBuilders.count(report.getFieldY() + "_count").field(report.getFieldY()));
        }
    }

    public void search(RestHighLevelClient client) throws IOException {
        this.request.source().from(0).size(0).aggregation(builder());
        this.response = client.search(this.request, RequestOptions.DEFAULT);
    }

    /**
     * 根据统计报表参数返回不同类型的聚合
     * @param request
     * @param report
     * @return
     */
    public static ReportAggregation getAggregation(SearchRequest request, ReportVo report){
        if (Arrays.asList(dm_date_types).contains(report.getFieldXType())) { // 日期类型
            return new DateHistogramReportAggregation(request, report);
        } else if (Arrays.asList(dm_number_types).contains(report.getFieldXType())) { //数值类型
            return new NumberHistogramReportAggregation(request, report);
        }else{// 字符串类型
            return new StringTermsReportAggregation(request, report);
        }
    }
}
