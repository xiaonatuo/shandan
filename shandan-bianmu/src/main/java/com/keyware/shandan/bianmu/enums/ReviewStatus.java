package com.keyware.shandan.bianmu.enums;

import lombok.Getter;

/**
 * <p>
 * 审核状态枚举类
 * </p>
 *
 * @author Administrator
 * @since 2021/5/25
 */
@Getter
public enum ReviewStatus  {
    UN_SUBMIT("未提交"),
    SUBMITTED("已提交"),
    PASS("审核通过"),
    FAIL("审核不通过");

    private final String remark;

    ReviewStatus(String remark) {
        this.remark = remark;
    }
}
