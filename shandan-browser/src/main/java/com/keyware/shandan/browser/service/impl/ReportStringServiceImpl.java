package com.keyware.shandan.browser.service.impl;

import com.keyware.shandan.bianmu.service.MetadataService;
import com.keyware.shandan.browser.service.MetadataDataService;
import com.keyware.shandan.browser.service.ReportService;
import com.keyware.shandan.datasource.mapper.DynamicDatasourceMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 统计报表服务接口实现类
 *
 * @author GuoXin
 * @since 2021/12/20
 */
@Service
public class ReportStringServiceImpl extends ReportService {
    private final Map<String, String> formatter = new HashMap<>();

    public ReportStringServiceImpl(MetadataService metadataService, MetadataDataService metadataDataService, DynamicDatasourceMapper dynamicDatasourceMapper) {
        super(metadataService, metadataDataService, dynamicDatasourceMapper);
        formatter.put("year", "yyyy");
        formatter.put("month", "yyyy-MM");
        formatter.put("day", "yyyy-MM-dd");
        formatter.put("hour", "yyyy-MM-dd hh24");
        formatter.put("minute", "yyyy-MM-dd hh24:mm");
    }

    @Override
    protected String buildStatisticsSql(String querySql) {
        return "select " +
                TEMP_ALIAS_1 + ".\"" + report.getFieldX() + "\" as \"name\", " +
                report.getAggregationType() + "(" + TEMP_ALIAS_1 + ".\"" + report.getFieldY() + "\") as \"value\"" +
                " from (" + querySql + ") as " + TEMP_ALIAS_1 +
                " group by " + TEMP_ALIAS_1 + ".\"" + report.getFieldX() + "\"" +
                " order by " + TEMP_ALIAS_1 + ".\"" + report.getFieldX() + "\" asc";
    }
}
