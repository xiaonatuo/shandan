package com.keyware.shandan.browser.controller;

import com.keyware.shandan.browser.entity.ConditionVo;
import com.keyware.shandan.browser.entity.PageVo;
import com.keyware.shandan.browser.service.SearchService;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.frame.annotation.AppLog;
import com.keyware.shandan.system.entity.SysFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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
    @AppLog(operate = "全文检索分页查询")
    @PostMapping("/full")
    public Result<PageVo> fullSearch(ConditionVo condition) {

        try {
            return Result.of(searchService.esSearch(condition));
        } catch (IOException e) {
            e.printStackTrace();
            return Result.of(null, false, "数据查询异常");
        }
    }


    /**
     * 测试接口
     *
     * @param condition
     * @return
     */
    @PostMapping("/full/test")
    public Result<PageVo> fullSearchTest(@RequestBody ConditionVo condition) {

        try {
            return Result.of(searchService.esSearch(condition));
        } catch (IOException e) {
            e.printStackTrace();
            return Result.of(null, false, "数据查询异常");
        }
    }

    /**
     * 查询指定元数据表的分页数据
     *
     * @param metadataId 元数据表ID
     * @param condition  查询条件
     * @return 分页数据
     */
    @PostMapping("/metadata/page")
    public Result<PageVo> dataPageByMetadata(String metadataId, ConditionVo condition) {

        return Result.of(null, false);
    }

}
