package com.keyware.shandan.bianmu.controller;

import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.bianmu.entity.MetadataDetailsVo;
import com.keyware.shandan.bianmu.service.impl.MetadataDetailsService;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 关联关系分析
 * </p>
 *
 * @author Administrator
 * @since 2021/6/24
 */
@RestController
@RequestMapping("/business/analysis")
public class AnalysisController {

    @Autowired
    private MetadataDetailsService metadataDetailsService;

    /**
     * 页面
     *
     * @param modelAndView
     * @return
     */
    @GetMapping("/")
    public ModelAndView index(ModelAndView modelAndView) {
        modelAndView.setViewName("business/analysis/analysis");
        return modelAndView;
    }

    @PostMapping("/data")
    public Result<Object> data(String datasourceId) {
        if (StringUtils.isBlank(datasourceId)) {
            return Result.of(null, false, "参数错误");
        }

        List<JSONObject> nodes = new ArrayList<>();
        List<JSONObject> links = new ArrayList<>();
        List<JSONObject> categories = new ArrayList<>();
        // 查询包含外表关系的数据
        List<MetadataDetailsVo> dataList = metadataDetailsService.analysis(datasourceId);
        if(dataList.size() == 0){
            return  Result.of(null, false);
        }
        dataList.forEach(vo -> {
            generateEchartsData(nodes, links, categories, vo);
        });

        JSONObject result = new JSONObject();
        result.put("nodes", nodes);
        result.put("links", links);
        result.put("categories", categories);
        return Result.of(result);
    }

    /**
     * 生成节点
     *
     * @param nodes
     * @param links
     * @param categories
     * @param vo
     */
    private void generateEchartsData(List<JSONObject> nodes, List<JSONObject> links, List<JSONObject> categories, MetadataDetailsVo vo) {
        JSONObject link = new JSONObject();
        JSONObject category = null;
        // 生成源节点
        if (nodes.stream().noneMatch(n -> n.getString("id").equals(vo.getTableName()))) {
            nodes.add(getNode(vo, false));

            category = new JSONObject();
            category.put("name", vo.getTableName());
            categories.add(category);

            link.put("source", vo.getTableName());
        }
        // 目标节点
        if (nodes.stream().noneMatch(n -> n.getString("id").equals(vo.getForeignTable()))) {
            nodes.add(getNode(vo, true));

            category = new JSONObject();
            category.put("name", vo.getForeignTable());
            categories.add(category);

            link.put("target", vo.getForeignTable());
            link.put("value", 500);
        }

        links.add(link);
    }

    private JSONObject getNode(MetadataDetailsVo vo, Boolean target){
        String name = vo.getTableName();
        String label = vo.getForeignColumn() + "-->" + vo.getForeignTable();
        if(target){
            name = vo.getForeignTable();
            label = "";
        }
        JSONObject node = new JSONObject();
        node.put("id", name);
        node.put("name", name);
        node.put("label", "{\"show\": true}");
        node.put("category", name);
        node.put("value", label);
        return node;
    }
}
