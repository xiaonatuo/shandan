package com.keyware.shandan.control.es.config;

import com.keyware.shandan.control.es.consumer.EsSysFileQueueConsumer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * 系统文件保存到ES的消费者启动配置
 *
 * @author GuoXin
 * @since 2021/6/30
 */
@Configuration
public class SysFileToESConsumerInitializing implements InitializingBean {

    @Autowired
    private EsSysFileQueueConsumer esSysFileQueueConsumer;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 启动消费者线程
        esSysFileQueueConsumer.start();
    }

}
