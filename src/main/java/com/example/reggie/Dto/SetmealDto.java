package com.example.reggie.Dto;

import com.example.reggie.entity.Dish;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;


@Data
public class SetmealDto extends Setmeal {
    private List<SetmealDish> dishList;
    private List<SetmealDish> setmealDishes;
}
