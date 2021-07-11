package com.keyware.shandan;

import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.system.entity.SysFile;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.metrics.valuecount.ParsedValueCount;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

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

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Test
    public void test() {
        SearchRequest request = Requests.searchRequest("shandan");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.size(0).aggregation( AggregationBuilders.dateHistogram("inputDate").field("inputDate").dateHistogramInterval(DateHistogramInterval.DAY).subAggregation(AggregationBuilders.count("count").field("source")));
        request.source(builder);
        try {
            SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
            Aggregations aggs = response.getAggregations();
            ParsedDateHistogram terms = aggs.get("inputDate");

            terms.getBuckets().forEach(bucket -> {
                DateTime time = (DateTime) bucket.getKey();
                Date date = time.toLocalDateTime().toDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                System.out.println(sdf.format(date) + "," + bucket.getDocCount());
                ParsedValueCount count = bucket.getAggregations().get("count");


                /*bucket.getAggregations().asList().forEach(item -> {

                    System.out.println(item.getType() + "," + item.getName() + "," + JSONObject.toJSON(item.getMetaData()));
                });*/
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
