package com.keyware.shandan.desktop.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用桌面设置
 *
 * @author GuoXin
 * @since 2021/7/22
 */
@Data
public class DesktopSetting {
    private List<Setting> settings = new ArrayList<>();

    /**
     * 获取key-value结构的应用桌面设置
     * @return key-value结构的应用桌面设置
     */
    public Map<String, Object> getSettingMap() {
        Map<String, Object> map = new HashMap<>();
        settings.forEach(set -> map.put(set.getKey(), set.getValue()));
        return map;
    }

    @Getter
    @Setter
    @TableName("SYS_DESKTOP_SETTING")
    public static class Setting implements Serializable {
        private static final long serialVersionUID = -2369715657070077236L;

        @TableId("KEY")
        private String key;

        @TableField("VALUE")
        private String value;

        public Setting(){}

        public Setting(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
