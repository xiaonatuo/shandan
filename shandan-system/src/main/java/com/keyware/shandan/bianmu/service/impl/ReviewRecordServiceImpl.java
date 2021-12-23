package com.keyware.shandan.bianmu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.bianmu.entity.DirectoryVo;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.entity.ReviewRecordVo;
import com.keyware.shandan.bianmu.enums.ReviewEntityType;
import com.keyware.shandan.bianmu.enums.ReviewStatus;
import com.keyware.shandan.bianmu.mapper.ReviewRecordMapper;
import com.keyware.shandan.bianmu.service.DirectoryService;
import com.keyware.shandan.bianmu.service.MetadataService;
import com.keyware.shandan.bianmu.service.ReviewRecordService;
import com.keyware.shandan.common.enums.NotificationType;
import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.frame.annotation.DataPermissions;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.system.entity.SysFile;
import com.keyware.shandan.system.entity.SysNotification;
import com.keyware.shandan.system.entity.SysUser;
import com.keyware.shandan.system.queue.provider.EsSysFileProvider;
import com.keyware.shandan.system.service.SysFileService;
import com.keyware.shandan.system.service.SysNotificationService;
import com.keyware.shandan.system.service.SysOrgService;
import com.keyware.shandan.system.utils.NotificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 审核记录表 服务实现类
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-03
 */
@Service
@Transactional
public class ReviewRecordServiceImpl extends BaseServiceImpl<ReviewRecordMapper, ReviewRecordVo, String> implements ReviewRecordService {

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private SysOrgService orgService;

    @Autowired
    private SysNotificationService notificationService;

    @Autowired
    private SysFileService fileService;

    @Autowired
    private EsSysFileProvider esSysFileProvider;

    @Override
    @DataPermissions
    public List<ReviewRecordVo> list(QueryWrapper<ReviewRecordVo> wrapper) {
        return super.list(wrapper);
    }

    @Override
    @DataPermissions
    public Page<ReviewRecordVo> page(Page<ReviewRecordVo> page, QueryWrapper<ReviewRecordVo> wrapper) {
        return super.page(page, wrapper);
    }

    /**
     * 审核操作
     *
     * @param entityId   数据ID
     * @param entityType 数据对象类型
     * @param status     审核状态
     * @param opinion    审核意见
     * @return
     */
    @Override
    public Boolean review(String entityId, String entityType, String status, String opinion) throws Exception {
        ReviewEntityType reviewEntityType = Enum.valueOf(ReviewEntityType.class, entityType);
        ReviewStatus reviewStatus = Enum.valueOf(ReviewStatus.class, status);
        if (ReviewEntityType.DIRECTORY.name().equals(entityType)) {
            DirectoryVo vo = directoryService.getById(entityId);
            if (vo == null) {
                return false;
            }
            vo.setReviewStatus(reviewStatus);
            directoryService.updateById(vo);
            //如果审核通过则需要把目录下文件保存到ES
            if (vo.getReviewStatus() == ReviewStatus.PASS) {
                appendFileToES(vo);
                //设置上级目录们为通过
                updateParentsToPass(vo);
                //设置子级目录们为通过
                updateChildrenToPass(vo);
            }
        } else if (ReviewEntityType.METADATA.name().equals(entityType)) {
            MetadataBasicVo vo = metadataService.getById(entityId);
            if (vo == null) {
                return false;
            }
            vo.setReviewStatus(reviewStatus);
            metadataService.updateById(vo);
        }

        ReviewRecordVo record = new ReviewRecordVo(entityId, reviewEntityType, reviewStatus, opinion);
        if (save(record)) {
            // 发送系统通知
            notificationService.sendNotification(newReviewNotify(record));
            return true;
        }

        return false;
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
     * 递归更新所有父级目录为PASS
     *
     * @param dir 目录
     */
    private void updateParentsToPass(DirectoryVo dir) throws Exception {
        DirectoryVo parent = directoryService.getById(dir.getParentId());
        if (parent != null) {
            parent.setReviewStatus(ReviewStatus.PASS);
            directoryService.updateById(parent);
            if (!"-".equals(parent.getParentId())) {
                updateParentsToPass(parent);
            }
        }
    }



    private void updateChildrenToPass(DirectoryVo dir){
        QueryWrapper<DirectoryVo> wrapper = new QueryWrapper<>();
        wrapper.likeRight("DIRECTORY_PATH", dir.getDirectoryPath() + "/");
        List<DirectoryVo> list = directoryService.list(wrapper);
        list.stream().peek(item -> item.setReviewStatus(ReviewStatus.PASS)).collect(Collectors.toList());
        directoryService.saveOrUpdateBatch(list);
    }

    /**
     * 生成审核通知
     * @param record
     * @return
     */
    private SysNotification newReviewNotify(ReviewRecordVo record) {
        String content = genNotificationContent(record);
        String receiveIds = judgingReceiver(record);
        if(record.getEntityType() == ReviewEntityType.METADATA){
            if(record.getReviewOperate() == ReviewStatus.PASS){
                return NotificationUtil.newNotification(NotificationType.METADATA_REVIEW_PASS, null, content, receiveIds);
            }else if(record.getReviewOperate() == ReviewStatus.FAIL){
                return NotificationUtil.newNotification(NotificationType.METADATA_REVIEW_FAIL, null, content, receiveIds);
            }

            return NotificationUtil.newMetadataReviewNotify(content, receiveIds);
        }else{
            if(record.getReviewOperate() == ReviewStatus.PASS){
                return NotificationUtil.newNotification(NotificationType.DIRECTORY_REVIEW_PASS, null, content, receiveIds);
            }else if(record.getReviewOperate() == ReviewStatus.FAIL){
                return NotificationUtil.newNotification(NotificationType.DIRECTORY_REVIEW_FAIL, null, content, receiveIds);
            }
            return NotificationUtil.newDirectoryReviewNotify(content, receiveIds);
        }
    }

    /**
     * 判断通知接收人
     *
     * @param reviewRecord
     */
    private String judgingReceiver(ReviewRecordVo reviewRecord) {
        String receiveId = "";
        SysUser currentUser = SecurityUtil.getLoginSysUser();
        if (reviewRecord.getReviewOperate() == ReviewStatus.SUBMITTED) {
            // 提交操作，接收人应该是当前用户所属部门管理员
            receiveId = orgService.getOrgAdmins(currentUser.getOrgId())
                    .stream().map(SysUser::getUserId)
                    .reduce("", (a, b) -> a + "," + b);
        } else {
            // 数据资源类型
            if (reviewRecord.getEntityType() == ReviewEntityType.METADATA) {
                receiveId = metadataService.getById(reviewRecord.getEntityId()).getCreateUser();
            } else {
                // 目录数据类型
                receiveId = directoryService.getById(reviewRecord.getEntityId()).getCreateUser();
            }
        }
        return receiveId;
    }

    /**
     * 生成通知内容
     *
     * @param record
     */
    private String genNotificationContent(ReviewRecordVo record) {
        String content = "";
        if (record.getReviewOperate() == ReviewStatus.SUBMITTED) {
            content = "您收到一条" + record.getEntityType().getRemark() + "审核，请查看";
        }else if(record.getReviewOperate() == ReviewStatus.PASS){
            content = "您有一条" + record.getEntityType().getRemark() + "审核通过了";
        }else if(record.getReviewOperate() == ReviewStatus.FAIL){
            content = "您有一条" + record.getEntityType().getRemark() + "审核失败";
        }
        return content;
    }
}
