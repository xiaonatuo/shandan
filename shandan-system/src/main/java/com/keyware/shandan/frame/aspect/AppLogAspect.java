package com.keyware.shandan.frame.aspect;

import com.alibaba.fastjson.JSONArray;
import com.keyware.shandan.system.entity.SysOperateLog;
import com.keyware.shandan.common.util.AspectUtil;
import com.keyware.shandan.frame.annotation.AppLog;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.system.service.SysOperateLogService;
import com.keyware.shandan.system.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 应用操作日志切面
 */
@Slf4j
@Aspect
@Component
public class AppLogAspect {

    @Autowired
    private SysOperateLogService operateLogService;

    @Before(value = "@annotation(com.keyware.shandan.frame.annotation.AppLog)")
    public void before(JoinPoint point){
        MethodSignature methodSignature = (MethodSignature)point.getSignature();
        AppLog appLog = methodSignature.getMethod().getAnnotation(AppLog.class);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes != null){
            HttpServletRequest request = attributes.getRequest();
            String ip = IpUtil.getIpAddr(request);
            // 当前登录用户
            User user = SecurityUtil.getLoginUser();

            SysOperateLog sysOperateLog = new SysOperateLog();
            sysOperateLog.setOperate(appLog.operate());
            sysOperateLog.setLoginName(user.getUsername());
            sysOperateLog.setUrl(request.getRequestURI());
            sysOperateLog.setIpAddr(ip);
            sysOperateLog.setOperateTime(new Date());

            try {
                Object[] args = AspectUtil.parseJoinPointArgs(point, attributes.getRequest());

                JSONArray argsArray = new JSONArray();
                for (Object arg : args) {
                    if (!(arg instanceof ServletRequest || arg instanceof ServletResponse ||
                            arg instanceof MultipartFile || arg instanceof BindingResult) && arg != null) {
                        argsArray.add(arg);
                    }
                }

                sysOperateLog.setParams(argsArray.toJSONString());
                operateLogService.save(sysOperateLog);
                log.info(sysOperateLog.toString());
            } catch (Exception e) {
                e.printStackTrace();
                log.error("请求参数解析异常", e);
            }
        }
    }
}
