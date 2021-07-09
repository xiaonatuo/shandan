package com.keyware.shandan.browser.controller;

import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.entity.MetadataDetailsVo;
import com.keyware.shandan.bianmu.service.MetadataService;
import com.keyware.shandan.common.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
