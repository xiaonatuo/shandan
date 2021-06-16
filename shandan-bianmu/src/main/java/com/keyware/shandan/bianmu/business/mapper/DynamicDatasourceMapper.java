package com.keyware.shandan.bianmu.business.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 动态数据源mapper
 * </p>
 *
 * @author Administrator
 * @since 2021/5/31
 */
@Mapper
public interface DynamicDatasourceMapper {

    List<HashMap<String, Object>> list(String tableName);
}
