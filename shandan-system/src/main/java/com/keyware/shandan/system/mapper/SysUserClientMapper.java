package com.keyware.shandan.system.mapper;

import com.keyware.shandan.common.mapper.IBaseMapper;
import com.keyware.shandan.system.entity.SysUserClient;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 系统用户与Oauth2客户端关系表 Mapper 接口
 * </p>
 *
 * @author fengzhi
 * @since 2021-12-13
 */
@Mapper
public interface SysUserClientMapper extends IBaseMapper<SysUserClient> {

    /**
     * 根据userId查询用户客户端关系集合
     * @param userId
     * @return
     */
    List<SysUserClient> listByUserId(String userId);

}
