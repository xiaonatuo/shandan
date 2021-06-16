package com.keyware.shandan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.frame.annotation.DataPermissions;
import com.keyware.shandan.system.entity.SysOrg;
import com.keyware.shandan.system.entity.SysUser;
import com.keyware.shandan.system.mapper.SysOrgMapper;
import com.keyware.shandan.system.service.SysOrgService;
import com.keyware.shandan.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 组织机构表 服务实现类
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-11
 */
@Service
public class SysOrgServiceImpl extends BaseServiceImpl<SysOrgMapper, SysOrg, String> implements SysOrgService {

    @Autowired
    private SysOrgMapper sysOrgMapper;

    @Autowired
    private SysUserService sysUserService;

    @Override
    @DataPermissions(orgColumn = "ID")
    public Page<SysOrg> page(Page<SysOrg> page, QueryWrapper<SysOrg> wrapper) {
        String parentId = wrapper.getEntity().getOrgParentId();
        wrapper.getEntity().setOrgParentId(null);

        if (StringUtils.isNotBlank(parentId)) {
            wrapper.eq("ORG_PARENT_ID", parentId).or().eq("ID", parentId);
        }
        return super.page(page, wrapper);
    }

    /**
     * 获取机构树
     *
     * @param parentId
     * @return
     */
    @Override
    public Result<List<SysOrg>> getOrgTree(String parentId) {

        SysOrg org = sysOrgMapper.getOrgTree(parentId);
        return Result.of(Collections.singletonList(org));
    }

    @Override
    @DataPermissions(orgColumn = "ID")
    public List<SysOrg> list(QueryWrapper<SysOrg> wrapper) {
        return super.list(wrapper);
    }

    /**
     * 获取机构的所有子机构,不包含当前机构
     *
     * @param orgId
     * @return
     */
    @Override
    public List<SysOrg> getOrgAllChildren(String orgId) {
        QueryWrapper<SysOrg> query = new QueryWrapper<>();
        query.ne("ID", orgId).like("ORG_PATH", "%" + orgId + "%");
        return list(query);
    }

    /**
     * 查询机构管理员
     *
     * @param orgId
     * @return
     */
    @Override
    public List<SysUser> getOrgAdmins(String orgId) {
        return sysOrgMapper.selectOrgAdmins(orgId);
    }

    /**
     * 重写组织机构的保存方法
     *
     * @param entity 组织机构
     * @return
     */
    @Override
    public Result<SysOrg> updateOrSave(SysOrg entity) {
        SysOrg parentOrg = getById(entity.getOrgParentId());
        if (parentOrg == null) {
            return Result.of(null, false, "未查询到父机构");
        }
        // 如果ID为空则表示新增需要验证orgNumber
        if (StringUtils.isBlank(entity.getId())) {
            SysOrg param = new SysOrg();
            param.setOrgNumber(entity.getOrgNumber());
            List<SysOrg> list = list(new QueryWrapper<>(param));
            if (list != null && list.size() > 0) {
                return Result.of(null, false, "组织机构编号已存在");
            }
            // 使用orgNumber作为主键
            entity.setId(entity.getOrgNumber());
        }
        // 设置父机构名称
        entity.setOrgParentName(parentOrg.getOrgName());
        // 设置机构路径
        entity.setOrgPath(parentOrg.getOrgPath().concat("|").concat(entity.getOrgNumber()));
        // 设置机构负责人
        if (StringUtils.isNotBlank(entity.getLeader())) {
            SysUser leader = sysUserService.getById(entity.getLeader());
            if (leader == null) {
                return Result.of(null, false, "机构负责人不存在");
            }
            entity.setLeaderName(leader.getUserName());
        }

        return super.updateOrSave(entity);
    }

    /**
     * 递归删除子机构
     *
     * @param id
     * @return
     */
    @Override
    public Result<Boolean> deleteById(String id) {
        List<SysOrg> menuList = listAllChildByParentId(id);
        List<String> idList = new ArrayList<>();
        idList.add(id);
        idList.addAll(menuList.stream().map(SysOrg::getId).collect(Collectors.toList()));
        boolean ok = super.removeByIds(idList);

        return Result.of(ok, ok);
    }

    /**
     * 根据父菜单Id递归查询子菜单列表
     *
     * @param parentId
     * @return
     */
    public List<SysOrg> listAllChildByParentId(String parentId) {
        List<SysOrg> allChild = new ArrayList<>();

        SysOrg childCondition = new SysOrg();
        childCondition.setOrgParentId(parentId);
        List<SysOrg> child = super.list(new QueryWrapper<>(childCondition));
        if (child != null && child.size() != 0) {
            allChild.addAll(child);
            child.forEach(menu -> {
                allChild.addAll(listAllChildByParentId(menu.getId()));
            });
        }

        return allChild;
    }
}
