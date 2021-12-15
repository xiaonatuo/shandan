package com.keyware.shandan.bianmu.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.entity.MetadataDetailsVo;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.datasource.entity.DBTableColumnVo;
import com.keyware.shandan.datasource.entity.DataSourceVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 动态sql工具类
 * </p>
 *
 * @author GuoXin
 * @since 2021/6/22
 */
public class MetadataUtils {
    // 主表别名
    private static final String MASTER_TABLE_ALIAS = " M";
    // 右表别名前缀
    private static final String TABLE_ALIAS_PREFIX = " T";

    public static String generateSql(MetadataBasicVo metadata, DataSourceVo dataSource) {
        StringBuilder builder = new StringBuilder();

        String schema = dataSource.getJdbcSchema();
        if(StringUtils.isBlank(schema)){
            schema = "";
        }else{
            schema = "\"" + schema + "\".";
        }
        // 找到主表并设置为左表
        Optional<MetadataDetailsVo> optional = metadata.getMetadataDetailsList().stream().filter(MetadataDetailsVo::getMaster).findFirst();
        List<MetadataDetailsVo> rightTables = metadata.getMetadataDetailsList().stream().filter(t -> !t.getMaster()).collect(Collectors.toList());
        if (optional.isPresent()) {
            MetadataDetailsVo masterTable = optional.get();
            JSONArray masterColumns = getColumns(masterTable);
            if (masterColumns.size() > 0) {
                StringBuilder tableNameBuilder = new StringBuilder();
                StringBuilder columnsBuilder = new StringBuilder();
                StringBuilder wheresBuilder = new StringBuilder();

                List<String> columnTemp = new ArrayList<>();

                // 主表
                String master_table = masterTable.getTableName();
                // 主表列
                masterColumns.forEach(o -> {
                    JSONObject obj = (JSONObject) o;
                    columnsBuilder.append(MASTER_TABLE_ALIAS).append(".\"").append(obj.getString("columnName")).append("\",");
                    columnTemp.add(obj.getString("columnName"));
                });
                // 主表表名
                tableNameBuilder.append(schema).append("\"").append(master_table).append("\" as ").append(MASTER_TABLE_ALIAS);

                // 需要判断所有右表是否被设定为外表，如果没有，则该表不参与sql查询
                List<MetadataDetailsVo> tables = getForeignTables(rightTables, masterTable);
                // where 条件
                appendWhere(tables, masterTable, wheresBuilder, MASTER_TABLE_ALIAS);

                // 遍历右表
                for (int i = 0; i < tables.size(); i++) {
                    MetadataDetailsVo table = tables.get(i);
                    String rightTableAlias = TABLE_ALIAS_PREFIX + i;
                    tableNameBuilder.append(", ").append(schema).append("\"").append(table.getTableName()).append("\" as ").append(rightTableAlias);

                    getColumns(table).forEach(obj -> {
                        JSONObject col = (JSONObject) obj;
                        if(!columnTemp.contains(col.getString("columnName"))){
                            columnsBuilder.append(rightTableAlias).append(".\"").append(col.getString("columnName")).append("\",");
                        }
                    });

                    // where 条件
                    appendWhere(tables, table, wheresBuilder, rightTableAlias);
                }

                builder.append("select ").append(columnsBuilder.substring(0, columnsBuilder.toString().length() - 1))
                        .append(" from ").append(tableNameBuilder)
                        .append(" where 1=1 ").append(wheresBuilder);

            } else {
                throw new IllegalArgumentException("没有找到表的列数据");
            }
        } else {
            throw new IllegalArgumentException("没有在数据表信息中找到主表");
        }

        return builder.toString();
    }

    private static void appendWhere(List<MetadataDetailsVo> tables, MetadataDetailsVo table, StringBuilder wheresBuilder, String tableAlias) {
// 先判断该表是否设置外表
        if (StringUtils.isNotBlank(table.getForeignTable())) {
            // 找到外表
            Optional<MetadataDetailsVo> foreignOptional = tables.stream().filter(t -> t.getTableName().equals(table.getForeignTable())).findFirst();
            if (foreignOptional.isPresent()) {
                MetadataDetailsVo foreignTable = foreignOptional.get();
                String foreignAlias = TABLE_ALIAS_PREFIX + tables.indexOf(foreignTable);
                if (StringUtils.isNotBlank(foreignTable.getTablePrimaryColumn())) {
                    wheresBuilder.append("and").append(tableAlias).append(".\"").append(table.getForeignColumn()).append("\" = ").append(foreignAlias).append(".\"").append(foreignTable.getTablePrimaryColumn()).append("\"");
                }
            }
        }
    }

    /**
     * 获取所有外表
     *
     * @param rightTables 需要判定的所有外表
     * @param masterTable 主表
     * @return 返回所有被其他表设为外表的表
     */
    private static List<MetadataDetailsVo> getForeignTables(List<MetadataDetailsVo> rightTables, MetadataDetailsVo masterTable) {
        List<MetadataDetailsVo> result = new ArrayList<>();
        for (MetadataDetailsVo vo : rightTables) {
            if(StringUtils.isBlank(masterTable.getForeignTable())){
                continue;
            }
            if (masterTable.getForeignTable().equals(vo.getTableName())) {
                // 判定为被主表关联，添加
                result.add(vo);
            } else {
                // 判断是否被其他表关联
                if (rightTables.stream().anyMatch(t -> vo.getTableName().equals(t.getForeignTable()))) {
                    result.add(vo);
                }
            }
        }
        return result;
    }

    /**
     * 获取表的列
     *
     * @param detail
     * @return
     */
    public static JSONArray getColumns(MetadataDetailsVo detail) {
        JSONArray columns = new JSONArray();
        String tableColumns = detail.getTableColumns();
        // 如果字段中没有配置的列，则添加全部列
        if (StringUtils.isBlank(tableColumns)) {
            columns.addAll(detail.getColumnList().stream().map(JSONObject::toJSON).collect(Collectors.toList()));
        } else {
            JSONArray arr = JSONArray.parseArray(tableColumns);
            if (arr != null) {
                arr.forEach(item -> {
                    JSONObject obj = (JSONObject) item;
                    if(detail.getColumnList() == null){
                        columns.add(obj);
                    }else{
                        for (DBTableColumnVo col : detail.getColumnList()) {
                            if (col.getColumnName().equals(obj.getString("columnName"))) {
                                columns.add(JSONObject.toJSON(col));
                                break;
                            }
                        }
                    }
                });
            }
        }

        return columns;
    }

}
