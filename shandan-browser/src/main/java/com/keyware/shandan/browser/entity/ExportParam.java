package com.keyware.shandan.browser.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 统计报表导出到word参数
 */
@Data
public class ExportParam implements Serializable {
    private static final long serialVersionUID = 2671311089853559165L;

    private String title = "统计报表";
    /**
     * 数据查询条件
     */
    private List<SearchConditionVo.Item> conditions;

    // echartsImage
    private List<EchartsParam> echarts;

    private List<Map<String, Object>> fields;

    private List<String> echartsImage;
}
