package com.keyware.shandan.beans;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
@TableName(value = "SYS_USER")
public class UserVo implements Serializable {
    private static final long serialVersionUID = -3768119259408441301L;
    /**
     * 用户id
     */
    @TableId("USER_ID")
    private String userId;

    /**
     * 登录名
     */
    @TableField(value="LOGIN_NAME", condition = SqlCondition.LIKE)
    private String loginName;

    /**
     * 用户名称
     */
    @TableField(value = "USER_NAME", condition = SqlCondition.LIKE)
    private String userName;

    /**
     * 登录密码
     */
    @TableField("PASSWORD")
    private String password;

    /**
     * 是否允许登录
     */
    @TableField(value = "VALID")
    private Boolean valid;

    /**
     * 限制允许登录的IP集合
     */
    @TableField("LIMITED_IP")
    private String limitedIp;

    /**
     * 账号失效时间，超过时间将不能登录系统
     */
    @TableField("EXPIRED_TIME")
    private Date expiredTime;

    /**
     * 最近修改密码时间，超出时间间隔，提示用户修改密码
     */
    @TableField("LAST_CHANGE_PWD_TIME")
    private Date lastChangePwdTime;

    /**
     * 是否允许账号同一个时刻多人在线
     */
    @TableField("LIMIT_MULTI_LOGIN")
    private Boolean limitMultiLogin;

    /**
     * 部门主键
     */
    @TableField(value = "ORG_ID")
    private String orgId;

    /**
     * 逻辑删除
     */
    @TableLogic(value = "0", delval = "1")
    @TableField(value = "IS_DELETE")
    private Boolean deleted;
}
