package com.keyware.shandan.browser.controller;

import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.entity.MetadataDetailsVo;
import com.keyware.shandan.bianmu.service.MetadataService;
import com.keyware.shandan.browser.entity.ConditionVo;
import com.keyware.shandan.browser.entity.ReportVo;
import com.keyware.shandan.browser.service.SearchService;
import com.keyware.shandan.browser.service.impl.SearchServiceImpl;
import com.keyware.shandan.common.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 统计报表前端控制器
 *
 * @author GuoXin
 * @since 2021/7/9
 */
@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private SearchService searchService;

    /**
     * 查询元数据表的字段
     * @param id 元数据ID
     * @return
     */
    @GetMapping("/metadata/columns/{id}")
    public Result<Object> getColumn(@PathVariable String id){
        MetadataBasicVo vo = metadataService.getById(id);
        if(vo != null){
            Stream<MetadataDetailsVo> stream = vo.getMetadataDetailsList().stream();
            Optional<MetadataDetailsVo> optional = stream.filter(MetadataDetailsVo::getMaster).findFirst();
            if(optional.isPresent()){
                MetadataDetailsVo details = optional.get();
                return Result.of(JSONObject.toJSON(details.getTableColumns()));
            }
        }
        return Result.of(null);
    }


    @PostMapping("/data")
    public Result<Object> getData(ReportVo report){

        try {
            return Result.of(searchService.report(report));
        } catch (IOException e) {
            e.printStackTrace();
            return Result.of(null, false);
        }
    }
}
