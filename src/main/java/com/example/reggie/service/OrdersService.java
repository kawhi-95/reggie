package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {
    public void submit(Orders orders);
}
