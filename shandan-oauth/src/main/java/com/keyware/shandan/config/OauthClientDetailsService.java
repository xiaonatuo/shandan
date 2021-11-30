package com.keyware.shandan.config;

import com.keyware.shandan.beans.ClientDetailsDTO;
import com.keyware.shandan.beans.GrantedAuthorityDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClientDetailsService
 *
 * @author Administrator
 * @since 2021/11/30
 */
@Service
public class OauthClientDetailsService extends JdbcClientDetailsService {

    public OauthClientDetailsService(DataSource dataSource, PasswordEncoder passwordEncoder) {
        super(dataSource);
        setPasswordEncoder(passwordEncoder);
    }

    public List<ClientDetailsDTO> list() {
        List<ClientDetails> list = super.listClientDetails();
        return list.stream().map(dto -> {
            BaseClientDetails baseDetails = (BaseClientDetails) dto;
            ClientDetailsDTO details = new ClientDetailsDTO();
            details.setClientId(baseDetails.getClientId());
            details.setClientSecret(baseDetails.getClientSecret());
            details.setAccessTokenValiditySeconds(baseDetails.getRefreshTokenValiditySeconds());
            details.setRefreshTokenValiditySeconds(baseDetails.getRefreshTokenValiditySeconds());
            details.setAdditionalInformation(baseDetails.getAdditionalInformation());

            details.setResourceIds(baseDetails.getResourceIds());

            details.setScope(baseDetails.getScope());

            details.setAuthorizedGrantTypes(baseDetails.getAuthorizedGrantTypes());

            details.setRegisteredRedirectUri(baseDetails.getRegisteredRedirectUri());

            details.setAutoApproveScopes(baseDetails.getAutoApproveScopes());

            if(baseDetails.getAuthorities() != null){
                details.setAuthorities(baseDetails.getAuthorities()
                        .stream()
                        .map(grantedAuthority -> new GrantedAuthorityDTO(grantedAuthority.getAuthority()))
                        .collect(Collectors.toSet()));
            }
            return details;
        }).collect(Collectors.toList());
    }
}
