package com.keyware.shandan.bianmu.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.common.mapper.IBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 数据资源表 Mapper 接口
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-25
 */
@Mapper
public interface MetadataBasicMapper extends IBaseMapper<MetadataBasicVo> {

    /**
     * 根据目录ID查询数据资源列表
     * @param directoryId
     * @return
     */
    Page<MetadataBasicVo> pageListByDirectoryId(Page page, String directoryId, String metadataName);

    @Override
    @Select("SELECT * from B_METADATA_BASIC ${ew.customSqlSegment} ORDER BY CASE REVIEW_STATUS WHEN 'UN_SUBMIT' THEN 1 WHEN 'SUBMITTED' THEN 2 WHEN 'FAIL' THEN 3 WHEN 'REJECTED' THEN 4 WHEN 'PASS' THEN 5 END, CREATE_TIME DESC")
    <E extends IPage<MetadataBasicVo>> E selectPage(E page, @Param(Constants.WRAPPER) Wrapper<MetadataBasicVo> queryWrapper);
}
