package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.Result;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.ShoppingCart;
import com.example.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("list")
    public Result<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<ShoppingCart> shoppingCartList = shoppingCartService.list();
        return Result.success(shoppingCartList);
    }

    @PostMapping("add")
    public Result<String> add(@RequestBody ShoppingCart shoppingCart) {
        //设置用户id,指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        log.info("接受到的shoppingCart: {}", shoppingCart.toString());
        shoppingCartService.save(shoppingCart);
        return Result.success("添加成功");
    }

//    @PostMapping(`"sub")
//    public Result<String> sub(){
//        shoppingCartService.removeById()
//        return Result.success("删除成功");
//    }`
}
