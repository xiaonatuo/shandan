package com.keyware.shandan.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.keyware.shandan.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 系统用户与Oauth2客户端关系表
 * </p>
 *
 * @author fengzhi
 * @since 2021-12-13
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "SYS_USER_CLIENT", resultMap = "BaseResultMap")
public class SysUserClient extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关系主键
     */
    @TableId("USER_CLIENT_ID")
    private String userClientId;

    /**
     * 用户ID
     */
    @TableField("USER_ID")
    private String userId;

    /**
     * 客户端ID
     */
    @TableField("CLIENT_ID")
    private String clientId;

    /**
     * 创建时间
     */
    @TableField("CREATE_TIME")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("MODIFY_TIME")
    private Date modifyTime;

    /**
     * 创建用户
     */
    @TableField("CREATE_USER")
    private String createUser;

    /**
     * 修改用户
     */
    @TableField("MODIFY_USER")
    private String modifyUser;


    /**
     * 客户端
     */
    @TableField(exist = false)
    private OauthClientDetails client;

    /**
     * 用户对象
     */
    @TableField(exist = false)
    private SysUser user;


    /**
     * 客户端Id列表，用于接收前端传参
     */
    @TableField(exist = false)
    public String clientIdList;

    @Override
    public String toString() {
        return "SysUserClient{" +
        "userClientId=" + userClientId +
        ", userId=" + userId +
        ", clientId=" + clientId +
        ", createTime=" + createTime +
        ", modifyTime=" + modifyTime +
        ", createUser=" + createUser +
        ", modifyUser=" + modifyUser +
        "}";
    }
}
