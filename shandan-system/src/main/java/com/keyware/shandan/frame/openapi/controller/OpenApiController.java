package com.keyware.shandan.frame.openapi.controller;

import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.frame.openapi.service.OpenApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/openApi/")
public class OpenApiController {

    @Autowired
    private OpenApiService openApiService;

    @GetMapping("test")
    public Result<String> test() {
        return openApiService.test();
    }
}
