package com.keyware.shandan.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.keyware.shandan.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 系统菜单表
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "SYS_MENU", resultMap = "BaseResultMap")
public class SysMenu extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单id
     */
    @TableId("MENU_ID")
    private String menuId;

    /**
     * 菜单名称
     */
    @TableField(value = "MENU_NAME", condition = SqlCondition.LIKE)
    private String menuName;

    /**
     * 菜单路径
     */
    @TableField("MENU_PATH")
    private String menuPath;

    /**
     * 上级id
     */
    @TableField("MENU_PARENT_ID")
    private String menuParentId;

    /**
     * 上级菜单名称
     */
    @TableField(exist = false)
    private String menuParentName;

    /**
     * 上级菜单对象
     */
    @TableField(exist = false)
    private SysMenu parentMenu;

    /**
     * 同级排序权重：0-10
     */
    @TableField(value = "SORT_WEIGHT")
    @OrderBy
    private Integer sortWeight;

    @TableField("IS_DELETE")
    private Boolean deleted;

    /**
     * 子级菜单
     */
    @TableField(exist = false)
    private List<SysMenu> children;

    /**
     * 获取上级菜单名称
     * @return
     */
    public String getMenuParentName() {
        if (parentMenu == null) {
            return "";
        }
        return parentMenu.getMenuName();
    }

    /**
     * 是否选中，0为不选中，用于前端树组件使用
     */
    @TableField(exist = false)
    public String checked = "0";
}
