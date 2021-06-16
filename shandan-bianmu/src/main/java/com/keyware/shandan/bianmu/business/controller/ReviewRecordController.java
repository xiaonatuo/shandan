package com.keyware.shandan.bianmu.business.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.bianmu.business.entity.DirectoryVo;
import com.keyware.shandan.bianmu.business.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.business.entity.ReviewRecordVo;
import com.keyware.shandan.bianmu.business.enums.DirectoryType;
import com.keyware.shandan.bianmu.business.enums.ReviewStatus;
import com.keyware.shandan.bianmu.business.service.DirectoryService;
import com.keyware.shandan.bianmu.business.service.MetadataService;
import com.keyware.shandan.bianmu.business.service.ReviewRecordService;
import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * 审核记录表 前端控制器
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-03
 */
@RestController
@RequestMapping("/business/review")
public class ReviewRecordController extends BaseController<ReviewRecordService, ReviewRecordVo, String> {

    @Autowired
    private ReviewRecordService reviewRecordService;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private DirectoryService directoryService;

    /**
     * 目录审核页面
     *
     * @return
     */
    @GetMapping("/directory")
    public ModelAndView directoryReview() {
        return new ModelAndView("business/review/directoryReview");
    }

    /**
     * 元数据审核页面
     *
     * @return
     */
    @GetMapping("/metadata")
    public ModelAndView metadataReview() {
        return new ModelAndView("business/review/metadataReview");
    }

    /**
     * 元数据待审核数据列表
     * @param page
     * @param vo
     * @return
     */
    @GetMapping("/list/metadata")
    public Result<Page<MetadataBasicVo>> metadataReviewPageList(Page<MetadataBasicVo> page, MetadataBasicVo vo){
        vo.setReviewStatus(ReviewStatus.SUBMITTED);
        return Result.of(metadataService.page(page, new QueryWrapper<>(vo)));
    }

    /**
     * 目录待审核数据列表
     * @param page
     * @param vo
     * @return
     */
    @GetMapping("/list/directory")
    public Result<Page<DirectoryVo>> directoryReviewPageList(Page<DirectoryVo> page, DirectoryVo vo) {
        vo.setReviewStatus(ReviewStatus.SUBMITTED);
        vo.setDirectoryType(DirectoryType.METADATA);
        return Result.of(directoryService.page(page, new QueryWrapper<>(vo)));
    }

    /**
     * 审核相关操作
     * @param entityType 数据对象类型
     * @param entityId 数据对象ID
     * @param status 审核状态
     * @param opinion 审核意见
     * @return
     */
    @PostMapping("/operate")
    public Result<Boolean> operate(String entityType, String entityId, String status, String opinion){
        if(StringUtils.isBlankAny(entityType, entityId, status)){
            return Result.of(null, false, "参数错误");
        }

        return Result.of(reviewRecordService.review(entityId, entityType, status, opinion));
    }
}
