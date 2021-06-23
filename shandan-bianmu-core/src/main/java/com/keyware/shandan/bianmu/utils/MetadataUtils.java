package com.keyware.shandan.bianmu.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.entity.MetadataDetailsVo;
import com.keyware.shandan.common.util.StringUtils;

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

    public static String generateSql(MetadataBasicVo metadata){
        StringBuilder builder = new StringBuilder();

        // 找到主表并设置为左表
        Optional<MetadataDetailsVo> optional = metadata.getMetadataDetailsList().stream().filter(MetadataDetailsVo::getMaster).findFirst();
        List<MetadataDetailsVo> rightTables = metadata.getMetadataDetailsList().stream().filter(t-> !t.getMaster()).collect(Collectors.toList());
        if (optional.isPresent()) {
            MetadataDetailsVo masterTable = optional.get();
            JSONArray masterColumns = getColumns(masterTable);
            if(masterColumns.size() > 0){
                StringBuilder tableNameBuilder = new StringBuilder();
                StringBuilder columnsBuilder = new StringBuilder();
                StringBuilder wheresBuilder = new StringBuilder();

                // 主表
                String master_table = masterTable.getTableName();
                // 主表列
                masterColumns.forEach(o->{
                    JSONObject obj = (JSONObject) o;
                    columnsBuilder.append(MASTER_TABLE_ALIAS).append(".").append(obj.getString("columnName")).append(",");
                });
                // 主表表名
                tableNameBuilder.append("\"").append(master_table).append("\" as ").append(MASTER_TABLE_ALIAS);

                // 需要判断所有右表是否被设定为外表，如果没有，则该表不参与sql查询
                List<MetadataDetailsVo> tables = getForeignTables(rightTables, masterTable);
                // 遍历右表
                for(int i=0; i < tables.size(); i++){
                    MetadataDetailsVo table = tables.get(i);
                    String rightTableAlias = TABLE_ALIAS_PREFIX + i;
                    tableNameBuilder.append(", \"").append(table.getTableName()).append("\" as ").append(rightTableAlias);

                    getColumns(table).forEach(obj -> {
                        JSONObject col = (JSONObject)obj;
                        columnsBuilder.append(rightTableAlias).append(".").append(col.getString("columnName")).append(",");
                    });

                    // where 条件
                    // 先判断该表是否设置外表
                    if(StringUtils.isNotBlank(table.getForeignTable())){
                        // 找到外表
                        Optional<MetadataDetailsVo> foreignOptional = tables.stream().filter(t -> t.getTableName().equals(table.getForeignTable())).findFirst();
                        if(foreignOptional.isPresent()){
                            MetadataDetailsVo foreignTable = foreignOptional.get();
                            String foreignAlias = TABLE_ALIAS_PREFIX + tables.indexOf(foreignTable);
                            if(StringUtils.isNotBlank(foreignTable.getTablePrimaryColumn())){
                                wheresBuilder.append("and").append(rightTableAlias).append(".").append(table.getForeignColumn()).append(" = ").append(foreignAlias).append(".").append(foreignTable.getTablePrimaryColumn());
                            }
                        }
                    }
                }

                builder.append("select ").append(columnsBuilder.substring(0, columnsBuilder.toString().length()-1))
                        .append(" from ").append(tableNameBuilder)
                        .append(" where 1=1 ").append(wheresBuilder);

            }else{
                throw new IllegalArgumentException("没有找到表的列数据");
            }
        }else{
            throw new IllegalArgumentException("没有在元数据中找到主表");
        }

        return builder.toString();
    }

    /**
     * 该方法判断右表是否被设定外表
     * @param rightTables 需要判定的所有外表
     * @param masterTable 主表
     * @return 返回所有被其他表设为外表的表
     */
    private static List<MetadataDetailsVo> getForeignTables(List<MetadataDetailsVo> rightTables, MetadataDetailsVo masterTable){
        List<MetadataDetailsVo> result = new ArrayList<>();
        for(MetadataDetailsVo vo : rightTables){
            if(masterTable.getForeignTable().equals(vo.getTableName())){
                // 判定为被主表关联，添加
                result.add(vo);
            }else{
                // 判断是否被其他表关联
                if(rightTables.stream().anyMatch(t ->vo.getTableName().equals(t.getForeignTable()))){
                    result.add(vo);
                }
            }
        }
        return result;
    }

    /**
     * 获取表的列
     * @param detail
     * @return
     */
    public static JSONArray getColumns(MetadataDetailsVo detail){
        JSONArray columns = new JSONArray();
        String tableColumns = detail.getTableColumns();
        // 如果字段中没有配置的列，则添加全部列
        if (StringUtils.isBlank(tableColumns)) {
            columns.addAll(detail.getColumnList().stream().map(column -> (JSONObject) JSONObject.toJSON(column)).collect(Collectors.toList()));
        } else {
            columns.addAll(JSON.parseArray(tableColumns));
        }

        return columns;
    }

}
