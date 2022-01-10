package com.keyware.shandan.bianmu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.bianmu.entity.DirectoryMetadataVo;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.service.DirectoryMetadataService;
import com.keyware.shandan.bianmu.service.MetadataService;
import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.enums.SecretLevel;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.system.entity.SysFile;
import com.keyware.shandan.system.service.SysFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 数据资源管理前端控制器
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
    public ModelAndView index(ModelAndView mov) {
        mov.setViewName("business/metadata/metadata");
        Map<String, String> map = new HashMap<>();
        Arrays.stream(SecretLevel.values()).forEach(secret -> map.put(secret.name(), secret.getDesc()));
        mov.addObject("SecretLevel", map);
        return mov;
    }

    @GetMapping("/edit")
    public ModelAndView edit(ModelAndView mov) {
        mov.setViewName("business/metadata/metadataEdit");
        return mov;
    }

    @GetMapping("/example")
    public ModelAndView example() {
        return new ModelAndView("business/metadata/example");
    }

    /**
     * 数据资源选择框页面
     *
     * @return
     */
    @GetMapping("/layer/choose")
    public ModelAndView chooseLayer() {
        return new ModelAndView("business/metadata/chooseLayer");
    }

    /**
     * 保存数据资源基础信息和详情表数据
     *
     * @param metadataBasic
     * @return
     */
    @PostMapping("/save/full")
    public Result<Boolean> saveFull(MetadataBasicVo metadataBasic) {
        return Result.of(metadataService.saveMetadataBasicAndDetailsList(metadataBasic));
    }

    @PostMapping("/save/all")
    public Result<Boolean> saveFullTest(@RequestBody MetadataBasicVo metadataBasic) {
        return Result.of(metadataService.saveMetadataBasicAndDetailsList(metadataBasic));
    }

    @GetMapping("/get/page")
    public Result<Object> getByPage(@RequestParam(value = "page", defaultValue = "1") int page,
                                    @RequestParam(value = "size", defaultValue = "10") int size) {
        return Result.of(metadataService.page(new Page<>(page, size)));
    }


    /**
     * 保存数据资源及文件，并关联目录
     *
     * @param metadata    数据资源
     * @param directoryId 目录ID
     * @param fileIds     文件ID
     * @return
     */
    @PostMapping("/save/file")
    public Result<Boolean> saveMetadataAndFiles(MetadataBasicVo metadata, String directoryId, String fileIds) {
        if (StringUtils.isBlankAny(directoryId, fileIds)) {
            return Result.of(null, false, "参数错误");
        }
        // 保存数据资源
        if (metadataService.save(metadata)) {
            // 保存目录数据资源关系
            directoryMetadataService.save(new DirectoryMetadataVo(directoryId, metadata.getId()));
            //更新文件信息
            List<SysFile> files = sysFileService.listByIds(Arrays.asList(fileIds.split(",")));
            files.stream().peek(file -> file.setEntityId(metadata.getId())).collect(Collectors.toList());
            sysFileService.updateBatchById(files);
        }
        return Result.of(null);
    }

}
