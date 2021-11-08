package com.keyware.shandan.browser.service.builders;

import com.keyware.shandan.browser.entity.ViewVo;
import com.keyware.shandan.browser.enums.ViewType;
import com.keyware.shandan.common.util.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.ParsedMultiBucketAggregation;

import java.io.IOException;
import java.util.Objects;

/**
 * 视图聚合桶抽象类
 */
public abstract class ViewAggregation {
    protected SearchRequest request;
    protected SearchResponse response;
    protected String alias;
    protected String field;
    private final ViewType type;

    private final RestHighLevelClient client;

    public ViewAggregation(SearchRequest request, RestHighLevelClient client) {
        this.request = request;
        this.client = client;
        this.alias = setAlias();
        this.type = setType();
        setField();
    }

    /**
     * 设置聚合别名
     */
    public abstract String setAlias();

    /**
     * 设置聚合字段
     */
    public void setField(){
        this.field = this.type.getField();
    }

    public abstract ViewType setType();
    public ViewType getType() {
        return this.type;
    }

    /**
     * 构建聚合器
     *
     * @return -
     */
    protected AggregationBuilder builder() {
        return AggregationBuilders.terms(alias).field(field+".keyword");
    }

    /**
     * 构建子聚合
     *
     * @param builder -
     */
    protected void subAggregation(AggregationBuilder builder) {
        builder.subAggregation(AggregationBuilders.count(field + "_count").field(field));
    }

    /**
     * 执行搜索
     *
     * @throws IOException -
     */
    public SearchResponse search() throws IOException {
        this.request.source().from(0).size(0).aggregation(builder());
        this.response = client.search(this.request, RequestOptions.DEFAULT);
        return this.response;
    }

    /**
     * 解析为图表数据
     *
     * @return -
     */
    public ViewVo<ViewVo.View> aggregation() throws IOException {
        if(Objects.isNull(this.response)){
            search();
        }
        ParsedMultiBucketAggregation histogram = response.getAggregations().get(alias);

        ViewVo<ViewVo.View> view = new ViewVo<>(this.type);
        histogram.getBuckets().forEach(bucket -> {
            String key = bucket.getKeyAsString();
            if(StringUtils.isNotBlank(key)){
                view.addView(ViewVo.newView(key, key, bucket.getDocCount()));
            }else{
                view.addView(ViewVo.newView("none", "其他", bucket.getDocCount()));
            }
        });
        return view;
    }

}
