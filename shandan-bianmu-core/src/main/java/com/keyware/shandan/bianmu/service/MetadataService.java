package com.keyware.shandan.bianmu.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.common.service.IBaseService;
import com.keyware.shandan.datasource.entity.DataSourceVo;

import java.util.HashMap;

/**
 * <p>
 * 数据资源详情表 服务类
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-25
 */
public interface MetadataService extends IBaseService<MetadataBasicVo, String> {

    /**
     * 保存数据资源基础信息和详细信息列表
     *
     * @param metadata
     * @return
     */
    Boolean saveMetadataBasicAndDetailsList(MetadataBasicVo metadata);

    /**
     * 根据目录ID查询数据资源列表
     * @param directoryId
     * @return
     */
    Page<MetadataBasicVo> pageListByDirectory(Page<MetadataBasicVo> page, String directoryId, String metadataName);

    /**
     * 查询示例数据
     * @param metadataBasic
     * @param dataSource
     * @return
     */
    Page<HashMap<String, Object>> getExampleData(MetadataBasicVo metadataBasic, DataSourceVo dataSource);

    Page<HashMap<String, Object>> getDynamicData(MetadataBasicVo metadata, int page, int size, String orderField, String order);

    /**
     * 获取数据资源的列
     * @param metadataId
     * @return
     */
    JSONArray getColumns(String metadataId);
}
