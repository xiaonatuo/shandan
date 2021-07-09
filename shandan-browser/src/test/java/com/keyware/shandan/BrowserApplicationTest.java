package com.keyware.shandan;

import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * BrowserApplicationTest
 *
 * @author Administrator
 * @since 2021/7/8
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BrowserApplicationTest {

    @Autowired
    private RestHighLevelClient esClient;

    @Test
    public void test() {
        SearchRequest request = Requests.searchRequest("shandan");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.size(0).aggregation(AggregationBuilders.terms("by_source").field("source.keyword"));
        request.source(builder);
        try {
            SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
            Aggregations aggs = response.getAggregations();
            ParsedStringTerms terms = aggs.get("by_source");

            terms.getBuckets().forEach(bucket -> {
                System.out.println(bucket.getKeyAsString() + "," + bucket.getDocCount());
                bucket.getAggregations().asList().forEach(item -> {
                    System.out.println(item.getType() + "," + item.getName() + "," + JSONObject.toJSON(item.getMetaData()));
                });
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
