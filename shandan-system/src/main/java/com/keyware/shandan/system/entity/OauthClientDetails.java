package com.keyware.shandan.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * OAUTH_CLIENT_DETAILS
 * </p>
 *
 * @author fengzhi
 * @since 2021-12-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "OAUTH_CLIENT_DETAILS", resultMap = "BaseResultMap")
public class OauthClientDetails implements Serializable {

    private static final long serialVersionUID =  2283589860793334842L;

    /**
     * 主键
     */
    @TableId(value = "CLIENT_ID", type = IdType.INPUT)
    private String id;

    @TableField("CLIENT_SECRET")
    private String clientSecret;

    @TableField("RESOURCE_IDS" )
    private String resourceIds;

    @TableField("SCOPE")
    private String scope;

    @TableField("AUTHORIZED_GRANT_TYPES")
    private String authorizedGrantTypes;

    @TableField("WEB_SERVER_REDIRECT_URI" )
    private String webServerRedirectUri;

    @TableField("AUTHORITIES")
    private String authorities;

    @TableField("ACCESS_TOKEN_VALIDITY")
    private Long accessTokenValidity;

    @TableField("REFRESH_TOKEN_VALIDITY")
    private Long refreshTokenValidity;

    @TableField("ADDITIONAL_INFORMATION")
    private String additionalInformation;

    @TableField("AUTOAPPROVE")
    private String autoapprove;

    @TableField("WEB_CLIENT_LOGOUT_URI")
    private String webClientLogoutUri;

    @Override
    public String toString() {
        return "ClientDetailsDTO{" +
                "clientId='" + id + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", resourceIds='" + resourceIds + '\'' +
                ", scope='" + scope + '\'' +
                ", authorizedGrantTypes='" + authorizedGrantTypes + '\'' +
                ", webServerRedirectUri='" + webServerRedirectUri + '\'' +
                ", authorities='" + authorities + '\'' +
                ", accessTokenValidity=" + accessTokenValidity +
                ", refreshTokenValidity=" + refreshTokenValidity +
                ", additionalInformation='" + additionalInformation + '\'' +
                ", autoapprove='" + autoapprove + '\'' +
                ", webClientLogoutUri='" + webClientLogoutUri + '\'' +
                '}';
    }
}
