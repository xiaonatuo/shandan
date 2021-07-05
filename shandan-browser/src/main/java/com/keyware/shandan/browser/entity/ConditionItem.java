package com.keyware.shandan.browser.entity;

import com.keyware.shandan.browser.enums.ConditionLogic;
import lombok.Data;


/**
 * 查询条件字段项实体
 *
 * @author GuoXin
 * @since 2021/7/1
 */
@Data
public class ConditionItem  {
    private static final long serialVersionUID = 8098508043125737364L;

    /**
     * 查询字段
     */
    private String field;

    /**
     * 查询条件的逻辑
     */
    private ConditionLogic logic;

    /**
     * 查询条件对应的值
     */
    private String value;

}
