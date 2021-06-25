package com.keyware.shandan.bianmu.mapper;

import com.keyware.shandan.bianmu.entity.MetadataDetailsVo;
import com.keyware.shandan.common.mapper.IBaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 元数据详情表 Mapper 接口
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-25
 */
@Mapper
public interface MetadataDetailsMapper extends IBaseMapper<MetadataDetailsVo> {

    /**
     * 查询包含有外表关系的数据
     * @param datasourceId
     * @return
     */
    List<MetadataDetailsVo> selectHasForeignTableData(String datasourceId);
}
