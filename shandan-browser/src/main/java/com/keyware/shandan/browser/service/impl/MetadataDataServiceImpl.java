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

/**
 * 资源数据服务实现类
 */
@Service
public class MetadataDataServiceImpl implements MetadataDataService {

    @Autowired
    private DynamicDatasourceMapper dynamicDatasourceMapper;

    @Autowired
    private DataSourceService dataSourceService;

    @Override
    @DS("#metadata.dataSourceId")
    public PageVo queryData(MetadataBasicVo metadata, SearchConditionVo condition) {
        DataSourceVo dataSource = dataSourceService.getById(metadata.getDataSourceId());
        if (dataSource == null) {
            throw new RuntimeException("系统没有找到数据源配置");
        }
        String sql = MetadataUtils.generateSql(metadata, dataSource);
        sql += condition.getWhereSql(false);
        sql += condition.getOrderBySql();
        return PageVo.pageConvert(dynamicDatasourceMapper.page(new Page(condition.getPage(), condition.getSize()), sql));
    }
}
