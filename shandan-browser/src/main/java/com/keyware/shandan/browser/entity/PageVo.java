package com.keyware.shandan.browser.entity;

import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.browser.config.BianmuDataCache;
import com.keyware.shandan.browser.constants.CommonFields;
import com.keyware.shandan.common.util.DateUtil;
import com.keyware.shandan.common.util.StringUtils;
import lombok.Data;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
        return vo;
    }

    /**
     * 对搜索结果填充元数据
     *
     * @param hit -
     * @return -
     */
    private static Map<String, Object> fillMetadata(SearchHit hit) {
        SearchResultRow resultRow = new SearchResultRow();
        Map<String, Object> source = hit.getSourceAsMap();
        resultRow.putAll(source);

        // 数据类型,具体对应实际数据中的表名或者文件类型
        Object metaTypeObj = source.get("META_TYPE");
        if(metaTypeObj == null){
            return source;
        }
        // 元数据表类型
        String metaType = metaTypeObj.toString();
        //表注释
        String tableComment = BianmuDataCache.getComment(metaType);
        resultRow.put("tableComment", tableComment);
        //表字段配置
        resultRow.put("columns", BianmuDataCache.getColumns(metaType));

        // 时间戳类型处理
        if(source.get("INPUTDATE") != null){
            long date = (long) source.get("INPUTDATE");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            resultRow.put("INPUTDATE", sdf.format(new Date(date)));
        }

        // 设置标题
        /*if (!metaType.equals("file")) {
            if (StringUtils.isNotBlank(tableComment)) {
                tableComment += "|";
            }
            source.put("title", tableComment + metaType);
        }else{
            source.put("title", source.get("fileName"));
        }

        // 设置公共字段注释
        StringBuilder commonText = new StringBuilder();
        for (Map.Entry<String, String> entry : CommonFields.entrySet()) {
            if (entry.getKey() != null) {
                Object value = source.get(entry.getKey());
                if(value == null){
                    value = source.get(entry.getKey().toUpperCase());
                }
                commonText.append("<label style=\"font-weight: bold;\">").append(entry.getValue()).append("</label>:").append(value == null ? "未知" : value).append(";");
            }
        }
        source.put("commonText", commonText);*/

        // 对大文本text字段长度进行预处理，最大长度不超过200个字符
        /*if(source.get("text") != null){
            String text = (String) source.get("text");
            if(text.length() > 200){
                source.put("text", text.substring(0, 200));
            }
        }*/

        //解析高亮字段
        /*Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        for (Map.Entry<String, HighlightField> entry : highlightFields.entrySet()) {
            //高亮标题覆盖原标题
            source.put(entry.getKey(), fragmentText(entry.getValue()));
        }*/
        return resultRow;
    }

    /**
     * 拼接命中高亮文本
     *
     * @param field 命中字段
     * @return 文本
     */
    private static String fragmentText(HighlightField field) {
        StringBuilder text = new StringBuilder();
        for (Text fragment : field.getFragments()) {
            text.append(fragment);

            // 如果是大文本，判断长度
            if(field.getName().equals("text")){
                text.append(";");
                int size = text.toString().replace("<label style=\"color:red\">", "").replace("</label>", "").length();
                if(size > 150){
                    break;
                }
            }
        }
        return text.toString();
    }
}
