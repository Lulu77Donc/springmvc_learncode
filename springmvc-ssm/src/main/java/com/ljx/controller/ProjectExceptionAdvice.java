package com.ljx.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProjectExceptionAdvice {
    @ExceptionHandler(Exception.class)//负责拦截异常种类
    public Result doException(Exception ex){
        System.out.println("异常哪里跑");
        return new Result(666,null,"异常哪里跑");
    }
}
