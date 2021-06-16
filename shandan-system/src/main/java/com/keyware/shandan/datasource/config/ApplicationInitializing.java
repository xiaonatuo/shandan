package com.keyware.shandan.datasource.config;

import com.keyware.shandan.datasource.service.DataSourceService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 动态数据源初始化类
 * </p>
 *
 * @author Administrator
 * @since 2021/5/24
 */
@Component
public class ApplicationInitializing implements InitializingBean {

    @Autowired
    private DataSourceService dataSourceService;


    @Override
    public void afterPropertiesSet() throws Exception {
        dataSourceService.loadDataSource();
    }
}
