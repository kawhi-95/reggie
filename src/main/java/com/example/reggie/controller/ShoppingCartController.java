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
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        return Result.success(shoppingCartList);
    }

    @PostMapping("add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("接受到的shoppingCart: {}", shoppingCart.toString());
        // 设置用户id,指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        // 查看该菜品，或者套餐是否在购物车中，在就加一，不在就添加
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());
        shoppingCartLambdaQueryWrapper.eq(null != shoppingCart.getDishId(), ShoppingCart::getDishId, shoppingCart.getDishId());
        shoppingCartLambdaQueryWrapper.eq(null != shoppingCart.getSetmealId(), ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        ShoppingCart shopping = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
        if(null == shopping) {
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
        }else {
            shopping.setNumber(shopping.getNumber()+1);
            shoppingCartService.updateById(shopping);
            shoppingCart = shopping;
        }
        return Result.success(shoppingCart);
    }

//    @PostMapping(`"sub")
//    public Result<String> sub(){
//        shoppingCartService.removeById()
//        return Result.success("删除成功");
//    }`

    @DeleteMapping("clean")
    public Result<String> clean() {
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        return Result.success("清空成功");
    }
}
