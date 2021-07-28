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
     *
     * @return
     */
    String orgColumn() default "ORG_ID";

    /**
     * 数据权限类型
     *
     * @return -
     */
    Type type() default Type.ORG;

    /**
     * 数据权限类型
     */
    public static enum Type {
        /**
         * 机构权限
         */
        ORG,

        /**
         * 目录权限
         */
        DIRECTORY,

        /**
         * 机构和目录权限
         */
        ORG_AND_DIRECTORY,

        /**
         * 元数据权限
         */
        METADATA;

        Type() {
        }
    }
}
