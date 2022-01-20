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

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 部门表 服务实现类
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
     * 获取部门树
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
     * 获取部门的所有子部门,不包含当前部门
     *
     * @param orgId
     * @return
     */
    @Override
    public List<SysOrg> getOrgAllChildren(String orgId) {
        QueryWrapper<SysOrg> query = new QueryWrapper<>();
        query.ne("ID", orgId).like("ORG_PATH", orgId);
        return list(query);
    }

    /**
     * 查询部门管理员
     *
     * @param orgId
     * @return
     */
    @Override
    public List<SysUser> getOrgAdmins(String orgId) {
        List<SysUser> leaders = sysOrgMapper.selectOrgAdmins(orgId);
        if(leaders.size() == 0){ // 如果当前结构没有管理员，则查找上级机构管理员
            SysOrg org = getById(orgId);
            if(org != null){
                leaders = getOrgAdmins(org.getOrgParentId());
            }
        }
        return leaders;
    }

    @Override
    @DataPermissions(orgColumn = "ID")
    public List<SysOrg> tree(String parentId) {
        QueryWrapper<SysOrg> wrapper = new QueryWrapper<>();
        /*if(StringUtils.isNotBlank(parentId)) {
            wrapper.like("ORG_PATH", "%" + parentId + "%");
        }*/
        List<SysOrg> list = list(wrapper);
        return generateTree(list);
    }

    /**
     * 重写部门的保存方法
     *
     * @param entity 部门
     * @return
     */
    @Override
    public Result<SysOrg> updateOrSave(SysOrg entity) throws Exception {
        SysOrg parentOrg = getById(entity.getOrgParentId());
        if (parentOrg == null) {
            return Result.of(null, false, "未查询到上级部门");
        }
        // 如果ID为空则表示新增需要验证orgNumber
        if (StringUtils.isBlank(entity.getId())) {
            SysOrg param = new SysOrg();
            param.setOrgNumber(entity.getOrgNumber());
            List<SysOrg> list = list(new QueryWrapper<>(param));
            if (list != null && list.size() > 0) {
                return Result.of(null, false, "部门编号已存在");
            }
            // 使用orgNumber作为主键
            entity.setId(entity.getOrgNumber());
        }
        // 设置父部门名称
        entity.setOrgParentName(parentOrg.getOrgName());
        // 设置部门路径
        entity.setOrgPath(parentOrg.getOrgPath().concat("|").concat(entity.getOrgNumber()));
        // 设置部门负责人
        if (StringUtils.isNotBlank(entity.getLeader())) {
            SysUser leader = sysUserService.getById(entity.getLeader());
            if (leader == null) {
                return Result.of(null, false, "部门负责人不存在");
            }
            entity.setLeaderName(leader.getUserName());
        }

        return super.updateOrSave(entity);
    }

    /**
     * 递归删除子部门
     *
     * @param id
     * @return
     */
    @Override
    public Result<Boolean> deleteById(String id) {
        List<String> idList = new ArrayList<>();
        List<SysOrg> orgList = listAllChildByParentId(id);
        idList.add(id);
        idList.addAll(orgList.stream().map(SysOrg::getId).collect(Collectors.toList()));

        // 查找该机构及子机构下是否包含用户
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.in("ORG_ID", idList);
        List<SysUser> users = sysUserService.list(wrapper);
        if (users.size() > 0) {
            return Result.of(false, false, "该部门下有用户数据，不能删除");
        }
        sysOrgMapper.deleteBatchIds(idList);
        return Result.of(true, true);
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

    /**
     * 构建部门树
     * @param list
     * @return
     */
    private List<SysOrg> generateTree(List<SysOrg> list){
        Set<SysOrg> allOrgSet = new HashSet<>(list);

        list.stream()
                // 找到当前部门列表的根部门
                .filter(org -> list.stream().noneMatch(o -> o.getId().equals(org.getOrgParentId())))
                // 根据当前根部门查找所有父部门
                .forEach(org -> allOrgSet.addAll(listByIds(Arrays.asList(org.getOrgPath().split("\\|")))));

                return allOrgSet.stream()
                // 找到根部门
                .filter(org -> allOrgSet.stream().noneMatch(o -> o.getId().equals(org.getOrgParentId())))
                // 递归子部门
                .peek(org-> findChildren(org, new ArrayList<>(allOrgSet)))
                // 转换List
                .collect(Collectors.toList());
    }

    /**
     * 递归子部门
     * @param org
     * @param list
     */
    private void findChildren(SysOrg org, List<SysOrg> list){
        org.setChildren(list.stream().filter(o -> org.getId().equals(o.getOrgParentId())).collect(Collectors.toList()));
        if(!org.getChildren().isEmpty()){
            org.getChildren().stream().peek(o -> findChildren(o, list)).collect(Collectors.toList());
        }
    }
}
