package com.keyware.shandan.bianmu.controller;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.service.MetadataService;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.datasource.entity.DBTableColumnVo;
import com.keyware.shandan.datasource.entity.DataSourceVo;
import com.keyware.shandan.datasource.service.DataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * BianmuCommonController
 *
 * @author Administrator
 * @since 2021/12/15
 */
@RestController
@RequestMapping("/business/metadata")
public class BianmuCommonController {

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private DataSourceService dataSourceService;

    /**
     * 数据资源详细信息页面
     * @param mav
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    public ModelAndView metadataDetails(ModelAndView mav, @PathVariable String id){
        mav.setViewName("review/metadataDetails");
        MetadataBasicVo metadata = metadataService.get(id).getData();
        mav.addObject("detailsData", metadata);

        // 查询字段列
        List<DBTableColumnVo> columnVoList = new ArrayList<>();
        metadata.getMetadataDetailsList().forEach(list -> columnVoList.addAll(list.getColumnList()));
        mav.addObject("columnList", columnVoList);

        // 查询示例数据
        DataSourceVo dataSourceVo = dataSourceService.getById(metadata.getDataSourceId());
        Page<HashMap<String, Object>> exampleData = metadataService.getExampleData(metadata, dataSourceVo);
        mav.addObject("exampleList", exampleData.getRecords());

        return mav;
    }


    /**
     * 示例数据
     *
     * @param metadataId
     * @return
     */
    @GetMapping("/example/data")
    public Result<Page<HashMap<String, Object>>> exampleData(String metadataId) {
        MetadataBasicVo metadata = metadataService.get(metadataId).getData();
        DataSourceVo dataSource = dataSourceService.getById(metadata.getDataSourceId());
        Page<HashMap<String, Object>> result = metadataService.getExampleData(metadata, dataSource);
        return Result.of(result);
    }

    /**
     * 查询数据资源表字段列表
     *
     * @param id
     * @return
     */
    @GetMapping("/columns")
    public Result<JSONArray> columns(String id) {
        return Result.of(metadataService.getColumns(id));
    }

    /**
     * 根据目录ID查询数据资源列表
     *
     * @param directoryId
     * @return
     */
    @GetMapping("/list/directory")
    public Result<Page<MetadataBasicVo>> listByDirectory(Page<MetadataBasicVo> page, String directoryId, String metadataName) {
        if (StringUtils.isBlank(directoryId)) {
            return Result.of(null, false, "参数不能为空");
        }
        return Result.of(metadataService.pageListByDirectory(page, directoryId, metadataName));
    }
}
