package com.keyware.shandan.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keyware.shandan.beans.DictVo;
import com.keyware.shandan.mapper.DictMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 */
@Service
public class DictService extends ServiceImpl<DictMapper, DictVo> {

    @Autowired
    private DictMapper dictMapper;

    public DictVo getDictByTypeAndCode(String type, String code){

        return dictMapper.selectByTypeIdAndDictCode(type, code);
    }

    public String getDesktopUrl(){
        return dictMapper.selectDesktopUrl();
    }
}
