package com.keyware.shandan.system.mapper;

import com.keyware.shandan.system.entity.SysOrg;
import com.keyware.shandan.common.mapper.IBaseMapper;
import com.keyware.shandan.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 组织机构表 Mapper 接口
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-11
 */
@Mapper
public interface SysOrgMapper extends IBaseMapper<SysOrg> {

    /**
     * 获取机构树
     * @param id
     * @return
     */
    SysOrg getOrgTree(String id);

    /**
     * 获取子机构
     * @param id
     * @return
     */
    List<SysOrg> getChildren(String id);

    /**
     * 查询机构管理员
     * @param orgId
     * @return
     */
    List<SysUser> selectOrgAdmins(String orgId);
}
