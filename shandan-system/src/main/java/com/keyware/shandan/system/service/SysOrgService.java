package com.keyware.shandan.system.service;

import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.system.entity.SysOrg;
import com.keyware.shandan.common.service.IBaseService;
import com.keyware.shandan.system.entity.SysUser;

import java.util.List;

/**
 * <p>
 * 部门表 服务类
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-11
 */
public interface SysOrgService extends IBaseService<SysOrg, String> {

    /**
     * 获取部门树
     * @param parentId
     * @return
     */
    Result<List<SysOrg>> getOrgTree(String parentId);

    /**
     * 获取部门的所有子部门，不包含当前部门
     * @param orgId
     * @return
     */
    List<SysOrg> getOrgAllChildren(String orgId);

    /**
     * 查询部门管理员
     * @param orgId
     * @return
     */
    List<SysUser> getOrgAdmins(String orgId);
}
