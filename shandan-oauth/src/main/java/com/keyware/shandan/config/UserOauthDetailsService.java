package com.keyware.shandan.config;

import com.keyware.shandan.beans.UserVo;
import com.keyware.shandan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UserOauthDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UserVo userVo = userService.findByLoginName(userName);
        if(userVo == null){
            throw new UsernameNotFoundException("用户未找到");
        }
        return new User(userVo.getLoginName(), userVo.getPassword(), new ArrayList<>());
    }
}
