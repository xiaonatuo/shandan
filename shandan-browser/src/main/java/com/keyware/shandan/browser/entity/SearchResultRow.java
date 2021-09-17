package com.keyware.shandan.browser.entity;

import com.keyware.shandan.common.enums.CommonFields;

import java.util.HashMap;

/**
 * 检索结果数据行
 *
 * @author GuoXin
 * @since 2021/9/16
 */
public class SearchResultRow extends HashMap<String, Object> {

    private static final long serialVersionUID = 64550248608999689L;

    // 实例化时自动填充公共字段
    public SearchResultRow(){
        for(CommonFields fields : CommonFields.values()){
            this.put(fields.name(), "");
        }
    }
}
