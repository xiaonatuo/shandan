package com.keyware.shandan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.keyware.shandan.beans.DictVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author Administrator
 */
@Mapper
public interface DictMapper extends BaseMapper<DictVo> {

    /**
     * 根据字典类型ID和字典编码获取数据
     *
     * @param typeId   字典类型ID
     * @param dictCode 字典编码
     * @return 字典项
     */
    @Select("select * from SYS_DICT where TYPE_ID = #{typeId} and DICT_CODE=#{dictCode}")
    DictVo selectByTypeIdAndDictCode(String typeId, String dictCode);

    /**
     * 查询桌面应用访问地址
     *
     * @return 访问地址
     */
    @Select("select SYS_ADDRESS from SYS_SETTING where ID = 'DESKTOP'")
    String selectDesktopUrl();
}
