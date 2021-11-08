package com.keyware.shandan.browser.service.builders.impl;

import com.keyware.shandan.browser.entity.ViewVo;
import com.keyware.shandan.browser.enums.ViewType;
import com.keyware.shandan.browser.service.builders.ViewAggregation;
import com.keyware.shandan.common.util.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.ParsedMultiBucketAggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.joda.time.DateTime;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ViewAggregationYear extends ViewAggregation {
    private static final String alias = "year_histogram";
    private static final ViewType type = ViewType.year;

    public ViewAggregationYear(SearchRequest request, RestHighLevelClient client) {
        super(request, client);
    }

    @Override
    public String setAlias() {
        return alias;
    }

    @Override
    public ViewType setType() {
        return type;
    }

    @Override
    protected AggregationBuilder builder() {
        DateHistogramAggregationBuilder builder = AggregationBuilders.dateHistogram(alias).field(field);
        builder.dateHistogramInterval(DateHistogramInterval.YEAR);
        subAggregation(builder);
        return builder;
    }

    /**
     * 解析为图表数据
     *
     * @return -
     */
    @Override
    public ViewVo<ViewVo.View> aggregation() throws IOException {
        if(Objects.isNull(this.response)){
            search();
        }
        ParsedMultiBucketAggregation histogram = response.getAggregations().get(alias);
        ViewVo<ViewVo.View> view = new ViewVo<>(this.getType());
        histogram.getBuckets().forEach(bucket -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            DateTime time = (DateTime) bucket.getKey();
            Date date = time.toLocalDateTime().toDate();
            String key = sdf.format(date);

            if(StringUtils.isNotBlank(key)){
                view.addView(ViewVo.newView(key, key+"年度", bucket.getDocCount()));
            }
        });
        return view;
    }
}
