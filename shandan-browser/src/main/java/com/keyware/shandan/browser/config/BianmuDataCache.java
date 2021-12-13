package com.keyware.shandan.browser.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.bianmu.entity.MetadataDetailsVo;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 分类编目数据缓存实体类
 *
 * @author GuoXin
 * @since 2021/7/6
 */
public class BianmuDataCache {
    /**
     * 数据资源表信息缓存
     */
    private final static ConcurrentHashMap<String, MetadataDetailsVo> cache = new ConcurrentHashMap<>();

    /**
     * 添加数据资源
     *
     * @param table -
     * @param vo    -
     */
    public static void put(String table, MetadataDetailsVo vo) {
        cache.put(table, vo);
    }

    /**
     * 根据数据资源表ID获取主表信息
     *
     * @param id -
     * @return -
     */
    public static MetadataDetailsVo get(String id) {
        return cache.values().stream().filter(item -> item.getMetadataId().equals(id) && item.getMaster()).findFirst().orElse(null);
    }

    /**
     * 获取数据资源表注释
     *
     * @param table 表名
     * @return --
     */
    public static String getComment(String table) {
        MetadataDetailsVo vo = cache.get(table);
        if (vo != null) {
            return vo.getTableComment();
        }
        return "";
    }

    /**
     * 获取表字段
     *
     * @param table 表名
     * @return -
     */
    public static JSONArray getColumns(String table) {
        JSONArray json = new JSONArray();
        MetadataDetailsVo vo = cache.get(table);
        if (vo != null) {
            return JSONArray.parseArray(vo.getTableColumns());
        }
        return json;
    }

    /**
     * 清空缓存数据
     */
    public static void clear() {
        cache.clear();
    }
}
