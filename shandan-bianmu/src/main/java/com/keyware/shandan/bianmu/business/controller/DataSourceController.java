package com.keyware.shandan.bianmu.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.datasource.entity.DBUserTableVo;
import com.keyware.shandan.datasource.entity.DataSourceVo;
import com.keyware.shandan.datasource.service.DataSourceService;
import com.keyware.shandan.bianmu.business.service.DynamicDataSourceService;
import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * 数据源表 前端控制器
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-11
 */
@RestController
@RequestMapping("/business/datasource")
public class DataSourceController extends BaseController<DataSourceService, DataSourceVo, String> {

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private DynamicDataSourceService dynamicDataSourceService;

    @GetMapping("/")
    public ModelAndView index(ModelAndView modelAndView){
        modelAndView.setViewName("business/datasource/datasource");
        return modelAndView;
    }

    @GetMapping("/edit")
    public ModelAndView edit(ModelAndView modelAndView){
        modelAndView.setViewName("business/datasource/datasourceEdit");
        return modelAndView;
    }

    @GetMapping("/selectTableLayer")
    public ModelAndView selectTable(){
        return new ModelAndView("business/datasource/selectTableLayer");
    }

    /**
     * 测试连接
     * @param dataSourceVo
     * @return
     */
    @PostMapping("/connection/test")
    public Result<Boolean> testConnection(DataSourceVo dataSourceVo){
        return dataSourceService.testDataSource(dataSourceVo);
    }

    /**
     * 根据数据源ID查询所有数据表
     * @param datasourceId
     * @return
     */
    @PostMapping("/table/list")
    public Result<Page<DBUserTableVo>> tableList(Page<DBUserTableVo> page, DBUserTableVo table, String datasourceId){
        if(StringUtils.isBlank(datasourceId)){
            return Result.of(null, false, "datasourceId 参数不能为空");
        }

        return Result.of(dynamicDataSourceService.getDBTablesPage(page, table, datasourceId));
    }
}
