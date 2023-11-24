package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.entity.DishFlavor;
import com.example.reggie.mapper.DIshFlavorMapper;
import com.example.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DIshFlavorMapper, DishFlavor> implements DishFlavorService {
}
