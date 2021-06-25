package com.keyware.shandan.bianmu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.bianmu.service.impl.MetadataDetailsService;
import com.keyware.shandan.datasource.entity.DBTableColumnVo;
import com.keyware.shandan.bianmu.entity.DirectoryMetadataVo;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.entity.MetadataDetailsVo;
import com.keyware.shandan.bianmu.service.DirectoryMetadataService;
import com.keyware.shandan.datasource.service.DynamicDataSourceService;
import com.keyware.shandan.bianmu.service.MetadataService;
import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.StringUtils;
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
import java.util.Optional;
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

    @GetMapping("/")
    public ModelAndView index() {
        return new ModelAndView("business/metadata/metadata");
    }

    @GetMapping("/edit")
    public ModelAndView edit() {
        return new ModelAndView("business/metadata/metadataEdit");
    }

    @GetMapping("/example")
    public ModelAndView example() {
        return new ModelAndView("business/metadata/example");
    }

    /**
     * 元数据选择框页面
     * @return
     */
    @GetMapping("/layer/choose")
    public ModelAndView chooseLayer() {
        return new ModelAndView("business/metadata/chooseLayer");
    }

    /**
     * 保存元数据基础信息和详情表数据
     *
     * @param metadataBasic
     * @return
     */
    @PostMapping("/save/full")
    public Result<Boolean> saveFull(MetadataBasicVo metadataBasic) {
        return Result.of(metadataService.saveMetadataBasicAndDetailsList(metadataBasic));
    }

    /**
     * 示例数据
     * @param metadataId
     * @return
     */
    @GetMapping("/example/data")
    public Result<Page<HashMap<String, Object>>> exampleData(String metadataId){
        MetadataBasicVo metadata = metadataService.get(metadataId).getData();
        Page<HashMap<String, Object>> result = metadataService.getExampleData(metadata, metadata.getDataSourceId());
        return Result.of(result);
    }

    /**
     * 查询元数据表字段列表
     * @param id
     * @return
     */
    @GetMapping("/columns")
    public Result<JSONArray> columns(String id){
        return Result.of(metadataService.getColumns(id));
    }

    /**
     * 根据目录ID查询元数据列表
     * @param directoryId
     * @return
     */
    @GetMapping("/list/directory")
    public Result<Page<MetadataBasicVo>> listByDirectory(Page<MetadataBasicVo> page, String directoryId, String metadataName){
        if(StringUtils.isBlank(directoryId)){
            return Result.of(null, false, "参数不能为空");
        }
        return Result.of(metadataService.pageListByDirectory(page, directoryId, metadataName));
    }

    /**
     * 保存元数据及文件，并关联目录
     * @param metadata 元数据
     * @param directoryId 目录ID
     * @param fileIds 文件ID
     * @return
     */
    @PostMapping("/save/file")
    public Result<Boolean> saveMetadataAndFiles(MetadataBasicVo metadata, String directoryId, String fileIds){
        if(StringUtils.isBlankAny(directoryId, fileIds)){
            return Result.of(null, false, "参数错误");
        }
        // 保存元数据
        if(metadataService.save(metadata)){
            // 保存目录元数据关系
            directoryMetadataService.save(new DirectoryMetadataVo(directoryId, metadata.getId()));
            //更新文件信息
            List<SysFile> files = sysFileService.listByIds(Arrays.asList(fileIds.split(",")));
            files.stream().peek(file -> file.setEntityId(metadata.getId())).collect(Collectors.toList());
            sysFileService.updateBatchById(files);
        }
        return Result.of(null);
    }

}
