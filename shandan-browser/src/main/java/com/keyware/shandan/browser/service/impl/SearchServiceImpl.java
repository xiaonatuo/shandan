package com.keyware.shandan.browser.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.bianmu.es.repository.EsSysFileRepository;
import com.keyware.shandan.bianmu.service.DirectoryService;
import com.keyware.shandan.browser.entity.ConditionVo;
import com.keyware.shandan.browser.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

/**
 * 检索服务实现类
 *
 * @author Administrator
 * @since 2021/7/1
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private EsSysFileRepository esSysFileRepository;

    /**
     * 全文检索
     *
     * @param condition 条件
     * @return -
     */
    @Override
    public Page<Object> esSearch(ConditionVo condition) {
        return null;
    }
}
