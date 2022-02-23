package com.keyware.shandan.system.service.impl;

import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.common.util.MD5Util;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.system.entity.OauthClientDetails;
import com.keyware.shandan.system.mapper.OauthClientDetailsMapper;
import com.keyware.shandan.system.service.OauthClientDetailsService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * OAUTH_CLIENT_DETAILS
 * </p>
 *
 * @author fengzhi
 * @since 2021-12-10
 */
@Service
public class OauthClientDetailsImpl extends BaseServiceImpl<OauthClientDetailsMapper, OauthClientDetails, Object>
        implements OauthClientDetailsService {

    @Override
    public Result<OauthClientDetails> updateOrSave(OauthClientDetails entity) throws Exception {
        OauthClientDetails old = getById(entity.getId());
        if (old == null || !old.getClientSecret().equals(entity.getClientSecret())) {
            if(StringUtils.isNotBlank(entity.getClientSecret())){
                entity.setClientSecret(MD5Util.getMD5(entity.getClientSecret()));
            }
        }
        return super.updateOrSave(entity);
    }
}
