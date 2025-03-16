package com.ljx.controller;

import com.ljx.exception.BusinessException;
import com.ljx.exception.SystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProjectExceptionAdvice {
    @ExceptionHandler(SystemException.class)//负责拦截异常种类
    public Result doSystemException(SystemException ex){
        //记录日志
        //发送消息给运维
        //发送邮件给开发人员
        return new Result(ex.getCode(), null, ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)//负责拦截异常种类
    public Result doBusinessException(BusinessException ex){
        return new Result(ex.getCode(), null, ex.getMessage());
    }

    //处理其他异常
    @ExceptionHandler(Exception.class)//负责拦截异常种类
    public Result doException(Exception ex){
        //记录日志
        //发送消息给运维
        //发送邮件给开发人员
        return new Result(Code.SYSTEM_UNKNOW_ERR,null,"系统繁忙，稍后再试");
    }
}
