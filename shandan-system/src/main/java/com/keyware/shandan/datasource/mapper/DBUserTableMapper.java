package com.keyware.shandan.datasource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
public interface DBUserTableMapper extends BaseMapper<DBUserTableVo> {
}
