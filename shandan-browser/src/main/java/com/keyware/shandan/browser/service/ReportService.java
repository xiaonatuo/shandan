package com.keyware.shandan.browser.service;

import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.service.MetadataService;
import com.keyware.shandan.browser.entity.ReportVo;
import com.keyware.shandan.browser.entity.SearchConditionVo;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.datasource.mapper.DynamicDatasourceMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计报表服务接口
 *
 * @author GuoXin
 * @since 2021/12/20
 */
public abstract class ReportService {
    protected final MetadataService metadataService;
    protected final MetadataDataService metadataDataService;
    protected final DynamicDatasourceMapper dynamicDatasourceMapper;

    public final static List<String> STRING_TYPES = Arrays.asList(SearchConditionVo.STRING_TYPES);
    public final static List<String> NUMBER_TYPES = Arrays.asList(SearchConditionVo.NUMBER_TYPES);
    public final static List<String> DATE_TYPES = Arrays.asList(SearchConditionVo.DATE_TYPES);

    protected final String TEMP_ALIAS_1 = "\"T1\"";
    protected final String TEMP_ALIAS_2 = "\"T2\"";

    protected MetadataBasicVo metadata;
    protected ReportVo report;

    public ReportService(MetadataService metadataService, MetadataDataService metadataDataService, DynamicDatasourceMapper dynamicDatasourceMapper) {
        this.metadataService = metadataService;
        this.metadataDataService = metadataDataService;
        this.dynamicDatasourceMapper = dynamicDatasourceMapper;
    }

    /**
     * @param condition 条件参数
     * @return 条件查询的sql语句
     * @throws Exception -
     */
    protected String getConditionsQuerySql(SearchConditionVo condition) throws Exception {
        return metadataDataService.getQuerySql(metadata, condition);
    }

    /**
     * 获取统计sql
     *
     * @return 统计sql语句
     * @throws Exception -
     */
    protected String getStatisticsSql() throws Exception {
        if (StringUtils.isBlank(report.getMetadataId())) {
            throw new Exception("数据资源ID不可用");
        }
        metadata = metadataService.get(report.getMetadataId()).getData();
        if (metadata == null) {
            throw new Exception("数据资源ID不可用");
        }

        SearchConditionVo condition = new SearchConditionVo();
        condition.setConditions(report.getConditions());
        String querySql = getConditionsQuerySql(condition);
        return buildStatisticsSql(querySql);
    }

    /**
     * 开始统计查询
     *
     * @param report 统计参数
     * @return 统计数据
     * @throws Exception -
     */
    public Map<String, Object> statisticsQuery(ReportVo report) throws Exception {
        this.report = report;
        String statisticSql = getStatisticsSql();
        List<Map<String, Object>> result = dynamicDatasourceMapper.list(statisticSql);

        return parseEchartsResult(result);
    }


    /**
     * 生成统计sql语句
     *
     * @param sql    数据查询sql
     * @return 统计sql语句
     */
    protected abstract String buildStatisticsSql(String sql) throws Exception;


    /*protected void buildCountStatisticsSql(StringBuilder builder, String querySql) {
        builder.append("select ").append(TEMP_ALIAS_1).append(".\"").append(report.getFieldX()).append("\" as \"name\", ");
        builder.append(report.getAggregationType()).append("(");
        builder.append(TEMP_ALIAS_1).append(".\"").append(report.getFieldY()).append("\") as \"value\"");
        builder.append(" from (").append(querySql).append(") as ").append(TEMP_ALIAS_1);
        builder.append(" group by ");
        builder.append(TEMP_ALIAS_1).append(".\"").append(report.getFieldX()).append("\"");
    }*/

    /**
     * 生成按指定数据范围统计的sql语句
     *
     * @param builder  sql builder
     * @param querySql 查询sql
     */
    //protected abstract void buildIntervalStatisticsSql(StringBuilder builder, String querySql) throws Exception;

    @Getter
    @Setter
    public static class MinMaxVal {
        private Object min;
        private Object max;

        public MinMaxVal(Object min, Object max) {
            this.min = min;
            this.max = max;
        }
    }

    protected Map<String, Object> parseEchartsResult(List<Map<String, Object>> source) {
        Map<String, Object> result = new HashMap<>();
        result.put("reportType", report.getReportType());
        result.put("data", source);
        return result;
    }
}
