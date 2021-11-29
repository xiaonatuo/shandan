package com.keyware.shandan.bianmu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
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
        DirectoryVo oldDir = null;
        if (StringUtils.isBlank(entity.getId())) {
            entity.setReviewStatus(ReviewStatus.UN_SUBMIT);
        } else {
            oldDir = getById(entity.getId());
        }

        // 查询父目录，并设置目录路径
        DirectoryVo parent = getById(entity.getParentId());
        entity.setDirectoryPath(parent == null ? "/".concat(entity.getDirectoryName()) : parent.getDirectoryPath().concat("/").concat(entity.getDirectoryName()));

        QueryWrapper<DirectoryVo> wrapper = new QueryWrapper<>();
        wrapper.eq("DIRECTORY_PATH", entity.getDirectoryPath());
        if (list(wrapper).size() > 0) {
            return Result.of(null, false, "目录已经存在！");
        }

        // 同时更新所有子级目录的路径
        if (oldDir != null) {
            directoryMapper.updateDirectoryPath(oldDir.getDirectoryPath()+"/", entity.getDirectoryPath()+"/");
        }

        //如果审核通过则需要把目录下文件保存到ES
        if (entity.getReviewStatus() == ReviewStatus.PASS) {
            SysFile condition = new SysFile();
            condition.setEntityId(entity.getId());
            List<SysFile> files = fileService.list(new QueryWrapper<>(condition));
            esSysFileProvider.appendQueue(files);

            //处理父级所有目录
            updateParentsToPass(entity);
        }

        return super.updateOrSave(entity);
    }

    @Override
    @DataPermissions(type = DataPermissions.Type.ORG_AND_DIRECTORY)
    public List<DirectoryVo> list(QueryWrapper<DirectoryVo> wrapper) {
        return super.list(wrapper);
    }

    @Override
    @DataPermissions(type = DataPermissions.Type.ORG_AND_DIRECTORY)
    public Page<DirectoryVo> page(Page<DirectoryVo> page, QueryWrapper<DirectoryVo> wrapper) {
        return super.page(page, wrapper);
    }

    /**
     * 获取树的子节点
     *
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
     *
     * @param id 目录ID
     * @return
     */
    @Override
    public List<MetadataBasicVo> directoryMetadata(String id) {
        return directoryMapper.selectMetadataByDirectory(id);
    }

    @Override
    public List<MetadataBasicVo> directoryAllMetadata(String id) {
        return directoryMapper.selectAllMetadataByDirectory(id);
    }

    /**
     * 递归更新所有父级目录为PASS
     *
     * @param dir 目录
     */
    private void updateParentsToPass(DirectoryVo dir) {
        DirectoryVo parent = getById(dir.getParentId());
        if (parent != null) {
            parent.setReviewStatus(ReviewStatus.PASS);
            updateOrSave(parent);
            if (!"-".equals(parent.getParentId())) {
                updateParentsToPass(parent);
            }
        }
    }
}
