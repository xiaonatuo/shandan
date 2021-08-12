package com.keyware.shandan.system.mapper;

import com.keyware.shandan.system.entity.SysPermissions;
import com.keyware.shandan.common.mapper.IBaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 系统权限表 Mapper 接口
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-19
 */
@Mapper
public interface SysPermissionsMapper extends IBaseMapper<SysPermissions> {

    /**
     * 保存机构权限配置
     *
     * @param permisId 权限ID
     * @param orgId    机构ID
     * @param userId   用户ID
     * @return 影响行数
     */
    @Insert("insert into SYS_PERMIS_ORG(PERMIS_ID, ORG_ID, CREATE_TIME, CREATE_USER) values(#{permisId}, #{orgId}, now(), #{userId})")
    void saveOrgConfig(String permisId, String orgId, String userId);

    /**
     * 保存目录权限配置
     *
     * @param permisId 权限ID
     * @param dirId    目录ID
     * @param userId   用户ID
     */
    @Insert("insert into SYS_PERMIS_DIR(PERMIS_ID, DIR_ID, CREATE_TIME, CREATE_USER) values(#{permisId}, #{dirId}, now(), #{userId})")
    void saveDirConfig(String permisId, String dirId, String userId);

    /**
     * 删除机构权限配置
     *
     * @param permisId 权限ID
     * @param orgId    机构ID
     * @return 影响行数
     */
    @Delete("delete from SYS_PERMIS_ORG where PERMIS_ID = #{permisId}")
    void removeOrgConfig(String permisId);

    /**
     * 删除目录权限配置
     *
     * @param permisId 权限ID
     * @param dirId    目录ID
     * @return 影响行数
     */
    @Delete("delete from SYS_PERMIS_DIR where PERMIS_ID = #{permisId}")
    void removeDirConfig(String permisId);

    /**
     * 查询机构权限配置
     *
     * @param permisId 权限ID
     * @return 机构ID集合
     */
    @Select("select ORG_ID from SYS_PERMIS_ORG where PERMIS_ID = #{permisId}")
    List<String> selectOrgConfig(String permisId);

    /**
     * 查询目录权限配置
     *
     * @param permisId 权限ID
     * @return 目录ID集合
     */
    @Select("select DIR_ID from SYS_PERMIS_DIR where PERMIS_ID = #{permisId}")
    List<String> selectDirConfig(String permisId);

    /**
     * @param permisId 权限ID
     * @param orgId    机构ID
     * @return -
     */
    @Select("select count(1) from SYS_PERMIS_ORG where PERMIS_ID = #{permisId} and ORG_ID = #{orgId} ")
    int orgConfigExists(String permisId, String orgId);

    /**
     * @param permisId 权限ID
     * @param dirId    目录ID
     * @return
     */
    @Select("select count(1) from SYS_PERMIS_DIR where PERMIS_ID = #{permisId} and DIR_ID = #{dirId} ")
    int dirConfigExists(String permisId, String dirId);
}
