package com.keyware.shandan.datasource.service;

import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.IBaseService;
import com.keyware.shandan.datasource.entity.DataSourceVo;

/**
 * <p>
 * 数据源表 服务类
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-11
 */
public interface DataSourceService extends IBaseService<DataSourceVo, String> {

    void loadDataSource();

    Result<Boolean> testDataSource(DataSourceVo dataSourceVo);
}
