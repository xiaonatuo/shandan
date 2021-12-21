package com.keyware.shandan.browser.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.service.MetadataService;
import com.keyware.shandan.browser.entity.ReportVo;
import com.keyware.shandan.browser.service.MetadataDataService;
import com.keyware.shandan.browser.service.ReportService;
import com.keyware.shandan.datasource.mapper.DynamicDatasourceMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 统计报表服务接口实现类
 *
 * @author GuoXin
 * @since 2021/12/20
 */
@Service
public class ReportNumberServiceImpl extends ReportService {

    public ReportNumberServiceImpl(MetadataService metadataService, MetadataDataService metadataDataService, DynamicDatasourceMapper dynamicDatasourceMapper) {
        super(metadataService, metadataDataService, dynamicDatasourceMapper);
    }

    @Override
    protected String buildStatisticsSql(String querySql) throws Exception {
        StringBuilder builder = new StringBuilder();
        String intervalSql = getIntervalSql(getMinMaxValues(querySql));
        builder.append("select ").append(TEMP_ALIAS_2).append(".\"name\", ");
        builder.append(report.getAggregationType()).append("(").append(TEMP_ALIAS_2).append(".\"").append(report.getFieldY()).append("\") as ").append("\"value\"");
        builder.append(" from (select *, ");
        builder.append(" (").append(intervalSql).append(") as \"name\"");
        builder.append(" from (").append(querySql).append(") as ").append(TEMP_ALIAS_1);
        builder.append(") as ").append(TEMP_ALIAS_2);
        builder.append(" group by ").append("\"name\"");
        builder.append(" order by ").append("\"name\" asc");
        return builder.toString();
    }

    protected String getIntervalSql(MinMaxVal minMaxVal) {
        List<MinMaxVal> intervals = getIntervalGroup(report.getNumberInterval(), minMaxVal);
        StringBuilder builder = new StringBuilder(" case");
        intervals.forEach(minMax -> {
            String alias = minMax.getMin() + "~" + minMax.getMax();
            builder.append(" when " + TEMP_ALIAS_1 + ".\"").append(report.getFieldY()).append("\" between ").append(minMax.getMin()).append(" and ").append(minMax.getMax()).append(" then '").append(alias).append("'");
        });
        return builder.append(" end ").toString();
    }

    @DS("#metadata.dataSourceId")
    protected MinMaxVal getMinMaxValues(String querySql) throws Exception {
        String sql = "select min(" + report.getFieldY() + ") as \"min\", max(" + report.getFieldY() + ") as \"max\" from (" + querySql + ") T";
        List<Map<String, Object>> list = dynamicDatasourceMapper.list(sql);
        if (list.size() == 0) {
            throw new Exception("聚合字段中没有可用数据");
        }
        Map<String, Object> minMax = list.get(0);
        if (minMax.get("min") == null && minMax.get("max") == null) {
            throw new Exception("聚合字段中没有可用数据");
        }
        return new MinMaxVal(minMax.get("min"), minMax.get("max"));
    }

    protected List<MinMaxVal> getIntervalGroup(Integer interval, MinMaxVal minMaxVal) {
        List<MinMaxVal> group = new ArrayList<>();
        int offset = 0;
        do {
            int min = offset;
            offset += interval;
            group.add(new MinMaxVal(min, offset));
        } while (offset <= ((Number) minMaxVal.getMax()).intValue());
        return group;
    }

}
