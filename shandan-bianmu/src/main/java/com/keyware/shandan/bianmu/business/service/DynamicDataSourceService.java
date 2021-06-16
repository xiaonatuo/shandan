package com.keyware.shandan.bianmu.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.keyware.shandan.datasource.entity.DBTableColumnVo;
import com.keyware.shandan.datasource.entity.DBUserTableVo;
import com.keyware.shandan.bianmu.business.entity.MetadataBasicVo;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 动态数据源服务类
 * </p>
 *
 * @author Administrator
 * @since 2021/5/26
 */
public interface DynamicDataSourceService extends IService<DBUserTableVo> {
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
     * @param sourceId
     * @return
     */
    List<DBTableColumnVo> getColumnListByTable(String tableName, String sourceId);


    /**
     * 获取元数据表的示例数据
     *
     * @param metadataBasic
     * @param sourceId
     * @return
     */
    Page<HashMap<String, Object>> getExampleData(MetadataBasicVo metadataBasic, String sourceId);
}
