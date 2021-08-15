package com.keyware.shandan.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 应用操作日志实体类
 *
 * @author GuoXin
 * @since 2021/7/26
 */
@Data
@TableName("SYS_OPERATE_LOG")
public class SysOperateLog implements Serializable {
    private static final long serialVersionUID = 6540421642442208365L;

    @TableId("ID")
    private String id;

    /**
     * 登录名
     */
    @TableField("LOGIN_NAME")
    private String loginName;

    /**
     * IP地址
     */
    @TableField("IP_ADDR")
    private String ipAddr;

    /**
     * 请求路径
     */
    private String url;

    /**
     * 操作行为
     */
    @TableField("OPERATE")
    private String operate;

    /**
     * 操作时间
     */
    @TableField("OPERATE_TIME")
    private Date operateTime;

    /**
     * 参数
     */
    @TableField("PARAMS")
    private String params;

    @Override
    public String toString() {
        return "OperateLog{" +
                "loginName='" + loginName + '\'' +
                ", ipAddr='" + ipAddr + '\'' +
                ", operate='" + operate + '\'' +
                ", operateTime=" + operateTime +
                ", params='" + params + '\'' +
                '}';
    }
}
