package com.keyware.shandan.frame.openapi.service;

import com.keyware.shandan.common.entity.Result;
import org.springframework.stereotype.Service;

@Service
public class OpenApiServiceImpl implements OpenApiService {
    @Override
    public Result<String> test() {
        return Result.of("无需登录的接口：OpenApi测试数据！");
    }
}
