package com.keyware.shandan.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * 系统通知类型常量类
 * </p>
 *
 * @author GuoXin
 * @since 2021/6/11
 */
@Getter
@AllArgsConstructor
public enum NotificationType {
    NORMAL("NORMAL", "普通通知"),
    METADATA_REVIEW("METADATA_REVIEW", "元数据审核通知"),
    METADATA_REVIEW_PASS("METADATA_REVIEW_PASS", "元数据审核通过通知"),
    METADATA_REVIEW_FAIL("METADATA_REVIEW_FAIL", "元数据审核未通过通知"),
    DIRECTORY_REVIEW("DIRECTORY_REVIEW", "目录审核通知"),
    DIRECTORY_REVIEW_PASS("DIRECTORY_REVIEW_PASS", "目录审核通过通知"),
    DIRECTORY_REVIEW_FAIL("DIRECTORY_REVIEW_FAIL", "目录审核未通过通知");

    @EnumValue
    private final String value;
    private final String remark;
}
