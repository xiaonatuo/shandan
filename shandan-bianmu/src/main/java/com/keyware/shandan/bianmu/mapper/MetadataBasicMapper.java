package com.keyware.shandan.bianmu.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.common.mapper.IBaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 元数据表 Mapper 接口
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-25
 */
@Mapper
public interface MetadataBasicMapper extends IBaseMapper<MetadataBasicVo> {

    /**
     * 根据目录ID查询元数据列表
     * @param directoryId
     * @return
     */
    Page<MetadataBasicVo> pageListByDirectoryId(Page page, String directoryId, String metadataName);
}
