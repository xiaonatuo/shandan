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
    METADATA_REVIEW("NORMAL", "元数据审核通知"),
    DIRECTORY_REVIEW("NORMAL", "目录审核通知");

    @EnumValue
    private final String value;
    private final String remark;
}
