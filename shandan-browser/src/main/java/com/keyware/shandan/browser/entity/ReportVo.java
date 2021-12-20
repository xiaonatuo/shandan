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
     * 数据资源ID
     */
    private String metadataId;

    /**
     * 图表类型
     */
    private String reportType;

    /**
     * 统计维度字段
     */
    private String fieldX;

    /**
     * 统计维度字段类型
     */
    private String fieldXType;

    /**
     * 统计维度日期间隔
     */
    private String dateInterval;

    /**
     * 统计维度数值间隔
     */
    private Double numberInterval;

    /**
     * 指标聚合方式
     */
    private String aggregationType;

    /**
     * 指标聚合字段
     */
    private String fieldY;

    /**
     * 数据查询条件
     */
    private List<SearchConditionVo.Item> conditions;
}
