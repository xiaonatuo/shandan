package com.keyware.shandan.desktop.services.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keyware.shandan.desktop.entity.AppInfo;
import com.keyware.shandan.desktop.mapper.AppInfoMapper;
import com.keyware.shandan.desktop.services.AppInfoService;
import org.springframework.stereotype.Service;

@Service
public class AppInfoServiceImpl extends ServiceImpl<AppInfoMapper, AppInfo> implements AppInfoService {
}
