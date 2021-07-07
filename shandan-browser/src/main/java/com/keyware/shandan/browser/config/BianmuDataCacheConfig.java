package com.keyware.shandan.browser.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.bianmu.entity.MetadataDetailsVo;
import com.keyware.shandan.bianmu.service.impl.MetadataDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分类编目数据缓存配置
 *
 * @author GuoXin
 * @since 2021/7/6
 */
@Configuration
@EnableScheduling
public class BianmuDataCacheConfig {

    // 每次查询的数据量
    private final static int size = 5000;

    @Autowired
    private MetadataDetailsService metadataDetailsService;

    /**
     * 定时任务：更新cache
     */
    @PostConstruct
    @Scheduled(cron = "0 0 0/1 * * ?")
    private void run() {
        updateCache();
    }

    /**
     * 定时任务每周执行一次，清空缓存并全量更新
     */
    @Scheduled(cron = "0 0 0 ? * 1")
    private void run2() {
        BianmuDataCache.clear();
        updateCache();
    }

    /**
     * 更新缓存
     */
    private void updateCache() {
        // 先获取总数， 总数大于5000则分次查询，每次查询5000条
        int count = metadataDetailsService.count();
        int pageTotals = (int) Math.ceil(count / (double) size);
        for (int page = 1; page <= pageTotals; page++) {
            queryMetadata(page);
        }
    }

    /**
     * 查询数据
     *
     * @param page 页数
     */
    private void queryMetadata(int page) {
        QueryWrapper<MetadataDetailsVo> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("MODIFY_TIME");
        Page<MetadataDetailsVo> pageList = metadataDetailsService.page(new Page<>(page, size), wrapper);
        for (MetadataDetailsVo vo : pageList.getRecords()) {
            BianmuDataCache.put(vo.getTableName(), vo);
        }
    }

}
