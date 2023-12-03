package com.example.reggie;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

// 用来看日志
@Slf4j
// 该注解会去扫描@WebFilter注解
@SpringBootApplication
@ServletComponentScan
// 开启对@Transactional的支持
@EnableTransactionManagement
// 开启Spring Cache注解方式的缓存功能
@EnableCaching
public class ReggieApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
        // 类上添加注解，在方法中就可以是使用log来输出日志
        log.info("项目启动成功.......");
    }

}
