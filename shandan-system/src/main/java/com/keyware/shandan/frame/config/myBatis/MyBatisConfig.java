package com.keyware.shandan.frame.config.myBatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.system.entity.SysUser;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Date;

@Configuration
@EnableTransactionManagement
public class MyBatisConfig {

    /*@Autowired
    private PermissionsInnerInterceptor permissionsInnerInterceptor;*/

    /**
     * 事务管理器
     *
     * @param dataSource
     * @return
     */
    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager(
            DataSource dataSource) {
        DataSourceTransactionManager manager = new DataSourceTransactionManager();
        manager.setDataSource(dataSource);
        return manager;
    }

    /**
     * Mybatis 拦截器配置
     *
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 分页配置
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.DM));

        // 权限过滤配置
        interceptor.addInnerInterceptor(permissionsInnerInterceptor());
        return interceptor;
    }

    @Bean
    public PermissionsInnerInterceptor permissionsInnerInterceptor() {
        return new PermissionsInnerInterceptor();
    }

    /**
     * MyBatis实体类公共字段填充配置
     */
    @Component
    class MetaObjectHandlerConfig implements MetaObjectHandler {

        @Override
        public void insertFill(MetaObject metaObject) {
            this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
            this.strictInsertFill(metaObject, "modifyTime", Date.class, new Date());
            SysUser user = SecurityUtil.getLoginSysUser();
            if (user != null) {
                this.strictInsertFill(metaObject, "createUser", String.class, user.getUserId());
                this.strictInsertFill(metaObject, "modifyUser", String.class, user.getUserId());
                this.strictInsertFill(metaObject, "orgId", String.class, user.getOrgId());
            }
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            this.strictInsertFill(metaObject, "modifyTime", Date.class, new Date());
            SysUser user = SecurityUtil.getLoginSysUser();
            if (user != null) {
                String currentUserId = SecurityUtil.getLoginSysUser().getUserId();
                this.strictInsertFill(metaObject, "modifyUser", String.class, currentUserId);
            }
        }
    }
}
