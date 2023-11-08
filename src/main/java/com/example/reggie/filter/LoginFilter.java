package com.example.reggie.filter;

import com.alibaba.fastjson2.JSON;
import com.example.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    // 不需要处理的路径
    private final String[] freeURLS = new String[]{
            "/employee/login",
            "employee/logout",
            "/backend/**",
            "/front/**"
    };

    /**
     * 检查用户是否完成登录
     * 1.获取本次请求的uri
     * 2.判断本次请求是否需要处理
     * 3.不需要直接放行
     * 4.判断登陆状态，登陆状态直接放行
     * 5.未登录返回登陆页面,通过输出流方式向客户端响应数据
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 1.
        String requestURI = request.getRequestURI();

        log.info("接收到请求：{}", requestURI);
        // 2.
        boolean check = check(requestURI);
        // 3.
        if (check) {
            log.info("本次请求不需要处理");
            filterChain.doFilter(request, response);
            return;
        }
        // 4.
        if (request.getSession().getAttribute("employee") != null) {
            log.info("本次请求用户已经登陆, 用户id为：{}", request.getSession().getAttribute("employee"));
            filterChain.doFilter(request, response);
            return;
        }
        log.info("本次请求用户未登录");
        // 5.
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
    }

    /**
     * 用来判断本次请求是否要放行
     * @param requestURI
     * @return
     */
    private boolean check(String requestURI) {
        for(String url : freeURLS) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match) return true;
        }
        return false;
    }
}
