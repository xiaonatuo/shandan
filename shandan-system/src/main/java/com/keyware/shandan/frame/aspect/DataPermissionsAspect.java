package com.keyware.shandan.frame.aspect;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.common.enums.DataPermisScope;
import com.keyware.shandan.frame.annotation.DataPermissions;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.system.entity.SysOrg;
import com.keyware.shandan.system.entity.SysPermissions;
import com.keyware.shandan.system.entity.SysRole;
import com.keyware.shandan.system.entity.SysUser;
import com.keyware.shandan.system.service.SysOrgService;
import com.keyware.shandan.system.service.SysPermissionsService;
import com.keyware.shandan.system.service.SysRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 数据权限处理
 */
@Aspect
@Component
public class DataPermissionsAspect {

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysOrgService sysOrgService;

    @Autowired
    private SysPermissionsService permissionsService;


    @Around(value = "@annotation(com.keyware.shandan.frame.annotation.DataPermissions)")
    public Object permissionsProtected(ProceedingJoinPoint joinPoint) throws Throwable {
        SysUser currentUser = SecurityUtil.getLoginSysUser();
        // 当前用户权限范围
        List<SysPermissions> permisList = getCurrentUserPermissions(currentUser.getUserId());
        DataPermissions annotation = getAnnotationByMethod(joinPoint, DataPermissions.class);
        // 不包含最高权限范围时，通过查询权限范围内的部门作为条件进行查询
        if (permisList.stream().noneMatch(permis -> permis.getPermisScope() == DataPermisScope.ALL_SCOPE)) {
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof QueryWrapper) {
                    QueryWrapper<Object> wrapper = (QueryWrapper<Object>) args[i];

                    Set<String> orgIds = new HashSet<>();
                    SysOrg currentOrg = sysOrgService.getById(currentUser.getOrgId());
                    if (permisList.stream().anyMatch(permis -> permis.getPermisScope() == DataPermisScope.CURRENT_ORG_CHILDRENS)) {
                        // 添加当前部门和子部门
                        orgIds.add(currentOrg.getId());
                        sysOrgService.getOrgAllChildren(currentOrg.getId()).forEach(org -> orgIds.add(org.getId()));

                    } else if (permisList.stream().anyMatch(permis -> permis.getPermisScope() == DataPermisScope.ONLY_CURRENT_ORG)) {
                        // 只添加当前部门
                        orgIds.add(currentOrg.getId());
                    }

                    // 自定义权限
                    if (permisList.stream().anyMatch(permis -> permis.getPermisScope() == DataPermisScope.SPECIAL)) {
                        permisList.stream().filter(permis -> permis.getPermisScope() == DataPermisScope.SPECIAL).forEach(permis -> {
                            List<String> ids = permissionsService.getOrgConfigs(permis.getPermisId());
                            orgIds.addAll(ids);
                        });
                    }

                    if (orgIds.size() == 0) {
                        wrapper.eq(true, "CREATE_USER", currentUser.getUserId());
                    } else {
                        wrapper.eq(true, "CREATE_USER", currentUser.getUserId())
                                .or(true, wp -> {
                                    wp.in(true, annotation.orgColumn(), orgIds);

                                    // 查询实体为目录或者数据资源时，则添加审核通wrapper.getEntity()过条件
                                    Object entity = wrapper.getEntity();
                                    if (entity != null) {
                                        String className = entity.getClass().getName();
                                        if (className.equals("com.keyware.shandan.bianmu.entity.MetadataBasicVo")
                                                || className.equals("com.keyware.shandan.bianmu.entity.DirectoryVo")) {
                                            wp.and(true, wpe -> wpe.eq("REVIEW_STATUS", "PASS"));
                                        }
                                    }
                                });
                    }

                    args[i] = wrapper;
                    return joinPoint.proceed(args);
                }
            }
        }
        return joinPoint.proceed();
    }

    /**
     * 获取当前用户的权限集合
     *
     * @return
     */
    private List<SysPermissions> getCurrentUserPermissions(String userId) {
        List<SysRole> roleList = sysRoleService.getUserRoles(userId);
        List<SysPermissions> permisList = new ArrayList<>();
        roleList.forEach(role -> permisList.addAll(role.getPermissionsList()));
        return permisList;
    }

    /**
     * 获得注解
     *
     * @param joinPoint       切面连接点
     * @param annotationClass
     * @return
     */
    private <T extends Annotation> T getAnnotationByMethod(JoinPoint joinPoint, Class<T> annotationClass) throws NoSuchMethodException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 这里获取目标对象的实际方法，而非代理对象的方法
        Method method = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(), signature.getParameterTypes());
        return method.getAnnotation(annotationClass);
    }

    /**
     * 判断目标类是否包含指定的注解
     *
     * @param joinPoint
     * @param annotationClass
     * @return
     */
    private <T extends Annotation> Boolean hasAnnotationByClass(JoinPoint joinPoint, Class<T> annotationClass) {

        return joinPoint.getTarget().getClass().getAnnotation(annotationClass) != null;
    }
}
