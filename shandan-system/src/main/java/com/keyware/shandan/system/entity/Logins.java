package com.keyware.shandan.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * persistent_logins表，用户实现记住我功能
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("PERSISTENT_LOGINS")
public class Logins implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableField("SERIES")
    private String SERIES;

    /**
     * 登陆账号
     */
    @TableField("USERNAME")
    private String USERNAME;

    /**
     * cookie令牌
     */
    @TableField("TOKEN")
    private String TOKEN;

    /**
     * 最后更新时间
     */
    @TableField("LAST_USED")
    private Date lastUsed;

}
