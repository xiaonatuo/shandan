package com.keyware.shandan.system.service.impl;

import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.system.entity.SysOperateLog;
import com.keyware.shandan.system.mapper.SysOperateLogMapper;
import com.keyware.shandan.system.service.SysOperateLogService;
import org.springframework.stereotype.Service;

@Service
public class SysOperateLogServiceImpl extends BaseServiceImpl<SysOperateLogMapper, SysOperateLog, String> implements SysOperateLogService {
}
