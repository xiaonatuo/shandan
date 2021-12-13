package com.keyware.shandan.system.service;

import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.IBaseService;
import com.keyware.shandan.system.entity.SysUserClient;

import java.util.List;

/**
 * <p>
 * 系统用户与Oauth2客户端关系表 服务类
 * </p>
 *
 * @author fengzhi
 * @since 2021-12-13
 */
public interface SysUserClientService extends IBaseService<SysUserClient,String> {

    Result<List<SysUserClient>> findByUserId(String userId);

    Result<Boolean> saveAllByUserId(String userId, String clientIdList);
}
