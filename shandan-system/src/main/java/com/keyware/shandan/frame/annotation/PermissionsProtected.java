package com.keyware.shandan.frame.annotation;

import com.keyware.shandan.common.enums.DataOperateType;

/**
 * <p>
 * 标记方法为受系统权限保护，需要仅限权限验证
 * </p>
 *
 * @author Administrator
 * @since 2021/5/21
 */
public @interface PermissionsProtected {
    /**
     * 数据操作类型
     * @return
     */
    DataOperateType operate();

    /**
     * 是否内部方法
     * @return
     */
    boolean internal() default false;
}
