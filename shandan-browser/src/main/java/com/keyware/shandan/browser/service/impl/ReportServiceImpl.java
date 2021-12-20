package com.keyware.shandan.browser.service.impl;

import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.service.MetadataService;
import com.keyware.shandan.browser.entity.ReportVo;
import com.keyware.shandan.browser.entity.SearchConditionVo;
import com.keyware.shandan.browser.service.MetadataDataService;
import com.keyware.shandan.browser.service.ReportService;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.datasource.mapper.DynamicDatasourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 统计报表服务接口实现类
 *
 * @author GuoXin
 * @since 2021/12/20
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private MetadataDataService metadataDataService;

    @Autowired
    private DynamicDatasourceMapper dynamicDatasourceMapper;

    /**
     * @param metadata  数据资源实体
     * @param condition 条件参数
     * @return 条件查询的sql语句
     * @throws Exception -
     */
    @Override
    public String getConditionsQuerySql(MetadataBasicVo metadata, SearchConditionVo condition) throws Exception {
        return metadataDataService.getQuerySql(metadata, condition);
    }

    /**
     * 获取统计sql
     *
     * @param report   统计参数
     * @return 统计sql语句
     * @throws Exception -
     */
    @Override
    public String getStatisticsSql(ReportVo report) throws Exception {
        if (StringUtils.isBlank(report.getMetadataId())) {
            throw new Exception("数据资源ID不可用");
        }

        MetadataBasicVo metadata = metadataService.get(report.getMetadataId()).getData();
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
    @Override
    public Map<String, Object> statisticsQuery(ReportVo report) throws Exception {
        String statisticSql = getStatisticsSql(report);
        List<Map<String, Object>> result = dynamicDatasourceMapper.list(statisticSql);

        return null;
    }

    /**
     * 生成统计sql语句
     *
     * @param reportVo 统计参数
     * @param sql      数据查询sql
     * @return 统计sql语句
     * @throws Exception -
     */
    private String buildStatisticsSql(ReportVo reportVo, String sql) throws Exception {
        StringBuilder builder = new StringBuilder("select ");


        return builder.toString();
    }
}
