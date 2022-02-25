package com.keyware.shandan.bianmu.utils;

import com.keyware.shandan.bianmu.entity.DirectoryVo;
import com.keyware.shandan.bianmu.entity.MetadataAndFileVo;
import com.keyware.shandan.common.entity.TreeVo;
import com.keyware.shandan.common.util.StringUtils;

/**
 * DirectoryUtil
 *
 * @author Administrator
 * @since 2021/11/28
 */
public class DirectoryUtil {

    /**
     * 目录实体转换为树组件实体
     *
     * @param dir 目录实体
     * @return 树组件实体
     */
    public static TreeVo dir2Tree(DirectoryVo dir) {
        TreeVo tree = new TreeVo();
        if (dir != null) {
            tree.setId(dir.getId());
            tree.setParentId(dir.getParentId());
            tree.setTitle(dir.getDirectoryName());
            tree.setType("DIRECTORY");
            tree.setPath(dir.getDirectoryPath());
            tree.setBasicData(dir);
            /*if (!"-".equals(dir.getParentId()) && !dir.getHasChild()) {
                tree.setLast(true);
            }*/
            tree.setLast(false);
        }
        return tree;
    }

    /**
     * @param mfv
     * @return
     */
    public static TreeVo metaAndFile2Tree(MetadataAndFileVo mfv) {
        TreeVo tree = new TreeVo();
        if (mfv != null) {
            tree.setId(mfv.getId());
            tree.setParentId(mfv.getDirectoryId());
            tree.setTitle(StringUtils.isBlank(mfv.getMetadataComment()) ? mfv.getMetadataName() : mfv.getMetadataComment());
            tree.setType("METADATA");
            tree.setPath("");
            tree.setBasicData(mfv);
            tree.setLast(true);
            tree.setIconClass("dtree-icon-normal-file");
        }
        return tree;
    }
}
