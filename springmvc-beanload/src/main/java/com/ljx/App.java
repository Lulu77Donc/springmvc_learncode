package com.ljx;

import com.ljx.config.SpringConfig;
import com.ljx.controller.UserController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

    //测试@ComponentScan是否可以过滤掉controller注解下的bean
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
        UserController bean = ctx.getBean(UserController.class);
        System.out.println(bean);
    }
}
