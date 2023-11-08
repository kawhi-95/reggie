package com.example.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.Result;
import com.example.reggie.entity.Employee;
import com.example.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param httpServletRequest
     * @param employee
     * @return
     * 1.将页面提交的密码password进行MD5加密
     * 2.根据提交的用户名username查询数据库
     * 3.查询到没有返回登陆失败
     * 4.对比密码，不一致返回登陆失败
     * 5.员工状态如果是已经禁用，返回已经禁用
     * 6.登陆成功，将id存到session，并返回登陆成功结果
     */
    @PostMapping("login")
    public Result<Employee> login(HttpServletRequest httpServletRequest, @RequestBody Employee employee) {
        // 1.
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        System.out.println(password);
        // 2.
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee one = employeeService.getOne(queryWrapper);
        System.out.println(one);
        // 3.
        if(one==null) {
            return Result.error("登陆失败，不存在相关人员");
        }
        // 4. 要使用equals
        if(!(one.getPassword() .equals(password))) {
            return Result.error("登陆失败，密码错误");
        }
        // 5.
        if(one.getStatus()!=1) {
            return Result.error("该账户已被锁定");
        }
        // 6.
        httpServletRequest.getSession().setAttribute("employee", one.getId());
        return Result.success(one);
    }

    /**
     * 1. 清理session中的用户id
     * 2. 返回结果
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("logout")
    public Result<String> logout(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }


    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public Result<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        // 加密初始密码密码123456并设置
        String password =  DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(password);

        // 获取当前登录人的id
        // Long empID = (Long) request.getSession().getAttribute("employee");

        // 设置初始信息
        //  employee.setCreateTime(LocalDateTime.now());
        //  employee.setUpdateTime(LocalDateTime.now());
        //  employee.setUpdateUser(empID);
        //  employee.setCreateUser(empID);
        employee.setStatus(0);

        employeeService.save(employee);
        log.info("新增员工，员工信息为：{}", employee);
        return Result.success("新增员工成功");
    }
    /*
    1. 页面发出请求，将分页请求参数（page，pagesize，name）提交到服务器
    2. 服务端controller接受页面提交的数据并调用service查询数据
    3.Service使用mapper操作数据库，查询分页数据
    4.Controller将查询到的数据响应给页面
    5.页面显示
     */
    @GetMapping("page")
    public Result<Page<Employee>> page(Integer page, Integer pageSize, String name) {
        log.info("page: {}, pageSize: {}, name: {}", page, pageSize, name);
        // 构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        // 构建条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // 添加一个过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询
        employeeService.page(pageInfo, queryWrapper);
        return Result.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public Result<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info(employee.toString());

        Long empID = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empID);

        employeeService.updateById(employee);
        return Result.error("更新信息成功");
    }

    @GetMapping("/{id}")
    public Result<Employee> getByID(@PathVariable Long id) {
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }
}
