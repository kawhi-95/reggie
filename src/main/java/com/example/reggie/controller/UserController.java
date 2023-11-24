package com.example.reggie.controller;

import com.example.reggie.common.Result;
import com.example.reggie.entity.User;
import com.example.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("login")
    public Result<User> login(HttpServletRequest httpServletRequest) {
        log.info("用户登录");
        User user = userService.getById(1l);
        httpServletRequest.getSession().setAttribute("user", user.getId());
        return Result.success(user);
    }
}
