package com.keyware.shandan.browser.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.bianmu.entity.DirectoryVo;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.service.DirectoryService;
import com.keyware.shandan.bianmu.service.MetadataService;
import com.keyware.shandan.browser.entity.SearchConditionVo;
import com.keyware.shandan.browser.entity.PageVo;
import com.keyware.shandan.browser.entity.ReportVo;
import com.keyware.shandan.browser.enums.ConditionLogic;
import com.keyware.shandan.browser.service.SearchService;
import com.keyware.shandan.browser.service.builders.ReportAggregation;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.system.entity.SysUser;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 检索服务实现类
 *
 * @author Administrator
 * @since 2021/7/1
 */
@Service
public class SearchServiceImpl implements SearchService {

    private final String search_index = "shandan";
    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private RestHighLevelClient esClient;


    /**
     * 全文检索
     *
     * @param condition 条件
     * @return -
     */
    @Override
    public PageVo esSearch(SearchConditionVo condition) throws IOException {
        SearchRequest request = buildRequest(search_index, condition);
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        return PageVo.ofSearchHits(response.getHits(), condition);
    }

    /**
     * 统计报表聚合查询
     *
     * @param report 统计类型
     * @return -
     * @throws IOException
     */
    @Override
    public JSONObject report(ReportVo report) throws IOException {
        SearchConditionVo condition = new SearchConditionVo();
        condition.setConditions(report.getConditions());
        SearchRequest request = buildRequest(search_index, condition);

        ReportAggregation aggregation = ReportAggregation.getAggregation(request, report);
        aggregation.search(esClient);

        return aggregation.parseEchartsItem();
    }

    /**
     * 构建请求
     *
     * @param index     索引
     * @param condition 条件
     * @return -
     */
    private SearchRequest buildRequest(String index, SearchConditionVo condition) {
        SearchRequest request = Requests.searchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        setPage(searchSourceBuilder, condition);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        List<SearchConditionVo.Item> conditionItems = condition.getConditions();

        boolean metaFlag = false;
        for (SearchConditionVo.Item item : conditionItems) {
            if (StringUtils.isBlank(item.getFieldValue().trim())) {
                continue;
            }
            // 文本框
            if ("searchInput".equals(item.getFieldName())) {

                boolQueryBuilder.must(QueryBuilders.queryStringQuery(item.getFieldValue()));
            } else if ("INPUTDATE".equals(item.getFieldName())) {
                String[] dates = item.getFieldValue().split("至");
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date begin = sdf.parse(dates[0].trim() + " 00:00:00");
                    Date end = sdf.parse(dates[1].trim() + " 23:59:59");
                    boolQueryBuilder.must(QueryBuilders.rangeQuery(item.getFieldName()).gte(begin.getTime()).lte(end.getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if ("metadataId".equals(item.getFieldName())) {
                MetadataBasicVo metadata = metadataService.getById(item.getFieldValue());
                boolQueryBuilder.filter(QueryBuilders.termsQuery("META_TYPE.keyword", metadata.getMetadataName()));
                metaFlag = true;
            } else if ("directoryId".equals(item.getFieldName()) && !"-".equals(item.getFieldName())) {
                metaFlag = true;
                // 当条件包含目录时，需要设置查询ES类型，即对应编目数据中的数据资源表
                BoolQueryBuilder bool = QueryBuilders.boolQuery();
                bool.should(QueryBuilders.termsQuery("META_TYPE.keyword", getMetadataIdsByDirId(item.getFieldValue())));
                //boolQueryBuilder.filter(QueryBuilders.termsQuery("META_TYPE.keyword", getMetadataIdsByDirId(item.getValue())));

                //因为目录中也包含file类型，所以需要单独对file类型的数据做过滤
                String[] dirids = getDirectoryAllChildIds(item.getFieldValue());
                if (dirids != null) {
                    BoolQueryBuilder mustQuery = QueryBuilders.boolQuery();
                    mustQuery.must(QueryBuilders.termsQuery("entityId", dirids));
                    mustQuery.must(QueryBuilders.termsQuery("META_TYPE.keyword", "file"));
                    bool.should(mustQuery);

                }
                boolQueryBuilder.should(bool);

            } else {
                if (item.getLogic() == ConditionLogic.eq) {
                    boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(item.getFieldName(), item.getFieldValue()));
                } else if (item.getLogic() == ConditionLogic.nq) {
                    boolQueryBuilder.mustNot(QueryBuilders.matchPhraseQuery(item.getFieldName(), item.getFieldValue()));
                } else if (item.getLogic() == ConditionLogic.like) {
                    boolQueryBuilder.must(QueryBuilders.fuzzyQuery(item.getFieldName(), item.getFieldValue()));
                } else if (item.getLogic() == ConditionLogic.gt) {
                    boolQueryBuilder.must(QueryBuilders.rangeQuery(item.getFieldName()).gt(item.getFieldValue()));
                } else if (item.getLogic() == ConditionLogic.lt) {
                    boolQueryBuilder.must(QueryBuilders.rangeQuery(item.getFieldName()).lt(item.getFieldValue()));
                }
            }
        }
        if (!metaFlag) {
            String[] metaIds = getAccessibleMetadata();
            if (metaIds != null && metaIds.length > 0) {
                boolQueryBuilder.filter(QueryBuilders.termsQuery("META_TYPE.keyword", metaIds));
            }
            String[] entityIds = getAccessibleDirectory();
            if (entityIds != null && entityIds.length > 0) {
                boolQueryBuilder.should(QueryBuilders.termsQuery("entityId", entityIds));
            }
        }
        searchSourceBuilder.query(boolQueryBuilder).highlighter(buildHighlightBuilder());
        if (StringUtils.isNotBlank(condition.getSortFiled())) {
            searchSourceBuilder.sort(condition.getSortFiled(), condition.getOrder());
        }
        return request.source(searchSourceBuilder);
    }

    /**
     * 数据命中高亮配置
     *
     * @return -
     */
    private HighlightBuilder buildHighlightBuilder() {
        // 高亮显示设置
        HighlightBuilder builder = new HighlightBuilder();
        builder.requireFieldMatch(true);
        //下面这两项,如果你要高亮如文字内容等有很多字的字段,必须配置,不然会导致高亮不全,文章内容缺失等
        //builder.fragmentSize(20); //最大高亮分片数
        //builder.numOfFragments(0); //从第一个分片获取高亮片段
        builder.preTags("<label style=\"color:red\">").postTags("</label>").field("*");
        return builder.forceSource(true);
    }

    /**
     * 设置分页
     *
     * @param builder -
     * @param vo      -
     */
    private void setPage(SearchSourceBuilder builder, SearchConditionVo vo) {
        int page = vo.getPage();
        int size = vo.getSize();
        builder.from(page * size - size).size(size);
    }

    /**
     * 找到目录下所有的数据资源表
     *
     * @param dirId 目录ID
     * @return -
     */
    private String[] getMetadataIdsByDirId(String dirId) {
        List<MetadataBasicVo> list = directoryService.directoryAllMetadata(dirId);
        String[] metadataNames = list.stream().map(MetadataBasicVo::getMetadataName).collect(Collectors.toList()).toArray(new String[list.size()]);
       // metadataNames[list.size()] = "file";
        return metadataNames;
    }

    /**
     * 获取目录的所有子级目录ID
     *
     * @param dirId 目录ID
     * @return
     */
    private String[] getDirectoryAllChildIds(String dirId) {
        DirectoryVo vo = directoryService.getById(dirId);
        if (vo == null) {
            return null;
        }
        QueryWrapper<DirectoryVo> wrapper = new QueryWrapper<>();
        wrapper.like("DIRECTORY_PATH", vo.getDirectoryName());
        List<DirectoryVo> list = directoryService.list(wrapper);
        String[] ids = new String[list.size() + 1];
        ids[0] = dirId;
        for (int i = 1; i <= list.size(); i++) {
            ids[i] = list.get(i - 1).getId();
        }
        return ids;
    }

    /**
     * 获取有访问权限的数据资源信息
     *
     * @return
     */
    public String[] getAccessibleMetadata() {
        SysUser user = SecurityUtil.getLoginSysUser();
        if (user != null && ("sa".equals(user.getLoginName()) || "admin".equals(user.getLoginName()))) {
            List<MetadataBasicVo> list = metadataService.list();
            if(list.size() > 0){
                List<String> ids = list.stream().map(MetadataBasicVo::getMetadataName).collect(Collectors.toList());
                ids.add("file");
                return ids.toArray(new String[ids.size()]);
            }else{
                return new String[]{"-"};
            }
        }
        List<MetadataBasicVo> metadataList = metadataService.list(new QueryWrapper<>());
        String[] metadataNames = metadataList.stream().map(MetadataBasicVo::getMetadataName).collect(Collectors.toList()).toArray(new String[metadataList.size() + 1]);
        metadataNames[metadataList.size()] = "file";
        return metadataNames;
    }

    /**
     * 获取有访问权限的目录信息
     *
     * @return
     */
    public String[] getAccessibleDirectory() {
        SysUser user = SecurityUtil.getLoginSysUser();
        if (user != null && ("sa".equals(user.getLoginName()) || "admin".equals(user.getLoginName()))) {
            return Strings.EMPTY_ARRAY;
        }
        List<DirectoryVo> dirList = directoryService.list(new QueryWrapper<>());
        return dirList.stream().map(DirectoryVo::getId).collect(Collectors.toList()).toArray(new String[dirList.size()]);
    }
}
