package com.keyware.shandan.bianmu.entity;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.OrderBy;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import com.keyware.shandan.bianmu.enums.ReviewEntityType;
import com.keyware.shandan.bianmu.enums.ReviewStatus;
import com.keyware.shandan.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * <p>
 * 审核记录表
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("B_REVIEW_RECORD")
public class ReviewRecordVo extends BaseEntity{

    private static final long serialVersionUID = -2672911761334699330L;

    /**
     * 主键
     */
    @TableField("ID")
    private String id;

    /**
     * 数据实体ID
     */
    @TableField("ENTITY_ID")
    private String entityId;

    /**
     * 数据实体类型
     */
    @TableField("ENTITY_TYPE")
    private ReviewEntityType entityType;

    /**
     * 审核操作（"SUBMIT"：提交审核，"REVIEW_PASS"：审核通过， "REVIEW_FAIL"：审核不通过）
     */
    @TableField("REVIEW_OPERATE")
    private ReviewStatus reviewOperate;

    /**
     * 审核意见
     */
    @TableField("REVIEW_OPINION")
    private String reviewOpinion;

    /**
     * 所属机构
     */
    @TableField(value = "ORG_ID", fill = FieldFill.INSERT)
    private String orgId;

    /**
     * 创建时间
     */
    @OrderBy
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private Date createTime;

    public ReviewRecordVo(){}
    public ReviewRecordVo(String entityId, ReviewEntityType entityType, ReviewStatus reviewOperate, String reviewOpinion) {
        this.entityId = entityId;
        this.entityType = entityType;
        this.reviewOperate = reviewOperate;
        this.reviewOpinion = reviewOpinion;
    }
}
