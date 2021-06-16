package com.keyware.shandan.datasource.entity;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.keyware.shandan.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
/**
 * <p>
 * 数据源表
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("B_DATA_SOURCE")
public class DataSourceVo extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("ID")
    private String id;

    /**
     * 数据源名称
     */
    @TableField(value = "NAME", condition = SqlCondition.LIKE)
    private String name;

    /**
     * 数据源类型(0：关系型数据库，1：非关系型数据库，2：文件存储)
     */
    @TableField("TYPE")
    private String type;

    /**
     * 数据库类型(ORACLE、MYSQL、DM、SQLSERVER等)
     */
    @TableField("DB_TYPE")
    private String dbType;

    /**
     * 数据库版本
     */
    @TableField("DB_VERSION")
    private String dbVersion;

    /**
     * JDBC数据库驱动
     */
    @TableField("JDBC_DRIVER_CLASS")
    private String jdbcDriverClass;

    /**
     * JDBC数据库连接
     */
    @TableField("JDBC_URL")
    private String jdbcUrl;

    /**
     * JDBC数据库用户名
     */
    @TableField("JDBC_USER_NAME")
    private String jdbcUserName;

    /**
     * JDBC数据库密码
     */
    @TableField("JDBC_PASSWORD")
    private String jdbcPassword;

    /**
     * JDBC数据库模式
     */
    @TableField("JDBC_SCHEMA")
    private String jdbcSchema;

    /**
     * 主机
     */
    @TableField("HOST")
    private String host;

    /**
     * 端口
     */
    @TableField("PORT")
    private String port;

    /**
     * 描述
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 是否删除（0：存在，1：已删除）
     */
    @TableField("IS_DELETE")
    private Integer deleted;


    @TableField(exist = false)
    private String poolName;

    @TableField(exist = false)
    private String driverClassName;

    @TableField(exist = false)
    private String url;

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String password;

    @TableField(exist = false)
    private String schema;

    public void setId(String id){
        this.poolName = id;
        this.id = id;
    }

    public void setJdbcDriverClass(String jdbcDriverClass) {
        this.driverClassName = jdbcDriverClass;
        this.jdbcDriverClass = jdbcDriverClass;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.url = jdbcUrl;
        this.jdbcUrl = jdbcUrl;
    }

    public void setJdbcUserName(String jdbcUserName) {
        this.username = jdbcUserName;
        this.jdbcUserName = jdbcUserName;
    }

    public void setJdbcPassword(String jdbcPassword) {
        this.password = jdbcPassword;
        this.jdbcPassword = jdbcPassword;
    }

    public void setJdbcSchema(String jdbcSchema) {
        //this.schema = jdbcSchema;
        this.jdbcSchema = jdbcSchema;
    }
}
