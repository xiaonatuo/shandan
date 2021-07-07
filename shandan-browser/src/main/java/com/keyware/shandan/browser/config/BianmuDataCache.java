package com.keyware.shandan.browser.config;

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
     * 元数据表信息缓存
     */
    private final static ConcurrentHashMap<String, MetadataDetailsVo> cache = new ConcurrentHashMap<>();

    /**
     * 添加元数据
     *
     * @param table -
     * @param vo    -
     */
    public static void put(String table, MetadataDetailsVo vo) {
        cache.put(table, vo);
    }

    /**
     * 获取指定key的元数据信息
     *
     * @param table -
     * @return -
     */
    public static MetadataDetailsVo get(String table) {
        return cache.get(table);
    }

    /**
     * 获取元数据表注释
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
    public static JSONObject getColumns(String table) {
        JSONObject json = new JSONObject();
        MetadataDetailsVo vo = cache.get(table);
        if (vo != null) {
            return JSONObject.parseObject(vo.getTableColumns());
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
