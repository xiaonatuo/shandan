package com.keyware.shandan.datasource.service.impl;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.keyware.shandan.datasource.entity.DataSourceVo;
import com.keyware.shandan.datasource.mapper.DataSourceMapper;
import com.keyware.shandan.datasource.service.DataSourceService;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.common.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 数据源表 服务实现类
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-11
 */
@Service
public class DataSourceServiceImpl extends BaseServiceImpl<DataSourceMapper, DataSourceVo, String> implements DataSourceService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private DefaultDataSourceCreator dataSourceCreator;


    @Override
    public Result<DataSourceVo> updateOrSave(DataSourceVo entity) {
        String url = entity.getJdbcUrl();
        Pattern pattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)\\:(\\d+)");
        Matcher match = pattern.matcher(url);
        if(match.find()){
            entity.setHost(match.group(1));
            entity.setPort(match.group(2));
        }else{
            return Result.of(null, false, "数据库连接格式错误");
        }

        // 如果是更新，先移除旧的数据源
        if(StringUtils.isNotBlank(entity.getId())){
            removeDataSource(entity.getId());
        }

        if(saveOrUpdate(entity)){
            try {
                addDataSource(entity);
                return Result.of(entity, true);
            } catch (Exception e) {
                return Result.of(entity, false, e.getMessage());
            }
        }else{
            return Result.of(entity, false, "保存失败");
        }
    }

    @Override
    public void loadDataSource() {
        List<DataSourceVo> dsList = list();
        if (dsList != null) {
            dsList.forEach(ds -> {
                try {
                    addDataSource(ds);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * 测试数据源连接
     * @param dataSourceVo
     * @return
     */
    @Override
    public Result<Boolean> testDataSource(DataSourceVo dataSourceVo) {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        BeanUtils.copyProperties(dataSourceVo, dataSourceProperty);
        Connection con = null;
        try {
            con = dataSourceCreator.createDataSource(dataSourceProperty).getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return Result.of(false, false, "连接失败：" + throwables.getMessage());
        }finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return Result.of(true, true);
    }

    /**
     * 添加一个数据源
     * @param dataSourceVo 数据源实体
     */
    private void addDataSource(DataSourceVo dataSourceVo) throws SQLException {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        BeanUtils.copyProperties(dataSourceVo, dataSourceProperty);
        dataSourceProperty.setLazy(true); // 设置为懒启动
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        DataSource dataSource = dataSourceCreator.createDataSource(dataSourceProperty);
        ds.addDataSource(dataSourceVo.getId(), dataSource);
    }


    /**
     * 移除一个数据源
     * @param dsId 数据源ID
     */
    private void removeDataSource(String dsId){
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        ds.removeDataSource(dsId);
    }
}
