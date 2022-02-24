package com.keyware.shandan.system.utils;

import com.keyware.shandan.common.constants.SystemConstants;
import com.keyware.shandan.common.util.StreamUtil;
import com.keyware.shandan.system.entity.SysDict;
import com.keyware.shandan.system.service.SysDictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.logging.log4j.util.Strings;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据字典工具类
 *
 * @author GuoXin
 */
@Slf4j
@Component
public class DictUtil {

    private static final MultiKeyMap<String, SysDict> DICT_CACHE = new MultiKeyMap<>();


    private final SysDictService dictService;

    public DictUtil(SysDictService dictService) {
        this.dictService = dictService;
    }

    /**
     * 初始化字典项缓存
     */
    @PostConstruct
    synchronized public void initDictCache() {
        DICT_CACHE.clear();
        List<SysDict> dicts = dictService.list();
        StreamUtil.as(dicts).forEach(dict -> {
            DICT_CACHE.put(dict.getTypeId(), dict.getDictCode(), dict);
        });
        log.info("数据字典初始化完成！");
    }

    /**
     * 获取所有字典项集合
     *
     * @return 集合
     */
    public static List<SysDict> getAllDictList() {
        return new ArrayList<>(DICT_CACHE.values());
    }

    /**
     * 根据字典类型和字典编码获取字典项
     *
     * @param type 字典类型
     * @param code 字典编码
     * @return 字典项
     */
    public static SysDict getDict(String type, String code) {
        return DICT_CACHE.get(type, code);
    }

    /**
     * 根据字典类型获取字典项集合
     *
     * @param type 类型
     * @return 集合
     */
    public static List<SysDict> getDictListByType(@NonNull String type) {
        return StreamUtil.as(DICT_CACHE.values()).filter(dict -> type.equals(dict.getTypeId())).toList();
    }

    /**
     * 获取公共配置字典项
     *
     * @param code 字典编码
     * @return 配置项的值
     */
    public static String getPublicDict(@NonNull String code) {
        SysDict dict = DICT_CACHE.get(SystemConstants.DICT_TYPE_PUBLIC_SETTING, code);
        return dict == null ? Strings.EMPTY : dict.getValue();
    }
}
