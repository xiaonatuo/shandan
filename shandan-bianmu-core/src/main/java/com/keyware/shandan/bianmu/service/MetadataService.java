package com.keyware.shandan.bianmu.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.common.service.IBaseService;

import java.util.HashMap;

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

    /**
     * 查询示例数据
     * @param metadataBasic
     * @param sourceId
     * @return
     */
    Page<HashMap<String, Object>> getExampleData(MetadataBasicVo metadataBasic, String sourceId);

    /**
     * 获取元数据的列
     * @param metadataId
     * @return
     */
    JSONArray getColumns(String metadataId);
}
