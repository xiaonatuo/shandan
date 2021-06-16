package com.keyware.shandan.common.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.keyware.shandan.common.entity.Result;

import java.util.List;

/**
 * 通用服务类
 * @param <E> 实体类
 */
public interface IBaseService<E, T> extends IService<E> {

    List<E> list(QueryWrapper<E> wrapper);

    Result<E> get(T id);

    Page<E> page(Page<E> page, QueryWrapper<E> wrapper);

    Result<E> updateOrSave(E entity);

    Result<Boolean> deleteById(T Id);
}
