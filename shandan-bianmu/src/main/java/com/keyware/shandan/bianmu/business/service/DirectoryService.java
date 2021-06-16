package com.keyware.shandan.bianmu.business.service;

import com.keyware.shandan.bianmu.business.entity.DirectoryVo;
import com.keyware.shandan.bianmu.business.entity.MetadataBasicVo;
import com.keyware.shandan.common.service.IBaseService;

import java.util.List;

/**
 * <p>
 * 目录表 服务类
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-01
 */
public interface DirectoryService extends IBaseService<DirectoryVo, String> {

    /**
     * 获取树的子节点
     * @param vo 父节点ID
     * @return
     */
    List<DirectoryVo> treeChildren(DirectoryVo vo);

    /**
     * 获取目录下的元数据
     * @param id 目录ID
     * @return
     */
    List<MetadataBasicVo> directoryMetadata(String id);
}
