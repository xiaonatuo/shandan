package com.keyware.shandan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keyware.shandan.beans.UserVo;
import com.keyware.shandan.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, UserVo> {

    @Autowired
    private RoleService roleService;

    public UserVo findByLoginName(String loginName){
        QueryWrapper<UserVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("LOGIN_NAME", loginName);
        UserVo user = getOne(queryWrapper);
        if(user == null){
            return null;
        }
        user.setRoles(roleService.listByUserId(user.getUserId()));
        return user;
    }
}
