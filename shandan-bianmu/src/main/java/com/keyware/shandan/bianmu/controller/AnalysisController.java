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
import java.util.stream.Collectors;

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


    @GetMapping("/selectAnalysisLayer")
    public ModelAndView selectAnalysisLayer(){
        return new ModelAndView("business/analysis/selectAnalysisLayer");
    }


    @PostMapping("/data")
    public Result<Object> data(String datasourceId) {
        Boolean bool = StringUtils.isBlank(datasourceId);
        if (Boolean.TRUE.equals(bool)) {
            return Result.of(null, false, "参数错误");
        }
        List<JSONObject> nodes = new ArrayList<>();
        List<JSONObject> links = new ArrayList<>();
        List<JSONObject> categories = new ArrayList<>();
        // 查询包含外表关系的数据
        List<MetadataDetailsVo> dataList = metadataDetailsService.analysisMetadata(datasourceId);
        if(dataList.isEmpty()){
            return Result.of(null, false);
        }
        generateEchartsData(nodes, links, categories, dataList);
        JSONObject result = new JSONObject();
        result.put("nodes", nodes);
        result.put("links", links);
        result.put("categories", categories);
        return Result.of(result);
    }


    /**
     * 生成节点
     * @param nodes
     * @param links
     * @param categories
     * @param dataList
     */
    private void generateEchartsData(List<JSONObject> nodes, List<JSONObject> links, List<JSONObject> categories, List<MetadataDetailsVo> dataList) {
        MetadataDetailsVo vo;
        //数据根节点
        List<MetadataDetailsVo> master = dataList.stream().filter(v ->Boolean.TRUE.equals(v.getMaster())).collect(Collectors.toList());
        //子节点
        List<MetadataDetailsVo> child = dataList.stream().filter(v ->Boolean.FALSE.equals(v.getMaster())).collect(Collectors.toList());
        //生成源节点
        if(!master.isEmpty()){
            vo = master.get(0);
            nodes.add(getNode(vo,nodes,links,categories));
            getChildrens(vo,child,nodes,links,categories);
        }
    }


    /**
     * 根据主节点
     * 递归查找获取下面所有子节点
     * @param
     * @return
     */
    private List<MetadataDetailsVo> getChildrens(MetadataDetailsVo root, List<MetadataDetailsVo> all,List<JSONObject> nodes, List<JSONObject> links, List<JSONObject> categories) {
        List<MetadataDetailsVo> children = all.stream().filter(md -> md.getTableName().equals(root.getForeignTable())).collect(Collectors.toList());
        //递归遍历
        for (MetadataDetailsVo s: children) {
            nodes.add(getNode(s,nodes,links,categories));
            getChildrens(s,all,nodes,links,categories);
        }
        return children;
    }


    /**
     *  封装数据格式
     * @param vo
     * @param
     * @return
     */
    private JSONObject getNode(MetadataDetailsVo vo,List<JSONObject> nodes, List<JSONObject> links, List<JSONObject> categories){
        JSONObject node = new JSONObject();
        String name = vo.getTableName();
        String label = (null!=vo.getForeignColumn() && null!=vo.getForeignTable()) ? (vo.getForeignColumn()+ "-->" + vo.getForeignTable()) : "";
        node.put("id", name);
        node.put("name", name);
        node.put("label", "{\"show\": true}");
        node.put("category", nodes.size());
        node.put("value", label);
        links.add(getLink(vo));
        categories.add(getCategory(vo));
        return node;
    }


    /**
     * 维护导航信息
     * @param vo
     * @return
     */
    private JSONObject getCategory(MetadataDetailsVo vo){
        JSONObject category = new JSONObject();
        category.put("name", vo.getTableName());
        return category;
    }


    /**
     * 维护线信息
     * @param vo
     * @return
     */
    private JSONObject getLink(MetadataDetailsVo vo){
        JSONObject link = new JSONObject();
        link.put("source", vo.getTableName());
        link.put("target", vo.getForeignTable());
        link.put("value", 500);
        return link;
    }


}
