package com.keyware.shandan.common.enums;

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
public enum NotificationType {
    NORMAL("普通通知"),
    METADATA_REVIEW("元数据审核通知"),
    DIRECTORY_REVIEW("目录审核通知");

    private final String remark;
    NotificationType(String remark){
        this.remark = remark;
    }
}
