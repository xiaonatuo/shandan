package com.keyware.shandan.datasource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.keyware.shandan.datasource.entity.DBTableColumnVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * DBTableColumn
 * </p>
 *
 * @author Administrator
 * @since 2021/5/26
 */
@Mapper
public interface DBTableColumnMapper extends BaseMapper<DBTableColumnVo> {

    /**
     * 根据表名查询列集合
     *
     * @param tableName 表名称
     * @return 列集合
     */
    List<DBTableColumnVo> selectColumnsByTableName(String tableName);
}
