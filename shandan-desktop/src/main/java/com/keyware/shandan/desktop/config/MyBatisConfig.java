package com.keyware.shandan.desktop.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Date;

//@Configuration
public class MyBatisConfig {
    /**
     * MyBatis实体类公共字段填充配置
     */
    @Component
    class MetaObjectHandlerConfig implements MetaObjectHandler {

        @Override
        public void insertFill(MetaObject metaObject) {
            this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
            this.strictInsertFill(metaObject, "modifyTime", Date.class, new Date());
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            this.strictInsertFill(metaObject, "modifyTime", Date.class, new Date());
        }
    }
}
