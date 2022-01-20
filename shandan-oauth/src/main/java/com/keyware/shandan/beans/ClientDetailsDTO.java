package com.keyware.shandan.beans;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 客户端详情数据传输类
 *
 * @author Guoxin
 * @since 2021/11/30
 */
@Data
public class ClientDetailsDTO implements ClientDetails {
    private static final long serialVersionUID = 1878566448352148025L;

    /**
     * 客户端ID
     */
    private String clientId;

    /**
     * 客户端密码
     */
    private String clientSecret;

    /**
     * 资源ID
     */
    private Set<String> resourceIds = Collections.emptySet();

    /**
     * 权限范围
     */
    private Set<String> scope = Collections.emptySet();

    /**
     * 授权类型
     */
    private Set<String> authorizedGrantTypes = Collections.emptySet();

    /**
     * 登录成功重定向地址
     */
    private Set<String> registeredRedirectUris = Collections.emptySet();

    /**
     * token有效时长
     */
    private Integer accessTokenValiditySeconds;

    /**
     * token刷新时间
     */
    private Integer refreshTokenValiditySeconds;

    /**
     * 自动授权的范围，该范围会与scope字段做比对，比对上了则会自动授权，否则手动授权，设置为true则全部自动授权
     */
    private Set<String> autoApproveScopes = Collections.emptySet();

    /**
     * 权限集合
     */
    private Set<GrantedAuthorityDTO> authorities = Collections.emptySet();

    /**
     * 其他条件信息
     */
    private Map<String, Object> additionalInformation;

    @Override
    public boolean isSecretRequired() {
        return this.clientSecret != null;
    }

    @Override
    public boolean isScoped() {
        return this.scope != null && !this.scope.isEmpty();
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return this.registeredRedirectUris;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return null;
    }



    @Override
    public boolean isAutoApprove(String scope) {
        if (autoApproveScopes == null) {
            return false;
        }
        for (String auto : autoApproveScopes) {
            if ("true".equals(auto) || scope.matches(auto)) {
                return true;
            }
        }
        return false;
    }
}
