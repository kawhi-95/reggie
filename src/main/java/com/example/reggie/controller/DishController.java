package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.Result;
import com.example.reggie.entity.Dish;
import com.example.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @GetMapping("page")
    public Result<Page<Dish>> page(Long pageSize, Long page){
        log.info("接收到查询菜品的请求");
        Page<Dish> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.orderByAsc(Dish::getSort);
        dishService.page(pageInfo, dishLambdaQueryWrapper);

        return Result.success(pageInfo);
    }


    @PostMapping
    public Result<String> save(@RequestBody Dish dish){
        log.info("新建菜品信息为: {}", dish);
        dishService.save(dish);
        return Result.success("保存菜品成功");
    }
}
