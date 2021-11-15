package com.keyware.shandan.mapper;

import com.keyware.shandan.beans.UserVo;
import com.keyware.shandan.common.mapper.IBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends IBaseMapper<UserVo> {
}
