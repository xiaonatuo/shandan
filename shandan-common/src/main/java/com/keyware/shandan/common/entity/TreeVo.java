package com.keyware.shandan.common.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 属性组件对象
 *
 * @author GuoXin
 * @since 2021/11/28
 */
@Data
public class TreeVo implements Serializable {
    private static final long serialVersionUID = 8756292100100240518L;

    /**
     * 节点ID
     */
    private String id;
    /**
     * 父级节点ID
     */
    private String parentId;
    /**
     * 节点类型，资源目录||结构目录
     */
    private String type;
    /**
     * 节点名称
     */
    private String title;
    /**
     * 节点图表样式名称
     */
    private String iconClass;
    /**
     * 是否展开
     */
    private boolean spread;
    /**
     * 是否最后一级
     */
    private boolean last;
    /**
     * 节点路径
     */
    private String path;
    /**
     * 选中状态
     */
    private String checked = "0";
    /**
     * 节点原始数据
     */
    private Object basicData;
    /**
     * 节点的子级节点
     */
    private List<TreeVo> children;
}
