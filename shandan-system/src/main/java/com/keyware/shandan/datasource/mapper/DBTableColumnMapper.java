package com.keyware.shandan.datasource.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.keyware.shandan.datasource.entity.DBTableColumnVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
}
