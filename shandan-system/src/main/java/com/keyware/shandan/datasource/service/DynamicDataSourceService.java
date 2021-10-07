package com.keyware.shandan.datasource.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.datasource.entity.DBTableColumnVo;
import com.keyware.shandan.datasource.entity.DBUserTableVo;
import com.keyware.shandan.datasource.entity.DataSourceVo;

import java.util.List;

/**
 * <p>
 * 动态数据源服务类
 * </p>
 *
 * @author Administrator
 * @since 2021/5/26
 */
public interface DynamicDataSourceService {
    /**
     * 分页查询数据表
     *
     * @param page
     * @param sourceId 数据源ID
     * @return
     */
    Page<DBUserTableVo> getDBTablesPage(Page<DBUserTableVo> page, DBUserTableVo table, String sourceId);

    /**
     * 查询数据表的列
     *
     * @param tableName
     * @param dataSource
     * @return
     */
    List<DBTableColumnVo> getColumnListByTable(String tableName, DataSourceVo dataSource);

}
