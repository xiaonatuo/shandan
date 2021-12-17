package com.keyware.shandan.browser.entity;

import com.keyware.shandan.browser.enums.ConditionLogic;
import lombok.Data;
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
public class SearchConditionVo extends PageVo implements Serializable {

    private static final long serialVersionUID = 703825295068176342L;

    /**
     * 搜索条件集合
     */
    List<Item> conditions;



    /**
     * 排序字段
     */
    @Deprecated
    private String sortFiled;

    /**
     * 排序方式
     */
    @Deprecated
    private SortOrder sort = SortOrder.DESC;

    /**
     * 搜索条件项
     */
    @Getter
    @Setter
    public static class Item  {

        /**
         * 查询字段
         */
        private String field;

        /**
         * 查询条件对应的值
         */
        private String value;

        /**
         * 与上一个条件拼接字符串,在条件项集合中第一个应该为空字符串，之后为“and”或者“or”，如果条件中包含子查询，那么应该为“(”和“)”,此时judgmentStr为空
         */
        private String joinStr = "";

        /**
         * 条件判断字符,如：=，!=，<，<=， >， >=，like
         */
        private String judgmentStr;

        /**
         * 查询条件的逻辑
         */
        @Deprecated
        private ConditionLogic logic;

    }

    /**
     * 排序
     */
    @Getter
    @Setter
    public static class Sort{
        /**
         * 排序字段
         */
        private String field;

        /**
         * 排序方式
         */
        private String sort = "ASC";
    }
}
