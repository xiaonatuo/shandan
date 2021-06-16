package com.keyware.shandan.common.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * 通用controller类
 * @param <S> 服务类泛型
 * @param <E> 实体类泛型
 * @param <T> 实体类主键对应类型泛型
 */
public class BaseController<S extends IBaseService<E, T>, E, T> {

    @Autowired
    private S commonService;


    @PostMapping("/page")
    public Result<Page<E>> page(Page<E> page, E entity){
        return Result.of(commonService.page(page, new QueryWrapper<>(entity)));
    }

    @PostMapping("list")
    public Result<List<E>> list(E entityVo) {
        return Result.of(commonService.list(new QueryWrapper<>(entityVo)));
    }

    @GetMapping("get/{id}")
    public Result<E> get(@PathVariable("id") T id) {
        return commonService.get(id);
    }

    @PostMapping("save")
    public Result<E> save(E entityVo) {
        return commonService.updateOrSave(entityVo);
    }

    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete( @PathVariable("id") T id) {
        return commonService.deleteById(id);
    }
}
