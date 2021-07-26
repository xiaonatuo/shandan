package com.keyware.shandan.common.entity;

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
public class OperateLog implements Serializable {
    private static final long serialVersionUID = 6540421642442208365L;

    /**
     * 登录名
     */
    private String loginName;

    /**
     * IP地址
     */
    private String ipAddr;

    /**
     * 操作行为
     */
    private String operate;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 参数
     */
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
