package com.keyware.shandan.browser.entity;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Getter;
import lombok.Setter;

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
public class ConditionVo extends Page<Object> implements Serializable {

    private static final long serialVersionUID = 703825295068176342L;

    /**
     * 搜索条件集合
     */
    List<ConditionItem> conditions;
}
