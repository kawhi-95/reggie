package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.Dto.SetmealDto;
import com.example.reggie.common.Result;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.entity.SetmealDish;
import com.example.reggie.service.SetmealDishService;
import com.example.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;
    @GetMapping("page")
    public Result<Page<Setmeal>> page(Long page, Long pageSize, String name){
        log.info("page:{},pageSize:{},name:{}", page, pageSize, name);
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.like(name!=null, Setmeal::getName, name);
        setmealLambdaQueryWrapper.orderByAsc(Setmeal::getCreateTime);
        setmealService.page(setmealPage, setmealLambdaQueryWrapper);
        return Result.success(setmealPage);
    }

    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public Result<String> add(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDishList(setmealDto);
        return Result.success("添加套餐成功");
    }

    @GetMapping("/{id}")
    public Result<SetmealDto> getSetmealDto(@PathVariable Long id){
        log.info("Setmeal id: {}", id);
        SetmealDto setmealDto = setmealService.getWithDishList(id);
        return Result.success(setmealDto);
    }

    @CacheEvict(value = "setmealCache", allEntries = true)
    @DeleteMapping
    public Result<String> delete(List<Long> ids) {
        setmealService.removeBatchByIds(ids);
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
        return Result.success("删除成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    @GetMapping("/list")
    public Result<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(null != setmeal.getCategoryId(), Setmeal::getCategoryId, setmeal.getCategoryId());
        setmealLambdaQueryWrapper.eq(null != setmeal.getStatus(), Setmeal::getStatus, setmeal.getStatus());

        List<Setmeal> list = setmealService.list(setmealLambdaQueryWrapper);
//        log.info(list.toString());
        return Result.success(list);
    }

}
