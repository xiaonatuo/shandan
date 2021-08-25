package com.keyware.shandan.control.controller;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.bianmu.entity.DirectoryMetadataVo;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.service.DirectoryMetadataService;
import com.keyware.shandan.bianmu.service.MetadataService;
import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.enums.SecretLevel;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.datasource.entity.DataSourceVo;
import com.keyware.shandan.datasource.service.DataSourceService;
import com.keyware.shandan.system.entity.SysFile;
import com.keyware.shandan.system.service.SysFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 元数据管理前端控制器
 * </p>
 *
 * @author Administrator
 * @since 2021/5/25
 */
@RestController
@RequestMapping("/business/metadata")
public class MetadataController extends BaseController<MetadataService, MetadataBasicVo, String> {

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private DirectoryMetadataService directoryMetadataService;

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private DataSourceService dataSourceService;

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
     * 查询元数据表字段列表
     *
     * @param id
     * @return
     */
    @GetMapping("/columns")
    public Result<JSONArray> columns(String id) {
        return Result.of(metadataService.getColumns(id));
    }

    /**
     * 根据目录ID查询元数据列表
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
