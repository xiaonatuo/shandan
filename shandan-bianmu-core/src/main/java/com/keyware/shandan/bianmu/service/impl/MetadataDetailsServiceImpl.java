package com.keyware.shandan.bianmu.service.impl;

import com.keyware.shandan.bianmu.entity.MetadataDetailsVo;
import com.keyware.shandan.bianmu.mapper.MetadataDetailsMapper;
import com.keyware.shandan.common.service.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 元数据表详情服务实现类
 * </p>
 *
 * @author Administrator
 * @since 2021/5/28
 */
@Service
public class MetadataDetailsServiceImpl extends BaseServiceImpl<MetadataDetailsMapper, MetadataDetailsVo, String> implements MetadataDetailsService {

    @Autowired
    private MetadataDetailsMapper metadataDetailsMapper;

    /**
     * 查询包含外表关系的字段
     * @param datasourceId
     * @return
     */
    @Override
    public List<MetadataDetailsVo> analysis(String datasourceId) {
        return metadataDetailsMapper.selectHasForeignTableData(datasourceId);
    }
}
