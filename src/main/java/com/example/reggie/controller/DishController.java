package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.Dto.DishDto;
import com.example.reggie.common.Result;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.DishFlavor;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.DishFlavorService;
import com.example.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("page")
    public Result<Page<DishDto>> page(Long pageSize, Long page, String name){
        log.info("接收到查询菜品的请求");
        // 分页构造器对象
        Page<DishDto> pageInfo = new Page<>(page, pageSize);
        Page<Dish> dishPage = new Page<>(page, pageSize);
        // 条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加条件
        dishLambdaQueryWrapper.orderByAsc(Dish::getSort);
        dishLambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        // 执行分页查询
        ////////
        // 1
        dishService.page(dishPage, dishLambdaQueryWrapper);
        // 2
        List<Dish> dishs = dishPage.getRecords();
        List<Long> categoryIds = new ArrayList<>();
        // 3
        for (Dish dish: dishs) {
            categoryIds.add(dish.getCategoryId());
        }
        // 5
        Map<Long, String> id2Category = new HashMap<>();
        for(Long id : categoryIds) {
            id2Category.put(id, categoryService.getById(id).getName());
        }
        // 6
        List<DishDto> dtoRecords = new ArrayList<>(dishs.size());
        // 7
        for (int i=0; i<dishs.size(); ++i) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dishs.get(i), dishDto);
            String categoryName = id2Category.get(dishs.get(i).getCategoryId());
            dishDto.setCategoryName(categoryName);
            dtoRecords.add(dishDto);
        }
        // 4
        BeanUtils.copyProperties(dishPage, pageInfo, "records");
        log.info("dtoRecords: {}", dtoRecords);
        pageInfo.setRecords(dtoRecords);

        return Result.success(pageInfo);
    }


    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto){
        log.info("新建菜品信息为: {}", dishDto);
        dishService.saveWithFlavor(dishDto);
        // 清理菜品的缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        redisTemplate.delete(key);
        return Result.success("保存菜品成功");
    }

    @GetMapping("{id}")
    public Result<DishDto> getByID(@PathVariable Long id){
        log.info("接收到菜品id, {}", id);
        DishDto dishDto =new DishDto();
        BeanUtils.copyProperties(dishService.getById(id), dishDto);
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
        dishDto.setFlavors(dishFlavorList);
        return Result.success(dishDto);
    }

    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto){
        log.info("修改dishDto信息: {}", dishDto.toString());

        dishService.updateById(dishDto);
        List<DishFlavor> flavors = dishDto.getFlavors();
        for(DishFlavor flavor : flavors) {
            flavor.setDishId(dishDto.getId());
        }
        // 清理菜品的缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        redisTemplate.delete(key);

        dishFlavorService.saveOrUpdateBatch(flavors);
        return Result.success("更新成功");
    }

    @GetMapping("list")
    public Result<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtos = null;
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        // 先从redis中获取缓存数据
        dishDtos = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if(dishDtos != null) {
            return Result.success(dishDtos);
        }
        // 如果不存在
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, dish.getCategoryId());
        dishLambdaQueryWrapper.eq(Dish::getStatus, 1);
        List<Dish> dishList = dishService.list(dishLambdaQueryWrapper);

        List<Long> dishIDs = new ArrayList<>();
        for(Dish disht : dishList) {
            dishIDs.add(disht.getId());
        }
        List<DishDto> dishDtoList = new ArrayList<>();
        for(int i=0; i<dishList.size(); ++i) {
            DishDto dishDto = new DishDto();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dishIDs.get(i));
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

            dishDto.setFlavors(dishFlavorList);
            BeanUtils.copyProperties(dishList.get(i), dishDto);
            dishDtoList.add(dishDto);
        }
        // 缓存到redis
        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);
        return Result.success(dishDtoList);
    }

    @DeleteMapping
    public Result<String> delete(@RequestParam("ids") List<Long> ids) {
        log.info("要删除的id为: {}", ids.toString());
        dishService.removeBatchByIds(ids);
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId, ids);
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
        return Result.success("删除成功");
    }
}
