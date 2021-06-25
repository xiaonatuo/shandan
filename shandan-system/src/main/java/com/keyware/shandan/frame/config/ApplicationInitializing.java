package com.keyware.shandan.frame.config;

import com.keyware.shandan.common.util.ErrorUtil;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.datasource.service.DataSourceService;
import com.keyware.shandan.system.entity.SysSetting;
import com.keyware.shandan.system.service.SysSettingService;
import com.keyware.shandan.system.utils.SysSettingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <p>
 * 动态数据源初始化类
 * </p>
 *
 * @author Administrator
 * @since 2021/5/24
 */
@Slf4j
@Component
public class ApplicationInitializing implements InitializingBean {

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private SysSettingService sysSettingService;

    @Autowired
    private Environment env;

    @Override
    public void afterPropertiesSet() throws Exception {
        dataSourceService.loadDataSource();
    }

    /**
     * 启动成功
     */
    @Bean
    public ApplicationRunner applicationRunner() {
        String contextPath = env.getProperty("server.servlet.context-path");
        String port = env.getProperty("server.port");
        if(StringUtils.isBlank(contextPath)){contextPath = "/";}
        if(StringUtils.isBlank(port)){port = "8080";}
        String finalPort = port;
        String finalContextPath = contextPath;
        return applicationArguments -> {
            try {
                //系统启动时获取数据库数据，设置到公用静态集合sysSettingMap
                SysSetting setting = sysSettingService.list().get(0);
                SysSettingUtil.setSysSettingMap(setting);

                //获取本机内网IP
                log.info("启动成功：" + "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + finalPort + finalContextPath);
            } catch (UnknownHostException e) {
                //输出到日志文件中
                log.error(ErrorUtil.errorInfoToString(e));
            }
        };
    }
}
