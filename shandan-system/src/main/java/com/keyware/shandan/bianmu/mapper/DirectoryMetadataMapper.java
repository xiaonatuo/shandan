package com.keyware.shandan.bianmu.mapper;

import com.keyware.shandan.bianmu.entity.DirectoryMetadataVo;
import com.keyware.shandan.bianmu.entity.MetadataAndFileVo;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.common.mapper.IBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 目录与数据资源关系表 Mapper 接口
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-03
 */
@Mapper
public interface DirectoryMetadataMapper extends IBaseMapper<DirectoryMetadataVo> {

    /**
     * 查询指定目录及其所有子目录下的数据资源以及文件
     * @param id 目录ID
     * @return 数据资源集合
     */
    @Select("<script> select * from V_METADATA_UNION_FILE\n" +
            "where DIRECTORY_ID in (\n" +
            "    select distinct ID\n" +
            "    from B_DIRECTORY\n" +
            "    where B_DIRECTORY.REVIEW_STATUS = 'PASS' " +
            "<if test=\"id != null and id != '' \">" +
            "   and DIRECTORY_PATH like ((select DIRECTORY_PATH from B_DIRECTORY where ID = #{id}) || '%')\n" +
            "</if> ) </script>")
    @ResultType(value = com.keyware.shandan.bianmu.entity.MetadataAndFileVo.class)
    List<MetadataAndFileVo> selectMetadataAndFileListByDir(String id);
}
