package com.keyware.shandan.control.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Application
 *
 * @author Administrator
 * @since 2021/12/9
 */
@Data
public class Application implements Serializable {

    private static final long serialVersionUID = -5708238376036987775L;




    @Getter
    @Setter
    static class OauthClient{
        private String clientId;
        private String clientSecret;
        private String scope = "all";
        private String authorizedGrantTypes = "authorization_code,refresh_token";
        private String registeredRedirectUris;
        private Integer accessTokenValiditySeconds;
        private Integer refreshTokenValiditySeconds;
        private String autoApproveScopes = "true";
    }
}
