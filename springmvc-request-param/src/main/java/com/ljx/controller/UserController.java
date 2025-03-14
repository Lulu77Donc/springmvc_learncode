package com.ljx.controller;

import com.ljx.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    @RequestMapping("/commonParamDifferentName")
    @ResponseBody
    //普通参数：请求参数名与形参名不同
    //在发送请求时，不同名称参数传参会无法识别，所以需要加上requestparam
    public String commonParamDifferentName(@RequestParam("name") String userName, Integer age){
        System.out.println("普通参数传递 userName ==>" + userName);
        System.out.println("普通参数传递 age ==>" + age);
        return "{'module':'common param different name'}";
    }

    //POJO参数
    @RequestMapping("/pojoParam")
    @ResponseBody
    //普通参数：请求参数名与形参名不同
    //在发送请求时，不同名称参数传参会无法识别，所以需要加上requestparam
    public String pojoParam(User user){
        System.out.println("pojo参数传递 user ==>" + user);
        return "{'module':'pojo param'}";
    }


}
