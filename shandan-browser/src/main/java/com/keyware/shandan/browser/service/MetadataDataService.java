package com.keyware.shandan.browser.service;

import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.browser.entity.PageVo;
import com.keyware.shandan.browser.entity.SearchConditionVo;

/**
 * 资源数据服务类
 */
public interface MetadataDataService {

    PageVo queryData(MetadataBasicVo metadata, SearchConditionVo condition);

    String getQuerySql(MetadataBasicVo metadata, SearchConditionVo condition);
}
