package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.Result;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Dish;
import com.example.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐分类
     * @param category
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody Category category) {
        log.info("category: {}", category);
        categoryService.save(category);
        return Result.success("新增分类成功");
    }

    /**
     * 进行分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("page")
    public Result<Page<Category>> page(Integer page, Integer pageSize) {
        log.info("page: {}, pageSize: {}, name: {}", page, pageSize);
        // 构造分页构造器
        Page<Category> pageInfo = new Page(page, pageSize);
        // 构建条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 添加一个过滤条件
        queryWrapper.orderByAsc(Category::getSort);

        log.info(pageInfo.toString());

        // 执行查询
        categoryService.page(pageInfo, queryWrapper);

        return Result.success(pageInfo);
    }

    /**
     * 删除套餐
     * @param id
     * @return
     */
    @DeleteMapping
    public Result<String> delete(Long id) {
        log.info("分类id为：{}", id);
        categoryService.remove(id);
        return Result.success("分类删除成功");
    }

    @PutMapping
    public Result<String> updata(HttpServletRequest request, @RequestBody Category category){
        categoryService.updateById(category);
        return Result.success("更新成功");
    }

    @GetMapping("list")
    public Result<List<Category>> list(Long type) {
        log.info("接收到请求类型为: {}分类", type);
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(Category::getType, type);

        List<Category> categories = categoryService.list(categoryLambdaQueryWrapper);

        return Result.success(categories);
    }


}
