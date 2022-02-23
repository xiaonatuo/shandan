package com.keyware.shandan.system.entity;

import com.baomidou.mybatisplus.annotation.*;
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
    private String scope = "all";

    @TableField("AUTHORIZED_GRANT_TYPES")
    private String authorizedGrantTypes = "authorization_code,refresh_token";

    @TableField("WEB_SERVER_REDIRECT_URI" )
    private String webServerRedirectUri;

    @TableField("AUTHORITIES")
    private String authorities = "all";

    @TableField("ACCESS_TOKEN_VALIDITY")
    private Long accessTokenValidity = 7200L;

    @TableField("REFRESH_TOKEN_VALIDITY")
    private Long refreshTokenValidity = 7200L;

    @TableField("ADDITIONAL_INFORMATION")
    private String additionalInformation;

    @TableField("AUTOAPPROVE")
    private String autoapprove = "true";

    @TableField("WEB_CLIENT_LOGOUT_URI")
    private String webClientLogoutUri;

    /**
     * 应用标题
     */
    @TableField("TITLE")
    private String title;

    /**
     * 应用打开方式
     */
    @TableField("TARGET")
    private String target;

    /**
     * 应用图标
     */
    @TableField("ICON")
    private String icon;

    /**
     * 应用排列顺序
     */
    @TableField("SORT")
    @OrderBy(isDesc = false)
    private String sort;

    @Override
    public String toString() {
        return "OauthClientDetails{" +
                "id='" + id + '\'' +
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
                ", title='" + title + '\'' +
                ", target='" + target + '\'' +
                ", icon='" + icon + '\'' +
                ", sort='" + sort + '\'' +
                '}';
    }
}
