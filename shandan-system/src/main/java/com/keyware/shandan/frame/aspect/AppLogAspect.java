package com.keyware.shandan.frame.aspect;

import com.alibaba.fastjson.JSONArray;
import com.keyware.shandan.common.entity.OperateLog;
import com.keyware.shandan.common.util.AspectUtil;
import com.keyware.shandan.frame.annotation.AppLog;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.system.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Date;

/**
 * 应用操作日志切面
 */
@Slf4j
@Aspect
@Component
public class AppLogAspect {



    @Before(value = "@annotation(com.keyware.shandan.frame.annotation.AppLog)")
    public void before(JoinPoint point){
        MethodSignature methodSignature = (MethodSignature)point.getSignature();
        AppLog appLog = methodSignature.getMethod().getAnnotation(AppLog.class);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes != null){
            String ip = IpUtil.getIpAddr(attributes.getRequest());
            // 当前登录用户
            User user = SecurityUtil.getLoginUser();

            OperateLog operateLog = new OperateLog();
            operateLog.setOperate(appLog.operate());
            operateLog.setLoginName(user.getUsername());
            operateLog.setIpAddr(ip);
            operateLog.setOperateTime(new Date());

            try {
                Object[] args = AspectUtil.parseJoinPointArgs(point, attributes.getRequest());

                JSONArray argsArray = new JSONArray();
                for (int i = 0; i < args.length; i++) {
                    if (!(args[i] instanceof ServletRequest || args[i] instanceof ServletResponse ||
                            args[i] instanceof MultipartFile || args[i] instanceof BindingResult) && args[i] != null) {
                        argsArray.add(args[i]);
                    }
                }

                operateLog.setParams(argsArray.toJSONString());
                log.info(operateLog.toString());
            } catch (Exception e) {
                e.printStackTrace();
                log.error("请求参数解析异常", e);
            }
        }
    }
}
