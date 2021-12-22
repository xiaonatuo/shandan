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
import org.springframework.transaction.annotation.Transactional;

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


    /**
     * 删除目录，包含所有子级目录
     *
     * @param id
     * @return -
     */
    @Override
    public Result<Boolean> deleteById(String id) {
        DirectoryVo dir = getById(id);
        QueryWrapper<DirectoryVo> wrapper = new QueryWrapper<>();
        wrapper.likeRight("DIRECTORY_PATH", dir.getDirectoryPath() + "/");
        // 先删除自己
        removeById(id);
        // 再删除子节点
        remove(wrapper);
        return Result.of(null, true);
    }

    @Override
    @Transactional
    public Result<DirectoryVo> updateOrSave(DirectoryVo entity) throws Exception {
        // 查询父目录，并设置目录路径
        DirectoryVo parent = null;
        if (entity.getParentId().equals("-")) {
            parent = new DirectoryVo();
            parent.setId("-");
            parent.setDirectoryPath("/");
        } else {
            parent = getById(entity.getParentId());
            if (parent == null) {
                throw new Exception("父级目录不存在");
            }
        }

        String path = parent.getDirectoryPath() + "/" + entity.getDirectoryName();
        boolean isInsert = StringUtils.isBlank(entity.getId());
        DirectoryVo existsDir = getByPath(path);

        if (isInsert) {
            if (existsDir != null) {
                throw new Exception("目录已存在");
            }
            entity.setReviewStatus(ReviewStatus.UN_SUBMIT);
            super.updateOrSave(entity);

            // 如果父目录为审核通过状态，则修改为待审核
            if (parent.getReviewStatus() == ReviewStatus.PASS) {
                parent.setReviewStatus(ReviewStatus.UN_SUBMIT);
                super.updateOrSave(parent);
            }
        } else {
            DirectoryVo oldDir = getById(entity.getId());
            if (existsDir != null && !existsDir.getId().equals(entity.getId())) {
                throw new Exception("目录已存在");
            }
            entity.setDirectoryPath(path);
            // 如果修改的目录原本为审核通过状态，则更新为未提交
            if (oldDir.getReviewStatus() == ReviewStatus.PASS) {
                entity.setReviewStatus(ReviewStatus.UN_SUBMIT);
            }
            super.updateOrSave(entity);
            // 同时更新所有子级目录的路径
            updateChildrenPath(oldDir, entity);

            //如果审核通过则需要把目录下文件保存到ES
            if (entity.getReviewStatus() == ReviewStatus.PASS) {
                appendFileToES(entity);
                //设置上级目录们为通过
                updateParentsToPass(entity);
            }
        }
        return Result.of(entity);
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
     * @param vo -
     * @return -
     */
    @Override
    public List<DirectoryVo> treeChildren(DirectoryVo vo) {
        if (StringUtils.isBlank(vo.getParentId())) {
            vo.setParentId("-");
        }
        return this.list(new QueryWrapper<>(vo));
    }

    /**
     * 获取目录下的数据资源
     *
     * @param id 目录ID
     * @return -
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
    private void updateParentsToPass(DirectoryVo dir) throws Exception {
        DirectoryVo parent = getById(dir.getParentId());
        if (parent != null) {
            parent.setReviewStatus(ReviewStatus.PASS);
            updateOrSave(parent);
            if (!"-".equals(parent.getParentId())) {
                updateParentsToPass(parent);
            }
        }
    }

    /**
     * 根据path查询目录实体
     *
     * @param path 目录路径
     * @return 目录
     */
    private DirectoryVo getByPath(String path) {
        QueryWrapper<DirectoryVo> wrapper = new QueryWrapper<>();
        wrapper.eq("DIRECTORY_PATH", path);
        return getOne(wrapper);
    }

    /**
     * 将目录下的文件同步到ES
     *
     * @param dir 目录
     */
    private void appendFileToES(DirectoryVo dir) {
        SysFile condition = new SysFile();
        condition.setEntityId(dir.getId());
        List<SysFile> files = fileService.list(new QueryWrapper<>(condition));
        esSysFileProvider.appendQueue(files);
    }

    /**
     * 更新所有子级目录路径
     *
     * @param oldDir 原始目录
     * @param newDir 新目录
     * @throws Exception -
     */
    private void updateChildrenPath(DirectoryVo oldDir, DirectoryVo newDir) throws Exception {
        if (StringUtils.isBlankAny(oldDir.getDirectoryPath(), newDir.getDirectoryPath())) {
            throw new Exception("目录路径设置异常");
        }
        directoryMapper.updateDirectoryPath(oldDir.getDirectoryPath() + "/", newDir.getDirectoryPath() + "/");
    }
}
