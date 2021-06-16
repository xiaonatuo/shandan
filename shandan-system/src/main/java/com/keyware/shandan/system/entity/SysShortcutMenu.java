package com.keyware.shandan.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.keyware.shandan.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 用户快捷菜单表
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "SYS_SHORTCUT_MENU", resultMap = "BaseResultMap")
public class SysShortcutMenu extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户快捷菜单id
     */
    @TableId("SHORTCUT_MENU_ID")
    private String shortcutMenuId;

    /**
     * 用户快捷菜单名称
     */
    @TableField("SHORTCUT_MENU_NAME")
    private String shortcutMenuName;

    /**
     * 用户快捷菜单路径
     */
    @TableField("SHORTCUT_MENU_PATH")
    private String shortcutMenuPath;

    /**
     * 用户id
     */
    @TableField("USER_ID")
    private String userId;

    /**
     * 上级id
     */
    @TableField("SHORTCUT_MENU_PARENT_ID")
    private String shortcutMenuParentId;

    /**
     * 同级排序权重：0-10
     */
    @TableField("SORT_WEIGHT")
    private Integer sortWeight;

    /**
     * 子菜单
     */
    @TableField(exist = false)
    private List<SysShortcutMenu> children;
}
