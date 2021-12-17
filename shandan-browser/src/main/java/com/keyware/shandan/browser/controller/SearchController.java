package com.keyware.shandan.browser.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.service.MetadataService;
import com.keyware.shandan.browser.entity.SearchConditionVo;
import com.keyware.shandan.browser.entity.PageVo;
import com.keyware.shandan.browser.service.SearchService;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.frame.annotation.AppLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * 组合搜索前端控制器
 *
 * @author Administrator
 * @since 2021/7/1
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @Autowired
    private MetadataService metadataService;


    /**
     * 全文检索分页查询
     *
     * @param condition 条件
     * @return -
     */
    @AppLog(operate = "全文检索分页查询")
    @PostMapping("/full")
    public Result<PageVo> fullSearch(SearchConditionVo condition) {

        try {
            return Result.of(searchService.esSearch(condition));
        } catch (IOException e) {
            e.printStackTrace();
            return Result.of(null, false, "数据查询异常");
        }
    }


    /**
     * 测试接口
     *
     * @param condition
     * @return
     */
    @PostMapping("/full/test")
    public Result<PageVo> fullSearchTest(@RequestBody SearchConditionVo condition) {

        try {
            return Result.of(searchService.esSearch(condition));
        } catch (IOException e) {
            e.printStackTrace();
            return Result.of(null, false, "数据查询异常");
        }
    }

    @GetMapping("/metadata/columns")
    public Result<JSONObject> getMetaColumns(String metaTable){
        QueryWrapper<MetadataBasicVo> qw = new QueryWrapper<>();
        qw.eq("METADATA_NAME", metaTable);
        List<MetadataBasicVo> list = metadataService.list(qw);
        if(list != null && list.size() > 0) {
            MetadataBasicVo basicVo = list.get(0);
            JSONObject json = (JSONObject) JSONObject.toJSON(basicVo);
            json.put("field", getFiledByMetadata(basicVo));
            return Result.of(json);
        }
        return Result.of(null, false);
    }

    /**
     * 查询指定数据资源表的分页数据
     * @param metaID
     * @param page
     * @param size
     * @param orderField
     * @param order
     * @return
     */
    @PostMapping("/metadata/page")
    public Result<PageVo> dataPageByMetadata(String metaID, Integer page, Integer size, String orderField, String order) {
        MetadataBasicVo basicVo = metadataService.getById(metaID);
        if(basicVo != null){
            Page<HashMap<String, Object>> dataList = metadataService.getDynamicData(basicVo, page, size, orderField, order);
            return Result.of(PageVo.pageConvert(dataList));
        }

        return Result.of(null, false);
    }

    private JSONArray getFiledByMetadata(MetadataBasicVo basicVo) {
        JSONArray jsonArray = new JSONArray();
        basicVo.getMetadataDetailsList().forEach(detail ->{
            jsonArray.addAll(JSONArray.parseArray(detail.getTableColumns()));
        });
        return jsonArray;
    }


    /**
     * 全文检索-检索文件
     * @return 文件结果列表
     */
    @GetMapping("/full/file")
    public Result<Object> searchFile(){

        return Result.of(null);
    }
}
