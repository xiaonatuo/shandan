package com.keyware.shandan.browser.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 统计报表实体类
 *
 * @author GuoXin
 * @since 2021/6/28
 */
@Data
public class ReportVo implements Serializable {
    private static final long serialVersionUID = 7215196708660649726L;

    /**
     * 图表类型
     */
    private String type;

    /**
     * 统计维度字段
     */
    private String fieldX;

    /**
     * 统计指标字段
     */
    private String fieldY;

    /**
     * 数据查询条件
     */
    private List<ConditionItem> conditions;
}
