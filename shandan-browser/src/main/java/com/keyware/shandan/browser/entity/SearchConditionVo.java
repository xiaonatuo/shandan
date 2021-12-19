package com.keyware.shandan.browser.entity;

import com.keyware.shandan.browser.enums.ConditionLogic;
import com.keyware.shandan.common.util.StringUtils;
import joptsimple.internal.Strings;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.search.sort.SortOrder;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    // 达梦数据库字符类型
    public static final String[] STRING_TYPES = {"CHAR", "CHARACTER", "VARCHAR", "VARCHAR2", "TEXT", "LONGVARCHAR", "BLOB", "CLOB"};
    // 达梦数据库数值类型
    public static final String[] NUMBER_TYPES = {"NUMERIC", "NUMBER", "DECIMAL", "DEC", "BIT", "INTEGER", "INT", "PLS_INTEGER", "BIGINT", "TINYINT", "BYTE", "SMALLINT", "BINARY", "VARBINARY", "REAL", "FLOAT", "DOUBLE", "DOUBLE PRECISION"};
    //达梦数据库日期类型
    public static final String[] DATE_TYPES = {"DATE", "DATETIME", "TIMESTAMP", "DATETIME WITH TIME ZONE", "TIME", "TIME WITH TIME ZONE", "TIMESTAMP WITH TIME ZONE", "TIMESTAMP WITH LOCAL TIME ZONE"};

    //搜索条件集合
    private List<Item> conditions = Collections.emptyList();
    // 排序
    private Sort sort = new Sort();
    //排序字段
    @Deprecated
    private String sortFiled;
    //排序方式
    @Deprecated
    private SortOrder order = SortOrder.DESC;

    /**
     * @param appendWithWhere 当前sql是否拼接在where关键字之后
     * @return where 条件sql字符串
     */
    public String getWhereSql(boolean appendWithWhere) {
        if (appendWithWhere && conditions.size() > 0) {
            conditions.get(0).setLogicJoin("");
        }
        List<String> sqlList = conditions.stream().map(Item::toSql).collect(Collectors.toList());

        return String.join("", sqlList);
    }

    /**
     * @return order by sql 语句
     */
    public String getOrderBySql() {
        return sort.getOrderBy();
    }

    /**
     * 搜索条件项
     */
    @Getter
    @Setter
    public static class Item {

        // 数据表名称
        private String table;
        // 字段数据类型
        private String dataType;
        // 字段名称
        private String fieldName;
        // 字段值
        private String fieldValue = Strings.EMPTY;
        //与上一个条件拼接字符串,在条件项集合中第一个应该为空字符串，之后为“and”或者“or”，如果条件中包含子查询，那么应该为“(”和“)”,此时judgmentStr为空
        private String logicJoin = "and";
        // 条件判断字符,如：=，!=，<，<=， >， >=，like_all, like_left, like_right
        private String logicJudgement = Strings.EMPTY;

        // 查询条件的逻辑
        @Deprecated
        private ConditionLogic logic;

        public String toSql() {
            if (StringUtils.isBlankAny(table, fieldName)) {
                return Strings.EMPTY;
            }
            if (Arrays.asList(STRING_TYPES).contains(dataType) ||
                    Arrays.asList(DATE_TYPES).contains(dataType)) {
                fieldValue = "'" + fieldValue + "'";
            }

            if (logicJudgement.equalsIgnoreCase("like_all")) {
                logicJudgement = "like";
                fieldValue = fieldValue.substring(1, fieldValue.length() - 2);
                fieldValue = "'%" + fieldValue + "%'";
            } else if (logicJudgement.equalsIgnoreCase("like_left")) {
                logicJudgement = "like";
                fieldValue = fieldValue.substring(1);
                fieldValue = "'%" + fieldValue;
            } else if (logicJudgement.equalsIgnoreCase("like_right")) {
                logicJudgement = "like";
                fieldValue = fieldValue.substring(0, fieldValue.length() - 2);
                fieldValue = fieldValue + "%'";
            }

            return " " + logicJoin + " \"" + table + "\".\"" + fieldName + "\" " + logicJudgement + " " + fieldValue;
        }
    }

    /**
     * 排序
     */
    @Getter
    @Setter
    public static class Sort {
        //排序字段
        private String field;
        //排序方式
        private String sort;

        public String getField() {
            return " " + field;
        }

        public String getSort() {
            return " " + sort;
        }

        /**
         * @return order by sql语句
         */
        public String getOrderBy() {
            return isEmpty() ? Strings.EMPTY : " order by" + getField() + " " + getSort();
        }

        public boolean isEmpty() {
            return StringUtils.isBlankAny(this.field, this.sort);
        }
    }
}
