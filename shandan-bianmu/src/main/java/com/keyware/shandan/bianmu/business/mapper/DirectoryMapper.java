package com.keyware.shandan.bianmu.business.mapper;

import com.keyware.shandan.bianmu.business.entity.DirectoryVo;
import com.keyware.shandan.bianmu.business.entity.MetadataBasicVo;
import com.keyware.shandan.common.mapper.IBaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 目录表 Mapper 接口
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-01
 */
@Mapper
public interface DirectoryMapper extends IBaseMapper<DirectoryVo> {

    /**
     * 获取目录下的元数据
     * @param id 目录ID
     * @return
     */
    List<MetadataBasicVo> selectMetadataByDirectory(String id);
}
