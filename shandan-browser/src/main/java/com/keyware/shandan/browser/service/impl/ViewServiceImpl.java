package com.keyware.shandan.browser.service.impl;

import com.keyware.shandan.browser.entity.ViewVo;
import com.keyware.shandan.browser.service.ViewService;
import com.keyware.shandan.browser.service.builders.impl.ViewAggregationEquipment;
import com.keyware.shandan.browser.service.builders.impl.ViewAggregationTask;
import com.keyware.shandan.browser.service.builders.impl.ViewAggregationTroop;
import com.keyware.shandan.browser.service.builders.impl.ViewAggregationYear;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ViewServiceImpl implements ViewService {
    private final static String index = "shandan";

    @Autowired
    private RestHighLevelClient esClient;

    @Autowired
    private SearchServiceImpl searchService;

    /**
     * 根据数据公共属性中的任务时间，聚合统计年度分组
     *
     * @return 年度视图
     */
    @Override
    public ViewVo getYearViews() throws IOException {
        ViewAggregationYear viewAgg = new ViewAggregationYear(buildRequest(), esClient);
        return viewAgg.aggregation();
    }

    /**
     * @return 部队编号视图
     */
    @Override
    public ViewVo getTroopViews() throws IOException {
        ViewAggregationTroop viewAgg = new ViewAggregationTroop(buildRequest(), esClient);
        return viewAgg.aggregation();
    }

    /**
     * @return 装备型号视图
     */
    @Override
    public ViewVo getEquipmentViews() throws IOException {
        ViewAggregationEquipment viewAgg = new ViewAggregationEquipment(buildRequest(), esClient);
        return viewAgg.aggregation();
    }

    /**
     * @return 任务代号视图
     */
    @Override
    public ViewVo getTaskViews() throws IOException {
        ViewAggregationTask viewAgg = new ViewAggregationTask(buildRequest(), esClient);
        return viewAgg.aggregation();
    }

    /**
     * @return 部门视图
     */
    @Override
    public ViewVo getOrgViews() {
        return null;
    }

    /**
     * 构建搜索请求
     *
     * @return 搜索请求
     */
    public SearchRequest buildRequest() {
        SearchRequest request = Requests.searchRequest(index);
        SearchSourceBuilder searchBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        String[] metaIds = searchService.getAccessibleMetadata();
        if (metaIds != null && metaIds.length > 0) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("META_TYPE.keyword", metaIds));
        }
        String[] entityIds = searchService.getAccessibleDirectory();
        if (entityIds != null && entityIds.length > 0) {
            boolQueryBuilder.should(QueryBuilders.termsQuery("entityId", entityIds));
        }

        searchBuilder.query(boolQueryBuilder);
        request.source(searchBuilder);
        return request;
    }

}
