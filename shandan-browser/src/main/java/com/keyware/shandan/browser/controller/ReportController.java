package com.keyware.shandan.browser.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.entity.MetadataDetailsVo;
import com.keyware.shandan.bianmu.service.MetadataService;
import com.keyware.shandan.browser.config.BianmuDataCache;
import com.keyware.shandan.browser.entity.ConditionItem;
import com.keyware.shandan.browser.entity.ExportParam;
import com.keyware.shandan.browser.entity.ReportVo;
import com.keyware.shandan.browser.service.SearchService;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.StringUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

/**
 * 统计报表前端控制器
 *
 * @author GuoXin
 * @since 2021/7/9
 */
@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private SearchService searchService;

    /**
     * 查询元数据表的字段
     *
     * @param id 元数据ID
     * @return
     */
    @GetMapping("/metadata/columns/{id}")
    public Result<Object> getColumn(@PathVariable String id) {
        MetadataBasicVo vo = metadataService.getById(id);
        if (vo != null) {
            Stream<MetadataDetailsVo> stream = vo.getMetadataDetailsList().stream();
            Optional<MetadataDetailsVo> optional = stream.filter(MetadataDetailsVo::getMaster).findFirst();
            if (optional.isPresent()) {
                MetadataDetailsVo details = optional.get();
                return Result.of(JSONObject.toJSON(details.getTableColumns()));
            }
        }
        return Result.of(null);
    }


    @PostMapping("/data")
    public Result<Object> getData(ReportVo report) {

        try {
            return Result.of(searchService.report(report));
        } catch (IOException e) {
            e.printStackTrace();
            return Result.of(null, false);
        }
    }

    /**
     * 导出到word
     *
     * @return
     */
    @PostMapping("/export/word")
    public String exportWord(ExportParam exportParam, HttpServletResponse response) {
        byte[] fileNameBytes = exportParam.getTitle().getBytes(StandardCharsets.UTF_8);
        String fileName = new String(fileNameBytes, 0, fileNameBytes.length, StandardCharsets.ISO_8859_1);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".doc");
        try {
            Writer out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8));
            createDoc(convertData(exportParam), out);

        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
        return "";
    }

    private Map<String, Object> convertData(ExportParam param) {
        Map<String, Object> result = new HashMap<>();
        // 文档标题
        result.put("title", param.getTitle());

        List<String> conditions = new ArrayList<>();
        Map<String, String> fields = filedMap(param);
        param.getConditions().forEach(item -> {
            if (StringUtils.isNotBlank(item.getValue())) {
                String filedComment = fields.get(item.getField());
                String logic = item.getLogic().getText();
                conditions.add(filedComment + " " + logic.replace("?", item.getValue()));
            }
        });
        result.put("conditions", conditions);

        List<Map<String, String>> reports = new ArrayList<>();
        param.getEcharts().forEach(item ->{
            Map<String, Object> map = new HashMap<>();
            map.put("title", "统计图表十三四");

            List<String> datas = new ArrayList<>();
            datas.add("这是统计结果");
            map.put("datas", datas);
            result.put("image", item.getImage().replace("data:image/png;base64,",""));
        });
        result.put("reports", reports);
        return result;
    }

    private Map<String, String> filedMap(ExportParam param) {
        Map<String, String> fields = new HashMap<>();
        boolean hasMetadata = false;
        for (ConditionItem item : param.getConditions()) {
            if (item.getField().equals("metadataId")) {
                if (StringUtils.isNotBlank(item.getValue())) {
                    MetadataDetailsVo vo = BianmuDataCache.get(item.getValue());
                    if (vo != null) {
                        JSONArray columns = JSONArray.parseArray(vo.getTableColumns());
                        for (Object json : columns) {
                            JSONObject o = (JSONObject) JSONObject.toJSON(json);
                            fields.put(o.getString("columnName"), o.getString("comment"));
                        }
                        hasMetadata = true;
                    }
                }
                break;
            }
        }
        if(!hasMetadata){
            param.getFields().forEach(item ->{
                fields.put(item.get("field").toString(), item.get("comment").toString());
            });
        }

        return fields;
    }

    /**
     * 创建DOC文档
     *
     * @param dataMap
     * @param out
     * @throws IOException
     * @throws TemplateException
     */
    private void createDoc(Map<String, Object> dataMap, Writer out) throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        configuration.setDefaultEncoding("utf-8");
        configuration.setClassForTemplateLoading(this.getClass(), "/templates");
        Template template = configuration.getTemplate("reportExport.ftl");
        template.process(dataMap, out);
        out.flush();
        out.close();
    }
}
