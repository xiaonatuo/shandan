package com.keyware.shandan.bianmu.service;

import com.keyware.shandan.bianmu.entity.DirectoryVo;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
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
     * 获取目录下的数据资源
     * @param id 目录ID
     * @return
     */
    List<MetadataBasicVo> directoryMetadata(String id);

    /**
     * 获取目录下所有的数据资源，包含子级
     * @param id 目录ID
     * @return
     */
    List<MetadataBasicVo> directoryAllMetadata(String id);

    /**
     * 获取目录的所有父级目录
     * @param dir
     * @return
     */
    List<DirectoryVo> parentLists(DirectoryVo dir);

    /**
     * 获取目录的所有子节点
     * @param dir
     * @return
     */
    List<DirectoryVo> childrenLists(DirectoryVo dir);
}
