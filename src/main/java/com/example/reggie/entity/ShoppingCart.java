package com.example.reggie.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车
 */
@Data
public class ShoppingCart implements Serializable {

    private static final long serialVersionUID = 1L;

    // 前后端传递Long时，会出现精度错误，为了防止精度错误
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    //名称
    private String name;

    //用户id
    // 前后端传递Long时，会出现精度错误，为了防止精度错误
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    //菜品id
    // 前后端传递Long时，会出现精度错误，为了防止精度错误
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dishId;

    //套餐id
    // 前后端传递Long时，会出现精度错误，为了防止精度错误
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setmealId;

    //口味
    private String dishFlavor;

    //数量
    private Integer number;

    //金额
    private BigDecimal amount;

    //图片
    private String image;

    private LocalDateTime createTime;
}
