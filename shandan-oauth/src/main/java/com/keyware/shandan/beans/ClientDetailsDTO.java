package com.keyware.shandan.beans;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * ClientDetailsDTO
 *
 * @author Administrator
 * @since 2021/11/30
 */
@Data
public class ClientDetailsDTO implements ClientDetails {
    private static final long serialVersionUID = 1878566448352148025L;

    private String clientId;

    private String clientSecret;

    private String resourceIds;

    private String scope;

    private String authorizedGrantTypes;

    private String registeredRedirectUri;

    private Integer accessTokenValiditySeconds;

    private Integer refreshTokenValiditySeconds;

    private String autoApproveScopes;

    private List<GrantedAuthority> authorities = Collections.emptyList();

    private String additionalInformation;

    @Override
    public Set<String> getScope() {
        if(StringUtils.hasText(scope)){
            return new HashSet<>(Arrays.asList(scope.split(",")));
        }
        return Collections.emptySet();
    }

    @Override
    public Set<String> getResourceIds() {
        if(StringUtils.hasText(resourceIds)){
            return new HashSet<>(Arrays.asList(resourceIds.split(",")));
        }
        return Collections.emptySet();
    }

    @Override
    public boolean isSecretRequired() {
        return this.clientSecret != null;
    }

    @Override
    public boolean isScoped() {
        return this.scope != null && !this.scope.isEmpty();
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        if(StringUtils.hasText(authorizedGrantTypes)){
            return new HashSet<>(Arrays.asList(authorizedGrantTypes.split(",")));
        }
        return Collections.emptySet();
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        if(StringUtils.hasText(registeredRedirectUri)){
            return new HashSet<>(Arrays.asList(registeredRedirectUri.split(",")));
        }
        return Collections.emptySet();
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return null;
    }

    public Set<String> getAutoApproveScopes(){
        if(StringUtils.hasText(autoApproveScopes)){
            return new HashSet<>(Arrays.asList(autoApproveScopes.split(",")));
        }
        return Collections.emptySet();
    }

    @Override
    public boolean isAutoApprove(String scope) {
        if (autoApproveScopes == null) {
            return false;
        }
        for (String auto : getAutoApproveScopes()) {
            if (auto.equals("true") || scope.matches(auto)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        if(StringUtils.hasText(additionalInformation)){
            JSONObject json = JSONObject.parseObject(additionalInformation);
            return json.getInnerMap();
        }
        return null;
    }
}
