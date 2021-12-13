package com.keyware.shandan.system.mapper;


import com.keyware.shandan.common.mapper.IBaseMapper;
import com.keyware.shandan.system.entity.OauthClientDetails;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * OAUTH_CLIENT_DETAILS
 * </p>
 *
 * @author fengzhi
 * @since 2021-12-10
 */
@Mapper
public interface OauthClientDetailsMapper extends IBaseMapper<OauthClientDetails> {

    @Select("select A.* from OAUTH_CLIENT_DETAILS A")
    List<OauthClientDetails> getList();

}


