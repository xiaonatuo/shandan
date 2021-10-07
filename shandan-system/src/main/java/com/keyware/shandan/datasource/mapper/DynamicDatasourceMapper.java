package com.keyware.shandan.datasource.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

    List<HashMap<String, Object>> list(String sqlStr);

    @Select("${sql}")
    Page<HashMap<String, Object>> page(Page page, String sql);

}
