package com.keyware.shandan.browser.entity;

import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.search.sort.SortOrder;

import java.io.Serializable;
import java.util.List;

/**
 * 检索条件实体类
 *
 * @author GuoXin
 * @since 2021/6/28
 */
@Getter
@Setter
public class ConditionVo extends PageVo implements Serializable {

    private static final long serialVersionUID = 703825295068176342L;

    /**
     * 搜索条件集合
     */
    List<ConditionItem> conditions;

    /**
     * 排序字段
     */
    private String sortFiled;

    /**
     * 排序方式
     */
    private SortOrder sort = SortOrder.DESC;
}
