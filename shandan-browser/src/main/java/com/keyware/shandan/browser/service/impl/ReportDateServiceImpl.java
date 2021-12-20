package com.keyware.shandan.browser.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.keyware.shandan.bianmu.service.MetadataService;
import com.keyware.shandan.browser.service.MetadataDataService;
import com.keyware.shandan.browser.service.ReportService;
import com.keyware.shandan.datasource.mapper.DynamicDatasourceMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计报表服务接口实现类
 *
 * @author GuoXin
 * @since 2021/12/20
 */
@Service
public class ReportDateServiceImpl extends ReportService {
    private final Map<String, String> formatter = new HashMap<>();

    public ReportDateServiceImpl(MetadataService metadataService, MetadataDataService metadataDataService, DynamicDatasourceMapper dynamicDatasourceMapper) {
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
                TEMP_ALIAS_2 + ".\"" + "\"name\", " +
                report.getAggregationType() + "(" + TEMP_ALIAS_2 + ".\"" + report.getFieldY() + "\") as \"value\"" +
                " from (" +
                " select *, TO_CHAR(" + TEMP_ALIAS_1 + ".\"" + report.getFieldX() + "\", '" + formatter.get(report.getDateInterval()) + "') as \"name\"" +
                " from (" + querySql + ") as " + TEMP_ALIAS_1 +
                ") as " + TEMP_ALIAS_2 +
                " group " + TEMP_ALIAS_2 + ".\"name\"" +
                " order by "+ TEMP_ALIAS_2 + ".\"name\" asc";
    }
}
