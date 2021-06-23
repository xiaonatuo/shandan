package com.keyware.shandan.datasource.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keyware.shandan.datasource.entity.DBTableColumnVo;
import com.keyware.shandan.datasource.entity.DBUserTableVo;
import com.keyware.shandan.datasource.mapper.DBTableColumnMapper;
import com.keyware.shandan.datasource.mapper.DBUserTableMapper;
import com.keyware.shandan.datasource.mapper.DynamicDatasourceMapper;
import com.keyware.shandan.datasource.service.DynamicDataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 动态数据源服务实现类
 * </p>
 *
 * @author GuoXin
 * @since 2021/5/26
 */
@Service
public class DynamicDataSourceServiceImpl extends ServiceImpl<DBUserTableMapper, DBUserTableVo> implements DynamicDataSourceService {

    @Autowired
    private DBTableColumnMapper columnMapper;

    @Autowired
    private DynamicDatasourceMapper dynamicDatasourceMapper;

    /**
     * 分页查询数据表
     *
     * @param page
     * @param sourceId 数据源ID
     * @return
     */
    @Override
    @DS("#sourceId") // 动态数据源，数据源从参数中获取
    public Page<DBUserTableVo> getDBTablesPage(Page<DBUserTableVo> page, DBUserTableVo table, String sourceId) {
        return page(page, new QueryWrapper<>(table));
    }

    /**
     * 查询数据表的列
     *
     * @param tableName
     * @param sourceId
     * @return
     */
    @Override
    @DS("#sourceId")
    public List<DBTableColumnVo> getColumnListByTable(String tableName, String sourceId) {
        return columnMapper.selectColumnsByTableName(tableName);
    }
}
