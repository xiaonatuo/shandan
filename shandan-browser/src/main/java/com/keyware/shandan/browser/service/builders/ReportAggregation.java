package com.keyware.shandan.browser.service.builders;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.browser.entity.ReportVo;
import com.keyware.shandan.browser.service.builders.impl.DateHistogramReportAggregation;
import com.keyware.shandan.browser.service.builders.impl.NumberHistogramReportAggregation;
import com.keyware.shandan.browser.service.builders.impl.StringTermsReportAggregation;
import com.keyware.shandan.common.util.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.ParsedMultiBucketAggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedAutoDateHistogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.joda.time.DateTime;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * 统计报表聚合抽象类
 *
 * @author GuoXin
 * @since 2021/7/13
 */
public abstract class ReportAggregation<T extends ParsedMultiBucketAggregation> {
    /**
     * 达梦数据库日期类型集合
     */
    private final static String[] dm_date_types = {"DATE", "DATETIME", "DATETIME WITH TIME ZONE", "TIME", "TIMESTAMP", "TIME WITH TIME ZONE", "TIMESTAMP WITH TIME ZONE", "TIMESTAMP WITH LOCAL TIME ZONE"};
    /**
     * 达梦数据库数值类型集合
     */
    private final static String[] dm_number_types = {"NUMERIC", "NUMBER", "DECIMAL", "DEC", "INTEGER", "INT", "PLS_INTEGER", "BIGINT", "TINYINT", "BYTE", "SMALLINT", "FLOAT", "DOUBLE", "DOUBLE PRECISION"};

    protected final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    protected ReportVo report;
    protected SearchRequest request;
    protected SearchResponse response;
    protected String alias;

    public ReportAggregation(SearchRequest request, ReportVo report) {
        this.request = request;
        this.report = report;
        setAlias();
    }

    /**
     * 设置聚合别名
     */
    public abstract void setAlias();

    /**
     * 构建聚合器
     *
     * @return -
     */
    public abstract AggregationBuilder builder();

    /**
     * 获取聚合结果集
     *
     * @return -
     */
    public T getAggregations() {
        return response.getAggregations().get(alias);
    }

    /**
     * 解析为图表数据
     * @return -
     */
    public JSONObject parseEchartsItem(){
        ParsedMultiBucketAggregation histogram = getAggregations();
        JSONArray array = new JSONArray();
        histogram.getBuckets().forEach(bucket -> {
            String key = bucket.getKeyAsString();
            if(bucket instanceof ParsedDateHistogram.ParsedBucket || bucket instanceof ParsedAutoDateHistogram.ParsedBucket){
                DateTime time = (DateTime) bucket.getKey();
                Date date = time.toLocalDateTime().toDate();
                key = sdf.format(date);
            }
            JSONObject json = new JSONObject();
            json.put("name", StringUtils.isBlank(key) ? "未知" : key);
            json.put("value", bucket.getDocCount());
            array.add(json);
        });
        JSONObject json = new JSONObject();
        json.put("data", array);
        json.put("reportType", report.getReportType());
        return json;
    }

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

    /**
     * 执行搜索
     *
     * @param client ES高级客户端
     * @throws IOException -
     */
    public void search(RestHighLevelClient client) throws IOException {
        this.request.source().from(0).size(0).aggregation(builder());
        this.response = client.search(this.request, RequestOptions.DEFAULT);
    }

    /**
     * 根据统计报表参数返回不同类型的聚合
     *
     * @param request 搜索请求
     * @param report  统计报表参数
     * @return -
     */
    public static ReportAggregation getAggregation(SearchRequest request, ReportVo report) {
        if (Arrays.asList(dm_date_types).contains(report.getFieldXType())) { // 日期类型
            return new DateHistogramReportAggregation(request, report);
        } else if (Arrays.asList(dm_number_types).contains(report.getFieldXType())) { //数值类型
            return new NumberHistogramReportAggregation(request, report);
        } else {// 字符串类型
            return new StringTermsReportAggregation(request, report);
        }
    }
}
