package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.Dto.SetmealDto;
import com.example.reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDishList(SetmealDto setmealDto);

    SetmealDto getWithDishList(Long id);
}
