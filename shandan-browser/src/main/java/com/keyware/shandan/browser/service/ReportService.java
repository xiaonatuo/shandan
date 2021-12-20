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

    protected final List<String> STRING_TYPES = Arrays.asList(SearchConditionVo.STRING_TYPES);
    protected final List<String> NUMBER_TYPES = Arrays.asList(SearchConditionVo.NUMBER_TYPES);
    protected final List<String> DATE_TYPES = Arrays.asList(SearchConditionVo.DATE_TYPES);

    protected final String TEMP_ALIAS_1 = "\"T1\"";
    protected final String TEMP_ALIAS_2 = "\"T2\"";

    protected MetadataBasicVo metadata;

    public ReportService(MetadataService metadataService, MetadataDataService metadataDataService, DynamicDatasourceMapper dynamicDatasourceMapper) {
        this.metadataService = metadataService;
        this.metadataDataService = metadataDataService;
        this.dynamicDatasourceMapper = dynamicDatasourceMapper;
    }

    /**
     * @param metadata  数据资源实体
     * @param condition 条件参数
     * @return 条件查询的sql语句
     * @throws Exception -
     */
    protected String getConditionsQuerySql(MetadataBasicVo metadata, SearchConditionVo condition) throws Exception {
        return metadataDataService.getQuerySql(metadata, condition);
    }

    /**
     * 获取统计sql
     *
     * @param report 统计参数
     * @return 统计sql语句
     * @throws Exception -
     */
    protected String getStatisticsSql(ReportVo report) throws Exception {
        if (StringUtils.isBlank(report.getMetadataId())) {
            throw new Exception("数据资源ID不可用");
        }
        metadata = metadataService.get(report.getMetadataId()).getData();
        if (metadata == null) {
            throw new Exception("数据资源ID不可用");
        }

        SearchConditionVo condition = new SearchConditionVo();
        condition.setConditions(report.getConditions());
        String querySql = getConditionsQuerySql(metadata, condition);
        return buildStatisticsSql(report, querySql);
    }

    /**
     * 开始统计查询
     *
     * @param report 统计参数
     * @return 统计数据
     * @throws Exception -
     */
    public List<Map<String, Object>> statisticsQuery(ReportVo report) throws Exception {
        String statisticSql = getStatisticsSql(report);
        List<Map<String, Object>> result = dynamicDatasourceMapper.list(statisticSql);

        return result;
    }


    /**
     * 生成统计sql语句
     *
     * @param report 统计参数
     * @param sql    数据查询sql
     * @return 统计sql语句
     * @throws Exception -
     */
    protected String buildStatisticsSql(ReportVo report, String sql) throws Exception {
        StringBuilder builder = new StringBuilder();
        if (NUMBER_TYPES.contains(report.getFieldXType()) || DATE_TYPES.contains(report.getFieldXType())) {
            buildIntervalStatisticsSql(report, builder, sql);
        } else {
            buildCountStatisticsSql(report, builder, sql);
        }
        return builder.toString();
    }


    protected void buildCountStatisticsSql(ReportVo report, StringBuilder builder, String querySql) {
        if (StringUtils.isNotBlank(report.getFieldXTable())) {
            builder.append(" \"").append(report.getFieldXTable()).append("\".\"").append(report.getFieldX()).append("\"");
        } else {
            builder.append("\"").append(report.getFieldX()).append("\"");
        }

        builder.append(" \"").append(report.getAggregationType()).append("(");
        if (StringUtils.isNotBlank(report.getFieldYTable())) {
            builder.append("\"").append(report.getFieldYTable()).append("\".\"").append(report.getFieldY()).append("\")");
        } else {
            builder.append("\"").append(report.getFieldY()).append("\")");
        }
        builder.append(" as \"").append(report.getAggregationText()).append("\"");
        builder.append(" from (").append(querySql).append(") as temp");
        builder.append(" group by ");
        if (StringUtils.isNotBlank(report.getFieldXTable())) {
            builder.append(" \"").append(report.getFieldXTable()).append("\".\"").append(report.getFieldX()).append("\"");
        } else {
            builder.append("\"").append(report.getFieldX()).append("\"");
        }
    }

    /**
     * 生成按指定数据范围统计的sql语句
     *
     * @param report   统计参数
     * @param builder  sql builder
     * @param querySql 查询sql
     */
    protected abstract void buildIntervalStatisticsSql(ReportVo report, StringBuilder builder, String querySql) throws Exception;

    /**
     * @return 按指定数据范围间隔统计的sql语句片段
     */
    protected abstract String getIntervalSql(ReportVo report, MinMaxVal minMaxVal);

    /**
     * 范围统计时，获取统计字段的最大值和最小值
     *
     * @param metadata 数据资源实体
     * @param report   统计参数
     * @return 最小值和最大值
     * @throws Exception -
     */
    protected abstract MinMaxVal getMinMaxValues(MetadataBasicVo metadata, ReportVo report, String querySql) throws Exception;

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
}
