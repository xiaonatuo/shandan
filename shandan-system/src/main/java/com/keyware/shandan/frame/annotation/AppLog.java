package com.keyware.shandan.frame.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 应用操作日志注解
 *
 * @author GuoXin
 * @since 2021/7/26
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AppLog {

    /**
     * 操作描述
     *
     * @return 操作描述
     */
    String operate();

    /**
     * 日志级别
     *
     * @return 日志级别
     */
    String level() default "info";
}
