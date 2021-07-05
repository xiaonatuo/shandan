package com.keyware.shandan.browser.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.browser.entity.ConditionVo;
import com.keyware.shandan.browser.service.SearchService;
import com.keyware.shandan.common.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 组合搜索前端控制器
 *
 * @author Administrator
 * @since 2021/7/1
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;


    /**
     * 全文检索分页查询
     *
     * @param condition 条件
     * @return -
     */
    @PostMapping("/full")
    public Result<Page<Object>> fullSearch(ConditionVo condition) {

        return Result.of(searchService.esSearch(condition));
    }
}
