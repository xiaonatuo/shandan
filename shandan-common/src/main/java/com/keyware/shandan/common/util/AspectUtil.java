package com.keyware.shandan.common.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * AspectUtil
 *
 * @author Administrator
 * @since 2021/7/26
 */
public class AspectUtil {

    private final static ObjectMapper mapper = new ObjectMapper();

    /**
     * 解析连接点中的加密参数
     * @param joinPoint 切面连接点
     * @param request 请求
     * @return 解析后的参数
     * @throws Exception -
     */
    public static Object[] parseJoinPointArgs(JoinPoint joinPoint, HttpServletRequest request) throws Exception {
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        if(request.getMethod().equalsIgnoreCase("get") || ServletFileUpload.isMultipartContent(request)){
            return joinPoint.getArgs();
        }
        return transformArguments(AesUtil.decrypt(request), joinPoint);
    }


    /**
     * 转换解密参数
     * @param data 需要转换的数据
     * @param joinPoint ProceedingJoinPoint
     * @return 转换后的参数数组
     * @throws JsonProcessingException -
     */
    private static Object[] transformArguments(String data, JoinPoint joinPoint) throws JsonProcessingException {
        if(StringUtils.isBlank(data)){
            return joinPoint.getArgs();
        }
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
        Class<?>[] parameterTypes = methodSignature.getParameterTypes();
        // 遍历数组
        for (int i = 0; i < parameterNames.length; i++) {
            String paramName = parameterNames[i];
            Class<?> clazz = parameterTypes[i];
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
                try {
                    args[i] = mapper.readValue(data, args[i].getClass());
                } catch (Exception ignored) {
                }
            }
        }
        return args;
    }
}
