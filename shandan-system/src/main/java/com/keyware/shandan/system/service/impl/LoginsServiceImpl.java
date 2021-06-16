package com.keyware.shandan.system.service.impl;

import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.system.entity.Logins;
import com.keyware.shandan.system.mapper.LoginsMapper;
import com.keyware.shandan.system.service.LoginsService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * persistent_logins表，用户实现记住我功能 服务实现类
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
@Service
public class LoginsServiceImpl extends BaseServiceImpl<LoginsMapper, Logins, Object> implements LoginsService {

}
