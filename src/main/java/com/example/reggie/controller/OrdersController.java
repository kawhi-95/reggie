package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.Result;
import com.example.reggie.entity.Orders;
import com.example.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @GetMapping("page")
    public Result<Page<Orders>> page(Long page, Long pageSize, Long number, String beginTime, String endTime){
        log.info("page:{}", page);
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.like(null!=number, Orders::getNumber, number);
        //添加 查询条件 动态sql 字符串使用StringUtils.isNotEmpty这个方法来判断
        ordersLambdaQueryWrapper
                .gt(StringUtils.isNotEmpty(beginTime),Orders::getOrderTime,beginTime)
                .lt(StringUtils.isNotEmpty(endTime),Orders::getOrderTime,endTime);
        ordersService.page(ordersPage, ordersLambdaQueryWrapper);
        return Result.success(ordersPage);
    }

    @GetMapping("userPage")
    public Result<Page<Orders>> userPage(Long page, Long pageSize ){
        log.info("page:{}", page);
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        ordersService.page(ordersPage);
        return Result.success(ordersPage);
    }

    @PostMapping("submit")
    public Result<String> submit(@RequestBody Orders orders) {

        return Result.success("支付成功");
    }

}
