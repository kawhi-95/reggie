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
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping
    public Result<String> delete(Long ids) {
        setmealService.removeById(ids);
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
        return Result.success("删除成功");
    }
}
