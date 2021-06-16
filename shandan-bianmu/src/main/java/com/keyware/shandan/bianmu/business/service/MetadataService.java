package com.keyware.shandan.bianmu.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.bianmu.business.entity.MetadataBasicVo;
import com.keyware.shandan.common.service.IBaseService;

/**
 * <p>
 * 元数据详情表 服务类
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-25
 */
public interface MetadataService extends IBaseService<MetadataBasicVo, String> {

    /**
     * 保存元数据基础信息和详细信息列表
     *
     * @param metadata
     * @return
     */
    Boolean saveMetadataBasicAndDetailsList(MetadataBasicVo metadata);

    /**
     * 根据目录ID查询元数据列表
     * @param directoryId
     * @return
     */
    Page<MetadataBasicVo> pageListByDirectory(Page<MetadataBasicVo> page, String directoryId, String metadataName);
}
