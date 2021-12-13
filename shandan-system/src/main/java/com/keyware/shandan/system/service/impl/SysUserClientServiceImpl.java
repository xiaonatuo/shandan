package com.keyware.shandan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.system.entity.SysUserClient;
import com.keyware.shandan.system.mapper.SysUserClientMapper;
import com.keyware.shandan.system.service.SysUserClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统用户与Oauth2客户端关系表 服务实现类
 * </p>
 *
 * @author fengzhi
 * @since 2021-12-13
 */
@Service
public class SysUserClientServiceImpl extends BaseServiceImpl<SysUserClientMapper, SysUserClient, String> implements SysUserClientService {

    @Autowired
    private SysUserClientMapper sysUserClientMapper;

    @Override
    public Result<List<SysUserClient>> findByUserId(String userId) {
        List<SysUserClient> userClients = sysUserClientMapper.listByUserId(userId);
        return Result.of(userClients);
    }

    @Override
    public Result<Boolean> saveAllByUserId(String userId, String clientIdList) {
        if (!StringUtils.isNotBlank(userId)) {
            return Result.of(false, false, "参数userId不能为null");
        }
        SysUserClient userClient = new SysUserClient();
        userClient.setUserId(userId);
        super.remove(new QueryWrapper<>(userClient));

        List<SysUserClient> userClientList = Arrays.stream(clientIdList.split(",")).map(clientId -> {
            SysUserClient ur = new SysUserClient();
            ur.setUserId(userId);
            ur.setClientId(clientId);
            return ur;
        }).collect(Collectors.toList());
        boolean ok = super.saveBatch(userClientList);
        return Result.of(ok, ok, ok ? "保存成功" : "保存失败");
    }
}
