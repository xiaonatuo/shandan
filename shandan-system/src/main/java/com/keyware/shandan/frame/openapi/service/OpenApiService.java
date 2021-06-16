package com.keyware.shandan.frame.openapi.service;

import com.keyware.shandan.common.entity.Result;

public interface OpenApiService {
    /**
     * open api test测试
     * @return 测试数据
     */
    Result<String> test();
}
