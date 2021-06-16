package com.keyware.shandan.bianmu.business.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.bianmu.business.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.business.entity.MetadataDetailsVo;
import com.keyware.shandan.bianmu.business.enums.ReviewStatus;
import com.keyware.shandan.bianmu.business.mapper.MetadataBasicMapper;
import com.keyware.shandan.datasource.mapper.DynamicDatasourceMapper;
import com.keyware.shandan.datasource.service.DynamicDataSourceService;
import com.keyware.shandan.bianmu.business.service.MetadataService;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.frame.annotation.DataPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 元数据详情表 服务实现类
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-25
 */
@Service
public class MetadataServiceImpl extends BaseServiceImpl<MetadataBasicMapper, MetadataBasicVo, String> implements MetadataService {

    @Autowired
    private MetadataBasicMapper metadataBasicMapper;

    @Autowired
    private MetadataDetailsService metadataDetailsService;

    @Autowired
    private DynamicDataSourceService dynamicDataSourceService;

    @Autowired
    private DynamicDatasourceMapper dynamicDatasourceMapper;

    @Override
    @DataPermissions
    public List<MetadataBasicVo> list(QueryWrapper<MetadataBasicVo> wrapper) {
        return super.list(wrapper);
    }

    @Override
    @DataPermissions
    public Page<MetadataBasicVo> page(Page<MetadataBasicVo> page, QueryWrapper<MetadataBasicVo> wrapper) {
        return super.page(page, wrapper);
    }

    /**
     * 保存元数据基础信息和详细信息列表
     *
     * @param metadata
     * @return
     */
    @Override
    @Transactional
    public Boolean saveMetadataBasicAndDetailsList(MetadataBasicVo metadata) {
        MetadataBasicVo basic = super.getById(metadata.getId());
        if (basic == null) {
            metadata.setReviewStatus(ReviewStatus.UN_SUBMIT);
            boolean ok = super.save(metadata);
            if (ok) {
                if (metadata.getMetadataDetailsList() != null) {
                    metadata.setMetadataDetailsList(metadata.getMetadataDetailsList().stream()
                            .peek(metadataDetails -> metadataDetails.setMetadataId(metadata.getId())).collect(Collectors.toList()));
                    metadataDetailsService.saveBatch(metadata.getMetadataDetailsList());
                }
            }
        } else {
            super.updateById(metadata);
            if (metadata.getMetadataDetailsList() != null) {
                metadata.setMetadataDetailsList(metadata.getMetadataDetailsList().stream()
                        .peek(metadataDetails -> metadataDetails.setMetadataId(metadata.getId())).collect(Collectors.toList()));
                metadataDetailsService.updateBatchById(metadata.getMetadataDetailsList());
            }
        }

        return true;
    }

    /**
     * 根据目录ID查询元数据列表
     * @param directoryId
     * @return
     */
    @Override
    public Page<MetadataBasicVo> pageListByDirectory(Page<MetadataBasicVo> page, String directoryId, String metadataName) {
        if (StringUtils.isNotBlank(metadataName)) {
            metadataName = "%" + metadataName + "%";
        }
        return metadataBasicMapper.pageListByDirectoryId(page, directoryId, metadataName);
    }

    /**
     * 获取元数据表的示例数据
     *
     * @param metadataBasic
     * @param sourceId
     * @return
     */
    @Override
    @DS("#sourceId")
    public Page<HashMap<String, Object>> getExampleData(MetadataBasicVo metadataBasic, String sourceId) {
        Optional<MetadataDetailsVo> optional = metadataBasic.getMetadataDetailsList().stream().filter(MetadataDetailsVo::getMaster).findFirst();
        if(!optional.isPresent()){
            return new Page<>();
        }
        MetadataDetailsVo master = optional.get();
        List<HashMap<String, Object>> list = dynamicDatasourceMapper.list(master.getTableName());
        Page<HashMap<String, Object>> page = new Page<>(1, 10);

        // 处理Clob类型
        page.setRecords(list.stream().peek(data -> {
            data.entrySet().stream().peek(entry -> {
                if (entry.getValue() instanceof Clob) {
                    Clob clob = (Clob) entry.getValue();
                    try {
                        entry.setValue(clob.getSubString(1, (int) clob.length()));
                    } catch (SQLException ignored) { }
                }
            }).collect(Collectors.toSet());
        }).collect(Collectors.toList()));
        page.setTotal(list.size());
        return page;
    }

    /**
     * 根据元数据id获取
     * @param id
     * @return
     */
    @Override
    public Result<MetadataBasicVo> get(String id) {
        MetadataBasicVo result = getById(id);
        List<MetadataDetailsVo> details = result.getMetadataDetailsList();

        // 查询每个详情对象的数据表的列
        if(!ObjectUtils.isEmpty(details)){
            result.setMetadataDetailsList(details.stream().peek(detail->{
                detail.setColumnList(dynamicDataSourceService.getColumnListByTable(detail.getTableName(), result.getDataSourceId()));
            }).collect(Collectors.toList()));
        }
        return Result.of(result);
    }

    /**
     * 根据ID删除，同时删除详情数据
     * @param id
     * @return
     */
    @Override
    @Transactional
    public Result<Boolean> deleteById(String id) {
        MetadataBasicVo result = getById(id);
        if(result != null){
            if(!ObjectUtils.isEmpty(result.getMetadataDetailsList())){
                metadataDetailsService.removeByIds(result.getMetadataDetailsList().stream().map(MetadataDetailsVo::getId).collect(Collectors.toList()));
            }
            return super.deleteById(id);
        }else{
            return Result.of(false, false, "删除失败，数据不存在");
        }
    }
}
