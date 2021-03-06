package com.keyware.shandan.browser.service;

import com.keyware.shandan.browser.entity.PageVo;
import com.keyware.shandan.common.util.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * FileSearchService
 *
 * @author Administrator
 * @since 2021/12/21
 */
@Service
public class FileSearchService {

    @Autowired
    private RestHighLevelClient esClient;

    public PageVo searchFile(PageVo page, String text, String metaId) throws IOException {
        SearchRequest request = Requests.searchRequest("shandan");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(page.getPage() * page.getSize() - page.getSize()).size(page.getSize());

        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        if(!"-".equals(metaId)){
            builder.must(QueryBuilders.termQuery("entityId", metaId));
        }
        if (StringUtils.isNotBlank(text)) {
            BoolQueryBuilder should = QueryBuilders.boolQuery();
            should.should(QueryBuilders.matchQuery("fileName", text));
            should.should(QueryBuilders.matchQuery("entryStaff", text));
            should.should(QueryBuilders.matchQuery("equipmentModel", text));
            should.should(QueryBuilders.matchQuery("source", text));
            should.should(QueryBuilders.matchQuery("taskCode", text));
            should.should(QueryBuilders.matchQuery("taskNature", text));
            should.should(QueryBuilders.matchQuery("troopCode", text));
            should.should(QueryBuilders.matchQuery("targetNumber", text));
            should.should(QueryBuilders.matchQuery("missileNumber", text));
            builder.must(should);
        }

        // ??????????????????
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.requireFieldMatch(true);
        //???????????????,?????????????????????????????????????????????????????????,????????????,???????????????????????????,?????????????????????
        //builder.fragmentSize(20); //?????????????????????
        //builder.numOfFragments(0); //????????????????????????????????????
        highlightBuilder.preTags("<label style=\"color:red\">").postTags("</label>").field("*");
        highlightBuilder.forceSource(true);
        searchSourceBuilder.query(builder).highlighter(highlightBuilder);
        request.source(searchSourceBuilder);

        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        return PageVo.ofSearchHits(response.getHits(), page);
    }
}
