package com.keyware.shandan.frame.config.security;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keyware.shandan.system.utils.IpUtil;
import com.keyware.shandan.system.utils.SysSettingUtil;
import com.keyware.shandan.common.util.*;
import com.keyware.shandan.system.entity.SysUser;
import com.keyware.shandan.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * 登录成功处理，登陆成功后还需要验证账号的有效性
 */
@Component
@Slf4j
public class LoginSuccessHandlerConfig implements AuthenticationSuccessHandler {
    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private SysUserService sysUserService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {

        //查询当前与系统交互的用户，存储在本地线程安全上下文，校验账号有效性
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SysUser sysUser = null;
        if(principal instanceof User){
            User user = (User) principal;
            sysUser = sysUserService.findByLoginName(user.getUsername()).getData();
        }else{
            sysUser = sysUserService.findByLoginName((String) principal).getData();
        }


        //默认登陆成功
        String msg = "{\"code\":\"300\",\"msg\":\"登录成功\",\"url\":\"/index\"}";
        boolean flag = false;

        //登陆IP不在白名单
        String ipAddr = IpUtil.getIpAddr(httpServletRequest);
        String limitedIp = sysUser.getLimitedIp();
        if(StringUtils.isNotBlank(limitedIp) && !Arrays.asList(limitedIp.split(",")).contains(ipAddr)){
            msg = "{\"code\":\"400\",\"msg\":\"登陆IP不在白名单，请联系管理员\"}";
            flag = true;
        }

        //禁止多人在线
        if(!sysUser.getLimitMultiLogin() &&  securityUtil.sessionRegistryGetUserBySessionId(httpServletRequest.getRequestedSessionId()) != null){
            msg = "{\"code\":\"400\",\"msg\":\"该账号禁止多人在线，请联系管理员\"}";
            flag = true;
        }

        //超出有效时间
        if(!ObjectUtils.isEmpty(sysUser.getExpiredTime()) && new Date().getTime() > sysUser.getExpiredTime().getTime()){
            msg = "{\"code\":\"400\",\"msg\":\"该账号已失效，请联系管理员\"}";
            flag = true;
        }

        //禁止登陆系统
        if(!sysUser.getValid()){
            msg = "{\"code\":\"400\",\"msg\":\"该账号已被禁止登陆系统，请联系管理员\"}";
            flag = true;
        }

        //校验不通过
        if(flag){
            //清除当前的上下文
            SecurityContextHolder.clearContext();

            //清除remember-me持久化tokens
            //securityUtil.rememberMeRemoveUserTokens(user.getUsername());
        } else{
            //校验通过，注册session
            //securityUtil.sessionRegistryAddUser(httpServletRequest.getSession().getId(),user);
        }

        //判断api加密开关是否开启
        if(SysSettingUtil.getCurrentSysSetting().getSysApiEncrypt()) {
            //加密
            try {
                //前端公钥
                String publicKey = httpServletRequest.getParameter("publicKey");

                //jackson
                ObjectMapper mapper = new ObjectMapper();
                //jackson 序列化和反序列化 date处理
                mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                //每次响应之前随机获取AES的key，加密data数据
                String key = AesUtil.getKey();
                String data = AesUtil.encrypt(msg, key);

                //用前端的公钥来解密AES的key，并转成Base64
                String aesKey = Base64.encodeBase64String(RsaUtil.encryptByPublicKey(key.getBytes(), publicKey));

                msg = "{\"data\":{\"data\":\"" + data + "\",\"aesKey\":\"" + aesKey + "\"}}";
            } catch (Throwable e) {
                //输出到日志文件中
                log.error(ErrorUtil.errorInfoToString(e));
            }
        }

        //转json字符串并转成Object对象，设置到Result中并赋值给返回值o
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        PrintWriter out = httpServletResponse.getWriter();
        out.print(msg);
        out.flush();
        out.close();
        httpServletResponse.flushBuffer();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }
}
