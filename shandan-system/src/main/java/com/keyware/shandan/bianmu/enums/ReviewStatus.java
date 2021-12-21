package com.keyware.shandan.bianmu.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public enum ReviewStatus  {
    UN_SUBMIT("UN_SUBMIT", "未提交"),
    SUBMITTED("SUBMITTED", "待审核"),
    PASS("PASS", "审核通过"),
    FAIL("FAIL", "审核不通过"),
    REJECTED("REJECTED", "已驳回");

    @EnumValue
    private final String value;
    private final String remark;

    /*ReviewStatus(String remark) {
        this.remark = remark;
    }*/
}
