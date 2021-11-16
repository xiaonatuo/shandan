package com.keyware.shandan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keyware.shandan.beans.RoleVo;
import com.keyware.shandan.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService extends ServiceImpl<RoleMapper, RoleVo> {

    @Autowired
    private RoleMapper roleMapper;

    public List<RoleVo> listByUserId(String userId){
        return roleMapper.listByUserId(userId);
    }
}
