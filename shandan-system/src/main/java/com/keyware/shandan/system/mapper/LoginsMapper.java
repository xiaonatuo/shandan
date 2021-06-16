package com.keyware.shandan.system.mapper;

import com.keyware.shandan.common.mapper.IBaseMapper;
import com.keyware.shandan.system.entity.Logins;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * persistent_logins表，用户实现记住我功能 Mapper 接口
 * </p>
 *
 * @author GuoXin
 * @since 2021-05-07
 */
@Mapper
public interface LoginsMapper extends IBaseMapper<Logins> {

}
