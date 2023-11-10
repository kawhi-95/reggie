package com.example.reggie.common;

/**
 * 自定义业务异常
 */
public class CustomException extends RuntimeException{
    public CustomException(String messgae) {
        super(messgae);
    }
}
