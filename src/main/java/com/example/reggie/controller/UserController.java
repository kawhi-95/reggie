package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.reggie.common.Result;
import com.example.reggie.entity.User;
import com.example.reggie.service.UserService;
import com.example.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.baomidou.mybatisplus.core.toolkit.StringUtils.*;


@Slf4j
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 发送手机验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone=user.getPhone();

        if(isNotEmpty(phone)){
            //生成随机四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);

            //需要将生成的验证码保存到Session
//            session.setAttribute(phone,code);

            //将生成的验证码缓存到redis中，有效期为5分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return Result.success("手机验证码短信发送成功");

        }

        return Result.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     * @param user
     * @param session
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map user, HttpSession session){

        log.info(user.toString());

        //获取手机号
        String phone = user.get("phone").toString();

        //获取验证码
        String code = user.get("code").toString();

        //从Session中获取保存的验证码
//        Object codeInSession = session.getAttribute(phone);
        //从redis中获取缓存的验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        //进行验证码的比对(页面提交的验证码和Session中保存的验证码比对)
        if(codeInSession!=null&&codeInSession.equals(code)) {
            //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User u = userService.getOne(queryWrapper);
            if(u==null){
                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                u=new User();
                u.setPhone(phone);
                u.setStatus(1);
                userService.save(u);
            }
            session.setAttribute("user",u.getId());
            //如果用户登录成功则删除Redis中缓存的验证码
            redisTemplate.delete(phone);
            return Result.success(u);
        }

        return Result.error("登录失败");
    }

    /**
     * C端用户退出功能
     * ①在controller中创建对应的处理方法来接受前端的请求，请求方式为post；
     * ②清理session中的用户id
     * ③返回结果（前端页面会进行跳转到登录页面）
     * @param request
     * @return
     */
    @PostMapping("/loginout")
    public Result<String> logout(HttpServletRequest request){
        request.removeAttribute("user");
        return Result.success("退出成功");
    }
}
