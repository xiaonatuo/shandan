package com.keyware.shandan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.keyware.shandan.beans.RoleVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<RoleVo> {

    @Select("select R.* from SYS_ROLE R inner join SYS_USER_ROLE UR on UR.ROLE_ID = R.ROLE_ID and UR.USER_ID = #{userId}")
    List<RoleVo> listByUserId(String userId);
}
