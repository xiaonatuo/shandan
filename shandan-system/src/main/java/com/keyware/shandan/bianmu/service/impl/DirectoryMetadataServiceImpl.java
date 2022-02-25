package com.keyware.shandan.bianmu.service.impl;

import com.keyware.shandan.bianmu.entity.DirectoryMetadataVo;
import com.keyware.shandan.bianmu.entity.MetadataAndFileVo;
import com.keyware.shandan.bianmu.mapper.DirectoryMetadataMapper;
import com.keyware.shandan.bianmu.service.DirectoryMetadataService;
import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.system.utils.StringUtil;
import joptsimple.internal.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 目录与数据资源关系表 服务实现类
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-03
 */
@Service
public class DirectoryMetadataServiceImpl extends BaseServiceImpl<DirectoryMetadataMapper, DirectoryMetadataVo, String> implements DirectoryMetadataService {

    @Autowired
    private DirectoryMetadataMapper directoryMetadataMapper;

    @Override
    public List<MetadataAndFileVo> getMetadataAndFileListByDir(String id) {
        if ("-".equals(id)) {
            id = Strings.EMPTY;
        }
        return directoryMetadataMapper.selectMetadataAndFileListByDir(id);
    }
}
