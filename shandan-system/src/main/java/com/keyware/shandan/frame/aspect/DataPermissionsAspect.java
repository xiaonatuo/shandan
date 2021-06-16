package com.keyware.shandan.frame.aspect;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.common.enums.DataPermisScope;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.frame.annotation.DataPermissions;
import com.keyware.shandan.system.entity.SysOrg;
import com.keyware.shandan.system.entity.SysPermissions;
import com.keyware.shandan.system.entity.SysRole;
import com.keyware.shandan.system.entity.SysUser;
import com.keyware.shandan.system.service.SysOrgService;
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
import java.util.HashMap;
import java.util.List;

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


    @Around(value = "@annotation(com.keyware.shandan.frame.annotation.DataPermissions)")
    public Object permissionsProtected(ProceedingJoinPoint joinPoint) throws Throwable {
        SysUser currentUser = SecurityUtil.getLoginSysUser();
        // 当前用户权限范围
        HashMap<String, SysPermissions> permisMap = getCurrentUserPermissions(currentUser.getUserId());
        DataPermissions annotation = getAnnotationByMethod(joinPoint, DataPermissions.class);
        // 不包含最高权限范围时，通过查询权限范围内的机构作为条件进行查询
        if (permisMap.get(DataPermisScope.ALL_SCOPE.name()) == null) {
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof QueryWrapper) {
                    QueryWrapper wrapper = (QueryWrapper) args[i];
                    List<String> orgIds = new ArrayList<>();
                    SysOrg currentOrg = sysOrgService.getById(currentUser.getOrgId());
                    if (permisMap.get(DataPermisScope.CURRENT_ORG_CHILDRENS.name()) != null) {
                        // 添加当前机构和子机构
                        orgIds.add(currentOrg.getId());
                        sysOrgService.getOrgAllChildren(currentOrg.getId()).forEach(org -> orgIds.add(org.getId()));
                    } else if (permisMap.get(DataPermisScope.ONLY_CURRENT_ORG.name()) != null) {
                        // 只添加当前机构
                        orgIds.add(currentOrg.getId());
                    } else {
                        // 只查询自己创建的数据
                        wrapper.eq("CREATE_USER", currentUser.getUserId());
                        args[i] = wrapper;
                        return joinPoint.proceed(args);
                    }

                    wrapper.in(annotation.orgColumn(), orgIds);
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
    private HashMap<String, SysPermissions> getCurrentUserPermissions(String userId) {
        List<SysRole> roleList = sysRoleService.getUserRoles(userId);
        HashMap<String, SysPermissions> permisMap = new HashMap<>();
        roleList.forEach(role -> role.getPermissionsList().forEach(permis -> permisMap.put(permis.getPermisScope().name(), permis)));
        return permisMap;
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
