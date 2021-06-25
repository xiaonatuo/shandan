package com.keyware.shandan.frame.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记方法问的数据受权限保护，仅限查询方法
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataPermissions {
    /**
     * 指定要查询的数据表中标识部门ID的列
     * @return
     */
    String orgColumn() default "ORG_ID";
}
