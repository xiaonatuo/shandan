package com.keyware.shandan.frame.aspect;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.AesUtil;
import com.keyware.shandan.common.util.AspectUtil;
import com.keyware.shandan.common.util.ErrorUtil;
import com.keyware.shandan.common.util.RsaUtil;
import com.keyware.shandan.system.utils.SysSettingUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * AES + RSA 加解密AOP处理
 */
@Slf4j
@Aspect
@Component
public class SafetyAspect {
    private static final String[] PATHS = {"/business/metadata/save/all","/search/metadata/condition/get","/search/metadata/condition/query"};
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(PATHS);
    //jackson
    private final ObjectMapper mapper = new ObjectMapper();
    /**
     * Pointcut 切入点
     */
    @Pointcut(value = "execution(public * com.keyware.shandan.*.controller.*.*(..))" )
    public void safetyAspect() {
    }

    /**
     * 环绕通知
     */
    @Around(value = "safetyAspect()")
    public Object around(ProceedingJoinPoint joinPoint) {
        //jackson 序列化和反序列化 date处理
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        try {
            //判断api加密开关是否开启
            if (!SysSettingUtil.getCurrentSysSetting().getSysApiEncrypt()) {
                return joinPoint.proceed(joinPoint.getArgs());
            }

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            assert attributes != null;
            //request对象
            HttpServletRequest request = attributes.getRequest();

            // 判断是否需要排除
            String path = request.getServletPath();
            if(EXCLUDE_PATHS.contains(path)){
                return joinPoint.proceed(joinPoint.getArgs());
            }

            //前端公钥
            String publicKey = request.getParameter("publicKey");

            Object result = joinPoint.proceed(AspectUtil.parseJoinPointArgs(joinPoint, request));
            //执行方法之前解密，且只拦截post请求
            if ("post".equalsIgnoreCase(request.getMethod()) && !ServletFileUpload.isMultipartContent(request)) {
                // 返回之前对结果进行加密
                //每次响应之前随机获取AES的key，加密data数据
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                String dataString = mapper.writeValueAsString(result);
                String key = AesUtil.getKey();
                String data = AesUtil.encrypt(dataString, key);

                //用前端的公钥来解密AES的key，并转成Base64
                String aesKey = Base64.encodeBase64String(RsaUtil.encryptByPublicKey(key.getBytes(), publicKey));

                //转json字符串并转成Object对象，设置到Result中并赋值给返回值o
                return Result.of(mapper.readValue("{\"data\":\"" + data + "\",\"aesKey\":\"" + aesKey + "\"}", Object.class));
            }else{
                return result;
            }

        } catch (Throwable e) {
            //输出到日志文件中
            log.error(ErrorUtil.errorInfoToString(e));
            return Result.of(null, false, e.getMessage());
        }
    }
}
