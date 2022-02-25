package com.keyware.shandan.bianmu.service;

import com.keyware.shandan.bianmu.entity.DirectoryMetadataVo;
import com.keyware.shandan.bianmu.entity.MetadataAndFileVo;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.common.service.IBaseService;

import java.util.List;

/**
 * <p>
 * 目录与数据资源关系表 服务类
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-03
 */
public interface DirectoryMetadataService extends IBaseService<DirectoryMetadataVo, String> {

    /**
     * 获取目录下包含所有子级目录下的数据资源
     *
     * @param id 目录ID
     * @return 数据资源集合
     */
    List<MetadataAndFileVo> getMetadataAndFileListByDir(String id);
}
