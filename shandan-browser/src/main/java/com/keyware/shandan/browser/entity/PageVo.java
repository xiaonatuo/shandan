package com.keyware.shandan.browser.entity;

import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.browser.config.BianmuDataCache;
import com.keyware.shandan.common.util.StringUtils;
import lombok.Data;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分页实体
 *
 * @author Administrator
 * @since 2021/7/5
 */
@Data
public class PageVo implements Serializable {
    private static final long serialVersionUID = 7558239023655425729L;

    /**
     * 当前页
     */
    private int page = 1;

    /**
     * 页面容量
     */
    private int size = 10;

    /**
     * 记录总数
     */
    private long total;

    /**
     * 页面总数
     */
    private int pageTotal;

    /**
     * 记录数
     */
    private List<Object> records;

    /**
     * 根据ES检索结果设置分页数据
     *
     * @param datas -
     */
    public static PageVo ofSearchHits(SearchHits datas, PageVo page) {
        PageVo vo = new PageVo();
        vo.page = page.page;
        vo.size = page.size;
        vo.total = datas.getTotalHits();
        vo.pageTotal = (int) Math.ceil(vo.total / (double) page.size);
        vo.records = Arrays.stream(datas.getHits()).map(PageVo::fillMetadata).collect(Collectors.toList());
        //vo.records = Arrays.asList(datas.getHits());
        return vo;
    }

    private static Map<String, Object> fillMetadata(SearchHit hit) {
        Map<String, Object> source = hit.getSourceAsMap();
        // 数据实体类型
        String type = (String)source.getOrDefault("entityType", "");
        String tableComment = BianmuDataCache.getComment(type);
        JSONObject tableColumns = BianmuDataCache.getColumns(type);
        //解析高亮字段
        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        for (Map.Entry<String, HighlightField> entry : highlightFields.entrySet()) {
            Text[] fragments = entry.getValue().fragments();
            StringBuilder n_field = new StringBuilder();
            for (Text fragment : fragments) {
                n_field.append(fragment);
            }
            //高亮标题覆盖原标题
            source.put(entry.getKey(), n_field.toString());
        }
        String title = "";
        if(StringUtils.isNotBlank(type)){
            title = tableComment + type;
        }
        source.put("title", tableComment + type);
        return source;
    }
}
