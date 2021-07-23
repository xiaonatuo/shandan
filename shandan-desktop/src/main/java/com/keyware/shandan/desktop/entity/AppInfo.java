package com.keyware.shandan.desktop.entity;
import com.baomidou.mybatisplus.annotation.OrderBy;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
/**
 * <p>
 * 应用桌面系统应用信息表
 * </p>
 *
 * @author GuoXin
 * @since 2021-07-22
 */
@Data
@TableName("SYS_DESKTOP_APPS")
public class AppInfo implements Serializable {

    private static final long serialVersionUID = 4696588358638763402L;

    /**
     * 主键
     */
    @TableId("ID")
    private Integer id;

    /**
     * 应用标题
     */
    @TableField("TITLE")
    private String title;

    /**
     * 应用访问地址
     */
    @TableField("URL")
    private String url;

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
    private Integer sort;

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

}
