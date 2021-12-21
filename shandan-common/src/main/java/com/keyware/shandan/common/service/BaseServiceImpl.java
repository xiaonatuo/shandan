package com.keyware.shandan.common.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.mapper.IBaseMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

/**
 * 通用服务实现类
 *
 * @param <M> Mapper映射类
 * @param <E> 实体类
 * @param <T> id类型
 */
@SuppressWarnings("unchecked")
public class BaseServiceImpl<M extends IBaseMapper<E>, E, T> extends ServiceImpl<M, E> implements IBaseService<E, T> {

    @Autowired
    private M baseMapper;

    @Override
    public List<E> list(QueryWrapper<E> wrapper) {
        return super.list(wrapper);
    }

    @Override
    public Result<E> get(T id) {
        return Result.of(super.getById((Serializable) id));
    }


    @Override
    public Page<E> page(Page<E> page, QueryWrapper<E> wrapper) {
        return super.page(page, wrapper);
    }

    @Override
    public Result<E> updateOrSave(E entity) throws Exception {
        boolean ok = super.saveOrUpdate(entity);
        return Result.of(ok ? entity : null, ok);
    }

    @Override
    public Result<Boolean> deleteById(T id) {
        boolean ok = super.removeById((Serializable) id);
        return Result.of(ok, ok);
    }

    @Override
    protected Class<E> currentMapperClass() {
        return (Class<E>) this.getResolvableType().as(BaseServiceImpl.class).getGeneric(0).getType();
    }

    @Override
    protected Class<E> currentModelClass() {
        return (Class<E>) this.getResolvableType().as(BaseServiceImpl.class).getGeneric(1).getType();
    }
}
