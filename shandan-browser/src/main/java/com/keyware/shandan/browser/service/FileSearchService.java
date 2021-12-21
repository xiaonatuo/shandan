package com.keyware.shandan.browser.service;

import com.keyware.shandan.bianmu.es.repository.EsSysFileRepository;
import com.keyware.shandan.browser.entity.PageVo;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.system.entity.SysFile;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * FileSearchService
 *
 * @author Administrator
 * @since 2021/12/21
 */
@Service
public class FileSearchService {

    @Autowired
    private EsSysFileRepository fileRepository;

    @Autowired
    private RestHighLevelClient esClient;

    public PageVo searchFile(PageVo page, String text, String metaId) throws IOException {
        SearchRequest request = Requests.searchRequest("shandan");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(page.getPage() * page.getSize() - page.getSize()).size(page.getSize());

        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(QueryBuilders.termQuery("entityId", metaId));
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

        // 高亮显示设置
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.requireFieldMatch(true);
        //下面这两项,如果你要高亮如文字内容等有很多字的字段,必须配置,不然会导致高亮不全,文章内容缺失等
        //builder.fragmentSize(20); //最大高亮分片数
        //builder.numOfFragments(0); //从第一个分片获取高亮片段
        highlightBuilder.preTags("<label style=\"color:red\">").postTags("</label>").field("*");
        highlightBuilder.forceSource(true);
        searchSourceBuilder.query(builder).highlighter(highlightBuilder);
        request.source(searchSourceBuilder);

        /*Page<SysFile> filePage = fileRepository.search(builder, PageRequest.of(page.getPage() - 1, page.getSize()));
        PageVo result = new PageVo();
        result.setSize(page.getSize());
        result.setPage(page.getPage());
        result.setTotal(filePage.getTotalElements());
        result.setPageTotal(filePage.getTotalPages());
        result.setRecords(filePage.getContent().stream().map(file -> (Object) file).collect(Collectors.toList()));
        return result;*/
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        return PageVo.ofSearchHits(response.getHits(), page);
    }
}
