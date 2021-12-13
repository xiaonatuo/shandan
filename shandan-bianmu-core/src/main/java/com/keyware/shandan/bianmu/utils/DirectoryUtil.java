package com.keyware.shandan.bianmu.utils;

import com.keyware.shandan.bianmu.entity.DirectoryVo;
import com.keyware.shandan.common.entity.TreeVo;

/**
 * DirectoryUtil
 *
 * @author Administrator
 * @since 2021/11/28
 */
public class DirectoryUtil {

    public static TreeVo Dir2Tree(DirectoryVo dir) {
        TreeVo tree = new TreeVo();
        if (dir == null) return tree;
        tree.setId(dir.getId());
        tree.setParentId(dir.getParentId());
        tree.setTitle(dir.getDirectoryName());
        tree.setType("DIRECTORY");
        tree.setPath(dir.getDirectoryPath());
        tree.setBasicData(dir);
        if(!"-".equals(dir.getParentId()) && dir.getHasChild() ==false){
            tree.setLast(true);
        }
        return tree;
    }
}
