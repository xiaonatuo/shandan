package com.keyware.shandan.bianmu.service.impl;

import com.keyware.shandan.bianmu.entity.MetadataDetailsVo;
import com.keyware.shandan.common.service.IBaseService;

import java.util.List;

/**
 * <p>
 * 数据资源详情服务类
 * </p>
 *
 * @author GuoXin
 * @since 2021/5/28
 */
public interface MetadataDetailsService extends IBaseService<MetadataDetailsVo, String> {
    /**
     * 查询包含外表关系的数据
     * @param datasourceId
     * @return
     */
    List<MetadataDetailsVo> analysis(String datasourceId);


    /**
     * 查询关联关系分析
     * @param metadataId
     * @return
     */
    List<MetadataDetailsVo> analysisMetadata(String metadataId);
}
