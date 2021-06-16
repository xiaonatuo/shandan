package com.keyware.shandan.datasource.mapper;

import com.keyware.shandan.datasource.entity.DataSourceVo;
import com.keyware.shandan.common.mapper.IBaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 数据源表 Mapper 接口
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-11
 */
@Mapper
public interface DataSourceMapper extends IBaseMapper<DataSourceVo> {

}
