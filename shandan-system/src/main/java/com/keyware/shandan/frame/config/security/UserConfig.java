package com.keyware.shandan.frame.config.security;

import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.system.entity.SysUser;
import com.keyware.shandan.system.entity.SysUserRole;
import com.keyware.shandan.system.service.SysUserRoleService;
import com.keyware.shandan.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class UserConfig implements UserDetailsService {

    private static ConcurrentHashMap<String, SysUser> loginUserCache = new ConcurrentHashMap<>();

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //查询用户
        SysUser user = sysUserService.findByLoginName(username).getData();
        //查询权限
        List<SysUserRole> userRoles = sysUserRoleService.findByUserId(user.getUserId()).getData();
        List<String> roles = userRoles.stream().map(userRole -> userRole.getRole().getRoleName()).collect(Collectors.toList());

        //查无此用户
        if(StringUtils.isBlank(user.getUserId())){
            user.setLoginName("查无此用户");
            user.setPassword("查无此用户");
        }

        addLoginUser(user);
        // 封装用户信息，并返回。参数分别是：用户名，密码，用户权限
        return new User(user.getLoginName(), user.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",", roles)));
    }

    /**
     * 添加登录用户缓存
     * @param user
     */
    public void addLoginUser(SysUser user){
        loginUserCache.put(user.getLoginName(), user);
    }

    /**
     * 移除登录缓存
     * @param loginName
     */
    public static void removeLoginUser(String loginName){
        loginUserCache.remove(loginName);
    }

    /**
     * 根据登录名获取登录用户信息
     * @param loginName
     * @return
     */
    public static SysUser getLoginUserByLoginName(String loginName){
        return loginUserCache.get(loginName);
    }
}
