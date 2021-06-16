package com.keyware.shandan.frame.aspect;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.AesUtil;
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
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * AES + RSA 加解密AOP处理
 */
@Slf4j
@Aspect
@Component
public class SafetyAspect {
    //jackson
    private final ObjectMapper mapper = new ObjectMapper();
    /**
     * Pointcut 切入点
     * 匹配
     * cn.huanzi.qch.baseadmin.system.*.controller、
     * cn.huanzi.qch.baseadmin.*.controller包下面的所有方法
     */
    @Pointcut(value = "execution(public * com.keyware.shandan.bianmu.*.controller.*.*(..))" )
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
            if ("N".equals(SysSettingUtil.getSysSetting().getSysApiEncrypt())) {
                return joinPoint.proceed(joinPoint.getArgs());
            }

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            assert attributes != null;
            //request对象
            HttpServletRequest request = attributes.getRequest();

            //前端公钥
            String publicKey = request.getParameter("publicKey");

            //执行方法之前解密，且只拦截post请求
            if ("post".equalsIgnoreCase(request.getMethod()) && !ServletFileUpload.isMultipartContent(request)) {
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                //执行并替换最新形参参数   PS：这里有一个需要注意的地方，method方法必须是要public修饰的才能设置值，private的设置不了
                Object result = joinPoint.proceed(transformArguments(AesUtil.decrypt(request), joinPoint));

                // 返回之前对结果进行加密
                //每次响应之前随机获取AES的key，加密data数据
                String dataString = mapper.writeValueAsString(result);
                String key = AesUtil.getKey();
                String data = AesUtil.encrypt(dataString, key);

                //用前端的公钥来解密AES的key，并转成Base64
                String aesKey = Base64.encodeBase64String(RsaUtil.encryptByPublicKey(key.getBytes(), publicKey));

                //转json字符串并转成Object对象，设置到Result中并赋值给返回值o
                return Result.of(mapper.readValue("{\"data\":\"" + data + "\",\"aesKey\":\"" + aesKey + "\"}", Object.class));
            }else{
                return joinPoint.proceed(joinPoint.getArgs());
            }

        } catch (Throwable e) {
            //输出到日志文件中
            log.error(ErrorUtil.errorInfoToString(e));
            return Result.of(null, false, e.getMessage());
        }
    }

    /**
     * 转换解密参数
     * @param data 需要转换的数据
     * @param joinPoint ProceedingJoinPoint
     * @return 转换后的参数数组
     * @throws JsonProcessingException
     */
    private Object[] transformArguments(String data, ProceedingJoinPoint joinPoint) throws JsonProcessingException {
        Object[] args = joinPoint.getArgs();
        JsonNode node = null;
        try {
            node = mapper.readTree(data);
        } catch (JsonParseException e) {
            e.printStackTrace();
            return args;
        }
        // 先拿到方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 通过方法签名拿到方法参数名称数组和方法参数类型数组
        String[] parameterNames = methodSignature.getParameterNames();
        Class[] parameterTypes = methodSignature.getParameterTypes();
        // 遍历数组
        for (int i = 0; i < parameterNames.length; i++) {
            String paramName = parameterNames[i];
            Class clazz = parameterTypes[i];
            // 从JsonNode中根据指定参数名称拿出对应参数
            JsonNode js = node.get(paramName);
            if (js != null) {
                // 判断参数类型，将JsonNode转换并赋值给参数
                if (String.class.isAssignableFrom(clazz)) {
                    args[i] = js.asText();
                } else if (Long.class.isAssignableFrom(clazz)) {
                    args[i] = js.asLong();
                } else if (Integer.class.isAssignableFrom(clazz)) {
                    args[i] = js.asInt();
                } else if (Double.class.isAssignableFrom(clazz)) {
                    args[i] = js.asDouble();
                } else if (Boolean.class.isAssignableFrom(clazz)) {
                    args[i] = js.asBoolean();
                } else if (clazz.isLocalClass()) {
                    args[i] = mapper.readValue(js.toString(), clazz);
                }else{
                    try {
                        args[i] = mapper.readValue(js.toString(), args[i].getClass());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else if(args[i] != null){
                args[i] = mapper.readValue(data, args[i].getClass());
            }
        }
        return args;
    }
}
