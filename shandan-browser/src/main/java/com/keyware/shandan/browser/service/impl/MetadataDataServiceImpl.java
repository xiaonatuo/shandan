package com.keyware.shandan.browser.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.utils.MetadataUtils;
import com.keyware.shandan.browser.entity.PageVo;
import com.keyware.shandan.browser.entity.SearchConditionVo;
import com.keyware.shandan.browser.service.MetadataDataService;
import com.keyware.shandan.datasource.entity.DataSourceVo;
import com.keyware.shandan.datasource.mapper.DynamicDatasourceMapper;
import com.keyware.shandan.datasource.service.DataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * 资源数据服务实现类
 */
@Service
public class MetadataDataServiceImpl implements MetadataDataService {

    @Autowired
    private DynamicDatasourceMapper dynamicDatasourceMapper;

    @Autowired
    private DataSourceService dataSourceService;

    /**
     * 根据检索条件查询指定数据资源下的数据分页列表
     *
     * @param metadata  数据资源实体
     * @param condition 条件项
     * @return 数据分页列表
     */
    @Override
    public PageVo queryData(MetadataBasicVo metadata, SearchConditionVo condition) {
        String sql = getQuerySql(metadata, condition);
        sql += condition.getOrderBySql();
        return dynamicQueryPage(condition, sql);
    }

    @Override
    public String getQuerySql(MetadataBasicVo metadata, SearchConditionVo condition) {
        DataSourceVo dataSource = dataSourceService.getById(metadata.getDataSourceId());
        if (dataSource == null) {
            throw new RuntimeException("系统没有找到数据源配置");
        }
        String sql = MetadataUtils.generateSql(metadata, dataSource);
        sql += condition.getWhereSql(false);
        return sql;
    }

    /**
     * 动态查询分页数据
     *
     * @param metadata  数据资源实体，动态数据源查询时，使用其中dataSourceId字段
     * @param condition 条件项
     * @param sql       动态sql语句
     * @return 分页列表
     */
    @DS("#metadata.dataSourceId")
    private PageVo dynamicQueryPage(SearchConditionVo condition, String sql) {
        Page<HashMap<String, Object>> page = dynamicDatasourceMapper.page(new Page<>(condition.getPage(), condition.getSize()), sql);
        // 处理Clob类型
        page.setRecords(page.getRecords().stream().peek(data -> data.entrySet().stream().peek(entry -> {
            if (entry.getValue() instanceof Clob) {
                Clob clob = (Clob) entry.getValue();
                try {
                    entry.setValue(clob.getSubString(1, (int) clob.length()));
                } catch (SQLException ignored) {
                }
            }
        }).collect(Collectors.toSet())).collect(Collectors.toList()));
        return PageVo.pageConvert(page);
    }
}
