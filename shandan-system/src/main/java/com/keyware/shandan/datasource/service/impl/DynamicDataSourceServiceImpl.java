package com.keyware.shandan.datasource.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.datasource.entity.DBTableColumnVo;
import com.keyware.shandan.datasource.entity.DBUserTableVo;
import com.keyware.shandan.datasource.entity.DataSourceVo;
import com.keyware.shandan.datasource.mapper.DBTableColumnMapper;
import com.keyware.shandan.datasource.mapper.DBUserTableMapper;
import com.keyware.shandan.datasource.mapper.DataSourceMapper;
import com.keyware.shandan.datasource.mapper.DynamicDatasourceMapper;
import com.keyware.shandan.datasource.service.DynamicDataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 动态数据源服务实现类
 * </p>
 *
 * @author GuoXin
 * @since 2021/5/26
 */
@Service
public class DynamicDataSourceServiceImpl implements DynamicDataSourceService {

    @Autowired
    private DataSourceMapper dataSourceMapper;

    @Autowired
    private DBUserTableMapper tableMapper;

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
        if (StringUtils.isNotBlank(table.getTableName())) {
            table.setTableName("%" + table.getTableName() + "%");
        }
        Page<DBUserTableVo> result = tableMapper.dbTablePageByOwner(page, table.getOwner(), table.getTableName());
        result.setRecords(result.getRecords().stream().peek(vo ->
                vo.setColumnList(vo.getColumnList().stream().filter(col -> col.getOwner().equals(table.getOwner())).collect(Collectors.toList()))
        ).collect(Collectors.toList()));
        return result;
    }

    /**
     * 查询数据表的列
     *
     * @param tableName  -
     * @param dataSource -
     * @return -
     */
    @Override
    @DS("#dataSource.id")
    public List<DBTableColumnVo> getColumnListByTable(String tableName, DataSourceVo dataSource) {
        return columnMapper.selectColumnsByTableNameAndOwner(tableName, dataSource.getJdbcSchema());
    }
}
