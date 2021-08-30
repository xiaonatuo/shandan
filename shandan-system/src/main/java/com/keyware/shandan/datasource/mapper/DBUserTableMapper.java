package com.keyware.shandan.datasource.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.datasource.entity.DBUserTableVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * DynamicDataSourceMapper
 * </p>
 *
 * @author Administrator
 * @since 2021/5/26
 */
@Mapper
public interface DBUserTableMapper {

    Page<DBUserTableVo> dbTablePageByOwner(Page<DBUserTableVo> page, String owner, String tableName);
}
