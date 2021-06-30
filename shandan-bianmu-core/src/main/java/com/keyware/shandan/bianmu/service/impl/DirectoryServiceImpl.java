package com.keyware.shandan.bianmu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.bianmu.entity.DirectoryVo;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.enums.ReviewStatus;
import com.keyware.shandan.bianmu.mapper.DirectoryMapper;
import com.keyware.shandan.bianmu.service.DirectoryService;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.frame.annotation.DataPermissions;
import com.keyware.shandan.system.entity.SysFile;
import com.keyware.shandan.system.queue.provider.EsSysFileProvider;
import com.keyware.shandan.system.service.SysFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 目录表 服务实现类
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-01
 */
@Service
public class DirectoryServiceImpl extends BaseServiceImpl<DirectoryMapper, DirectoryVo, String> implements DirectoryService {

    @Autowired
    private DirectoryMapper directoryMapper;

    @Autowired
    private SysFileService fileService;

    @Autowired
    private EsSysFileProvider esSysFileProvider;

    @Override
    public Result<DirectoryVo> updateOrSave(DirectoryVo entity) {
        if (StringUtils.isBlank(entity.getId())) {
            entity.setReviewStatus(ReviewStatus.UN_SUBMIT);
        }
        // 查询父目录，并设置目录路径
        DirectoryVo parent = getById(entity.getParentId());
        entity.setDirectoryPath(parent == null ? "/".concat(entity.getDirectoryName()) : parent.getDirectoryPath().concat("/").concat(entity.getDirectoryName()));

        //如果审核通过则需要把目录下文件保存到ES
        if (entity.getReviewStatus() == ReviewStatus.PASS) {
            SysFile condition = new SysFile();
            condition.setEntityId(entity.getId());
            List<SysFile> files = fileService.list(new QueryWrapper<>(condition));
            esSysFileProvider.appendQueue(files);
        }

        return super.updateOrSave(entity);
    }

    @Override
    @DataPermissions
    public List<DirectoryVo> list(QueryWrapper<DirectoryVo> wrapper) {
        return super.list(wrapper);
    }

    @Override
    @DataPermissions
    public Page<DirectoryVo> page(Page<DirectoryVo> page, QueryWrapper<DirectoryVo> wrapper) {
        return super.page(page, wrapper);
    }

    /**
     * 获取树的子节点
     * @param vo
     * @return
     */
    @Override
    public List<DirectoryVo> treeChildren(DirectoryVo vo) {
        if (StringUtils.isBlank(vo.getParentId())) {
            vo.setParentId("-");
        }
        return this.list(new QueryWrapper<>(vo));
    }

    /**
     * 获取目录下的元数据
     * @param id 目录ID
     * @return
     */
    @Override
    public List<MetadataBasicVo> directoryMetadata(String id) {
        return directoryMapper.selectMetadataByDirectory(id);
    }
}
