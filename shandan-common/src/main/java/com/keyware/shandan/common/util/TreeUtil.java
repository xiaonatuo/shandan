package com.keyware.shandan.common.util;

import com.keyware.shandan.common.entity.TreeVo;
import com.sun.org.apache.xml.internal.utils.StringComparable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 树形组件工具类
 *
 * @author GuoXin
 * @since 2021/11/28
 */
public class TreeUtil {
    /**
     * 构建树形结构数据
     *
     * @param nodes    准备构建树形结构的数据节点
     * @param parentId 树形结构父级节点ID
     * @return 树形结构数据
     */
    public static List<TreeVo> buildDirTree(@NonNull List<TreeVo> nodes, @Nullable String parentId) {
        List<TreeVo> tree = new ArrayList<>();

        if (!StringUtils.isEmpty(parentId)) {
            //根据父级目录的路径查找父级节点
            Optional<TreeVo> parent = nodes.stream().filter(vo -> parentId.equals(vo.getId())).findFirst();
            if (parent.isPresent()) {
                TreeVo parentNode = parent.get();
                // 将父级节点从nodes中移除
                List<TreeVo> newNodes = nodes.stream().filter(node -> !node.getId().equals(parentNode.getId())).collect(Collectors.toList());
                // 根据父级节点ID查找子级节点
                List<TreeVo> childrenNodes = newNodes.stream().filter(node -> parentNode.getId().equals(node.getParentId())).collect(Collectors.toList());

                tree = childrenNodes.stream().peek(children -> children.setChildren(buildDirTree(newNodes, children.getId()))).collect(Collectors.toList());
            }
        }
        return treeCompare(tree);
    }

    /**
     * 构建树形结构数据
     *
     * @param nodes 准备构建树形结构的数据节点
     * @return 树形结构数据
     */
    public static List<TreeVo> buildDirTree(@NonNull List<TreeVo> nodes) {
        List<TreeVo> tree = new ArrayList<>();
        Stream<TreeVo> nodesStream = nodes.stream();
        // 从所有节点中找出父节点不在集合中的节点
        List<String> allId = nodes.stream().map(TreeVo::getId).collect(Collectors.toList());
        nodesStream.filter(node -> (!allId.contains(node.getParentId()))).forEach(node -> {
            node.setChildren(buildDirTree(nodes, node.getId()));
            tree.add(node);
        });

        return treeCompare(tree);
    }

    /**
     * 树节点根据中文排序
     *
     * @param tree
     * @return
     */
    private static List<TreeVo> treeCompare(List<TreeVo> tree) {
        return tree.stream().sorted((tree1, tree2) -> {
            Collator collator = Collator.getInstance(Locale.CHINA);
            return collator.compare(tree1.getTitle(), tree2.getTitle());
        }).collect(Collectors.toList());
    }
}
