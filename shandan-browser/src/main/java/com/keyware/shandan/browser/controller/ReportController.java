package com.keyware.shandan.browser.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.bianmu.entity.DirectoryVo;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.entity.MetadataDetailsVo;
import com.keyware.shandan.bianmu.service.DirectoryService;
import com.keyware.shandan.bianmu.service.MetadataService;
import com.keyware.shandan.browser.config.BianmuDataCache;
import com.keyware.shandan.browser.entity.SearchConditionVo;
import com.keyware.shandan.browser.entity.ExportParam;
import com.keyware.shandan.browser.entity.ReportVo;
import com.keyware.shandan.browser.service.ReportService;
import com.keyware.shandan.browser.service.SearchService;
import com.keyware.shandan.browser.service.impl.ReportDateServiceImpl;
import com.keyware.shandan.browser.service.impl.ReportNumberServiceImpl;
import com.keyware.shandan.browser.service.impl.ReportStringServiceImpl;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.StringUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
    private DirectoryService directoryService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private ReportNumberServiceImpl reportNumberService;

    @Autowired
    private ReportDateServiceImpl reportDateService;

    @Autowired
    private ReportStringServiceImpl reportStringService;

    private static ExportParam tempExportParam;

    /**
     * 查询数据资源表的字段
     *
     * @param id 数据资源ID
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

    @PostMapping("/data/metadata/conditions")
    public Result<Object> getDataByMetadata(ReportVo report) {
        try {
            if (ReportNumberServiceImpl.NUMBER_TYPES.contains(report.getFieldXType())) {
                return Result.of(reportNumberService.statisticsQuery(report));
            } else if (ReportNumberServiceImpl.DATE_TYPES.contains(report.getFieldXType())) {
                return Result.of(reportDateService.statisticsQuery(report));
            } else {
                return Result.of(reportStringService.statisticsQuery(report));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.of(null, false, e.getMessage());
        }
    }

    /**
     * 导出到word
     *
     * @return
     */
    @PostMapping("/export/word")
    public Result<Object> exportWord(ExportParam exportParam) {
        tempExportParam = exportParam;
        return Result.of(null);
    }

    @GetMapping("/download/word")
    public void downloadWord(HttpServletResponse response) {
        if (tempExportParam == null) {
            return;
        }
        File tempDoc = null;
        BufferedInputStream bis = null;
        try {
            tempDoc = createDoc(convertData(tempExportParam));
            bis = new BufferedInputStream(new FileInputStream(tempDoc));

            byte[] fileNameBytes = tempExportParam.getTitle().getBytes(StandardCharsets.UTF_8);
            String fileName = new String(fileNameBytes, 0, fileNameBytes.length, StandardCharsets.ISO_8859_1);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".doc");

            OutputStream out = response.getOutputStream();
            byte[] buffer = new byte[1024];  // 缓冲区
            int bytesToRead = 0;
            // 通过循环将读入的Word文件的内容输出到浏览器中
            while ((bytesToRead = bis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesToRead);
                out.flush();
            }
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (tempDoc != null) tempDoc.delete(); // 删除临时文件
            tempExportParam = null;
        }
    }

    /**
     * 转换模板数据
     *
     * @param param
     * @return
     */
    private Map<String, Object> convertData(ExportParam param) {
        Map<String, Object> result = new HashMap<>();
        // 文档标题
        result.put("topTitle", param.getTitle());

        List<String> conditions = new ArrayList<>();
        Map<String, String> fields = filedMap(param);
        param.getConditions().forEach(item -> {
            if (StringUtils.isNotBlank(item.getFieldValue())) {
                if (item.getFieldName().equals("searchInput")) {
                    conditions.add("关键词包含：" + item.getFieldValue());
                } else if (item.getFieldName().equals("directoryId")) {
                    DirectoryVo dir = directoryService.getById(item.getFieldValue());
                    if (dir != null) {
                        conditions.add("数据目录：" + dir.getDirectoryPath());
                    }
                } else if (item.getFieldName().equals("metadataId")) {
                    MetadataBasicVo metadata = metadataService.getById(item.getFieldValue());
                    if (metadata != null) {
                        String tableName = StringUtils.isNotBlank(metadata.getMetadataComment()) ? metadata.getMetadataComment() : metadata.getMetadataName();
                        conditions.add("数据资源：" + tableName);
                    }
                } else if (item.getFieldName().equals("inputDate")) {
                    conditions.add("任务时间：" + item.getFieldValue());
                } else {
                    String filedComment = fields.get(item.getFieldName());
                    String logic = item.getLogic().getText();
                    conditions.add(filedComment + "：" + logic.replace("?", item.getFieldValue()));
                }
            }
        });

        if (conditions.size() == 0) {
            conditions.add("统计结果基于全部数据");
        }
        result.put("conditions", conditions);

        // 遍历echarts报表
        List<Map<String, Object>> reports = new ArrayList<>();
        param.getEcharts().forEach(echart -> {
            Map<String, Object> map = new HashMap<>();
            // echarts图表的标题
            map.put("title", echart.getTitle() == null ? "" : echart.getTitle());
            // echarts导出的图片
            map.put("imageData", echart.getImage().replace("data:image/png;base64,", ""));
            // echarts图表表头统计维度字段
            String xFiledStr = fields.getOrDefault(echart.getFieldX(), echart.getFieldX());
            map.put("data_header_field", xFiledStr);
            //echarts图表表头统计指标描述
            if (StringUtils.isNotBlank(echart.getRemark())) {
                map.put("data_header_remark", echart.getRemark());
            } else {
                String yFileStr = fields.getOrDefault(echart.getFiledY(), echart.getFiledY());
                String type = echart.getAggregationType();
                if ("count".equals(type)) {
                    yFileStr += "总数";
                } else if ("sum".equals(type)) {
                    yFileStr += "总和";
                } else if ("avg".equals(type)) {
                    yFileStr += "平均值";
                }
                map.put("data_header_remark", yFileStr);
            }
            List<Map<String, String>> datas = new ArrayList<>();
            echart.getData().forEach(data -> {
                Map<String, String> row = new HashMap<>();
                row.put("field_text", (String) data.get("name"));
                row.put("field_data", String.valueOf(data.get("value")));
                datas.add(row);
            });
            map.put("data_body", datas);
            reports.add(map);
        });
        result.put("echarts", reports);
        return result;
    }

    /**
     * 字段名称及注释map
     *
     * @param param
     * @return
     */
    private Map<String, String> filedMap(ExportParam param) {
        Map<String, String> fields = new HashMap<>();
        boolean hasMetadata = false;
        for (SearchConditionVo.Item item : param.getConditions()) {
            if (item.getFieldName().equals("metadataId")) {
                if (StringUtils.isNotBlank(item.getFieldValue())) {
                    MetadataDetailsVo vo = BianmuDataCache.get(item.getFieldValue());
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
        if (!hasMetadata) {
            param.getFields().forEach(item -> {
                fields.put(item.get("columnName").toString(), item.get("comment").toString());
            });
        }

        return fields;
    }

    /**
     * 创建DOC文档
     *
     * @param dataMap
     * @throws IOException
     * @throws TemplateException
     */
    private File createDoc(Map<String, Object> dataMap) throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setClassForTemplateLoading(this.getClass(), "/templates");
        File temp = new File("temp.doc");
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp), StandardCharsets.UTF_8));
        Template template = configuration.getTemplate("report-Word-Microsoft.ftl");
        template.process(dataMap, out);
        out.flush();
        out.close();
        return temp;
    }
}
