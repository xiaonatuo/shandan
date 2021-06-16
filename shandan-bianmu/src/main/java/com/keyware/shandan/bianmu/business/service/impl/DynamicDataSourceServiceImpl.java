package com.keyware.shandan.bianmu.business.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keyware.shandan.datasource.entity.DBTableColumnVo;
import com.keyware.shandan.datasource.entity.DBUserTableVo;
import com.keyware.shandan.bianmu.business.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.business.entity.MetadataDetailsVo;
import com.keyware.shandan.datasource.mapper.DBTableColumnMapper;
import com.keyware.shandan.datasource.mapper.DBUserTableMapper;
import com.keyware.shandan.bianmu.business.mapper.DynamicDatasourceMapper;
import com.keyware.shandan.bianmu.business.service.DynamicDataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 动态数据源服务实现类
 * </p>
 *
 * @author Administrator
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
        DBTableColumnVo column = new DBTableColumnVo();
        column.setTableName(tableName);
        return columnMapper.selectList(new QueryWrapper<>(column));
    }

    /**
     * 获取元数据表的示例数据
     *
     * @param metadataBasic
     * @param sourceId
     * @return
     */
    @Override
    @DS("#sourceId")
    public Page<HashMap<String, Object>> getExampleData(MetadataBasicVo metadataBasic, String sourceId) {
        Optional<MetadataDetailsVo> optional = metadataBasic.getMetadataDetailsList().stream().filter(MetadataDetailsVo::getMaster).findFirst();
        if(!optional.isPresent()){
            return new Page<>();
        }
        MetadataDetailsVo master = optional.get();
        List<HashMap<String, Object>> list = dynamicDatasourceMapper.list(master.getTableName());
        Page<HashMap<String, Object>> page = new Page<>(1, 10);

        // 处理Clob类型
        page.setRecords(list.stream().peek(data -> {
            data.entrySet().stream().peek(entry -> {
                if (entry.getValue() instanceof Clob) {
                    Clob clob = (Clob) entry.getValue();
                    try {
                        entry.setValue(clob.getSubString(1, (int) clob.length()));
                    } catch (SQLException ignored) { }
                }
            }).collect(Collectors.toSet());
        }).collect(Collectors.toList()));
        page.setTotal(list.size());
        return page;
    }
}
