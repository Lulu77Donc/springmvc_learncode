package com.ljx.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//创建springmvc配置文件，加载controller对应bean
//@Configuration
@ComponentScan("com.ljx.controller")
public class SpringMvcConfig {

}
