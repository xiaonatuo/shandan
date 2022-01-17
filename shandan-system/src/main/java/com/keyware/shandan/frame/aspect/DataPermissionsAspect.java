package com.keyware.shandan.frame.aspect;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.bianmu.entity.DirectoryVo;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
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
import java.util.concurrent.atomic.AtomicBoolean;

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

        //获取登录用户信息
        SysUser currentUser = SecurityUtil.getLoginSysUser();
        // 当前用户权限范围
        List<SysPermissions> permisList = getCurrentUserPermissions(currentUser.getUserId());
        DataPermissions annotation = getAnnotationByMethod(joinPoint, DataPermissions.class);

        AtomicBoolean isDeptAdmin = new AtomicBoolean(false);
        // 当前用户权限范围
        permisList.forEach(sp ->{
            if(DataPermisScope.ONLY_CURRENT_ORG.equals(sp.getPermisScope())){
                isDeptAdmin.set(true);
            }
        });

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

                        Object entity = wrapper.getEntity();
                        if (entity != null) {
                            String className = entity.getClass().getName();
                            //目录
                            if ("com.keyware.shandan.bianmu.entity.DirectoryVo".equals(className)) {
                                com.keyware.shandan.bianmu.entity.DirectoryVo dir = (DirectoryVo) entity;
                                //部门管理员
                                if(isDeptAdmin.get()){
                                    if((null == dir.getReviewStatus())){
                                        wrapper.eq(true, "CREATE_USER", currentUser.getUserId())
                                                .or(true, wp ->
                                                    wp.in(true, annotation.orgColumn(), orgIds)
                                                ).and(true, wp -> {
                                                    wp.or(true, wpe -> wpe.eq("REVIEW_STATUS", "UN_SUBMIT"));
                                                    wp.or(true, wpe -> wpe.eq("REVIEW_STATUS", "SUBMITTED"));
                                                    wp.or(true, wpe -> wpe.eq("REVIEW_STATUS", "PASS"));
                                                    wp.or(true, wpe -> wpe.eq("REVIEW_STATUS", "FAIL"));
                                                });

                                    }else if(dir != null && null!=dir.getReviewStatus() && null!=dir.getReviewStatus().getValue()){
                                        wrapper.eq("REVIEW_STATUS", dir.getReviewStatus().getValue())
                                                .and(true, wp -> wp.in(true, annotation.orgColumn(), orgIds));
                                    }

                                }else{
                                    //普通人员
                                    if(dir != null && null!=dir.getReviewStatus() && null!=dir.getReviewStatus().getValue()){
                                        wrapper.eq(true, "CREATE_USER", currentUser.getUserId())
                                                .and(true, wp -> {
                                                    wp.in(true, annotation.orgColumn(), orgIds);
                                                    wp.and(true, wpe -> wpe.eq("REVIEW_STATUS", dir.getReviewStatus().getValue()));
                                                });

                                    }else{
                                        wrapper.eq(true, "CREATE_USER", currentUser.getUserId())
                                                .and(true, wp -> {
                                                    wp.in(true, annotation.orgColumn(), orgIds);
                                                    wp.or(true, wpe -> wpe.eq("REVIEW_STATUS", "UN_SUBMIT"));
                                                    wp.or(true, wpe -> wpe.eq("REVIEW_STATUS", "SUBMITTED"));
                                                    wp.or(true, wpe -> wpe.eq("REVIEW_STATUS", "PASS"));
                                                    wp.or(true, wpe -> wpe.eq("REVIEW_STATUS", "FAIL"));
                                                });

                                    }
                                }
                            }
                            //元数据
                            if("com.keyware.shandan.bianmu.entity.MetadataBasicVo".equals(className)){
                                com.keyware.shandan.bianmu.entity.MetadataBasicVo vo = (MetadataBasicVo) entity;
                                if(isDeptAdmin.get()){
                                    //部门管理员
                                    if((null == vo.getReviewStatus())){
                                        wrapper.eq(true, "CREATE_USER", currentUser.getUserId())
                                                .or(true, wp -> wp.in(true, annotation.orgColumn(), orgIds))
                                                .and(true, wp -> {
                                                    wp.or(true, wpe -> wpe.eq("REVIEW_STATUS", "UN_SUBMIT"));
                                                    wp.or(true, wpe -> wpe.eq("REVIEW_STATUS", "SUBMITTED"));
                                                    wp.or(true, wpe -> wpe.eq("REVIEW_STATUS", "PASS"));
                                                    wp.or(true, wpe -> wpe.eq("REVIEW_STATUS", "FAIL"));
                                                });
                                    }else if(vo != null && null!=vo.getReviewStatus() && null!=vo.getReviewStatus().getValue()){
                                            wrapper.eq("REVIEW_STATUS", vo.getReviewStatus().getValue())
                                                    .and(true, wp -> wp.in(true, annotation.orgColumn(), orgIds));
                                    }

                                }else{
                                    //普通人员
                                    if(vo != null && null!=vo.getReviewStatus() && null!=vo.getReviewStatus().getValue()){
                                        wrapper.eq(true, "CREATE_USER", currentUser.getUserId())
                                                .and(true, wp -> {
                                                    wp.in(true, annotation.orgColumn(), orgIds);
                                                    wp.and(true, wpe -> wpe.eq("REVIEW_STATUS", vo.getReviewStatus().getValue()));
                                                });

                                    }else{
                                        wrapper.eq(true, "CREATE_USER", currentUser.getUserId())
                                                .and(true, wp -> {
                                                    wp.in(true, annotation.orgColumn(), orgIds);
                                                    wp.or(true, wpe -> wpe.eq("REVIEW_STATUS", "UN_SUBMIT"));
                                                    wp.or(true, wpe -> wpe.eq("REVIEW_STATUS", "SUBMITTED"));
                                                    wp.or(true, wpe -> wpe.eq("REVIEW_STATUS", "PASS"));
                                                    wp.or(true, wpe -> wpe.eq("REVIEW_STATUS", "FAIL"));
                                                });

                                    }
                                }
                            }
                        }

                    }

                    args[i] = wrapper;
                    return joinPoint.proceed(args);
                }
            }
        }else {
            //admin
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof QueryWrapper) {
                    QueryWrapper<Object> wrapper = (QueryWrapper<Object>) args[i];
                    Object entity = wrapper.getEntity();

                    if (entity != null) {
                        String className = entity.getClass().getName();
                        if("com.keyware.shandan.bianmu.entity.MetadataBasicVo".equals(className)){
                            com.keyware.shandan.bianmu.entity.MetadataBasicVo vo = (MetadataBasicVo) entity;
                            if(null!=vo.getReviewStatus() && null!=vo.getReviewStatus().getValue()){
                                wrapper.eq("REVIEW_STATUS", vo.getReviewStatus().getValue());
                            }
                            args[i] = wrapper;
                            return joinPoint.proceed(args);
                        }else if("com.keyware.shandan.bianmu.entity.DirectoryVo".equals(className)) {
                            com.keyware.shandan.bianmu.entity.DirectoryVo dio = (DirectoryVo) entity;
                            if(null != dio.getReviewStatus() && null != dio.getReviewStatus().getValue()){
                                wrapper.eq("REVIEW_STATUS", dio.getReviewStatus().getValue());
                            }
                            return joinPoint.proceed();
                        }
                    }

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

}
