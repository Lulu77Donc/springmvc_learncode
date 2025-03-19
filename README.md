# SpringMVC相关内容
部分内容已经从项目里知道MVC的作用，但是这里还是把一些没有了解到的新的东西写在这里

## 关于我上传这段代码具体是如何实现mvc的
在研究mvc过程中，我觉得很奇怪，按照spring容器来说，我们需要定义配置类来选择我们要扫描的包，接着交给Ioc容器管理，这里的容器可以是ApplicationContext,那么我们mvc是如何光配置配置类，就可以实现mvc功能呢，他是和spring配置类一样交给spring容器管理bean吗？
如果我们要想彻底研究明白mvc是怎样实现自动注册的，就必须要搞明白mvc的另一个配置类---ServletContainerInitConfig，它继承了AbstractAnnotationConfigDispatcherServletInitializer。
AbstractAnnotationConfigDispatcherServletInitializer 是 Spring 提供的一个 自动配置 Servlet 容器 的基类，它会自动完成：
创建 Spring 容器（Root ApplicationContext），加载 getRootConfigClasses() 配置的 Bean（Service、Repository 等）。
创建 Spring MVC 容器（DispatcherServlet 的 ApplicationContext），加载 getServletConfigClasses() 配置的 Bean（Controller、ViewResolver 等）。
自动注册 DispatcherServlet 并映射到 /，让 Spring MVC 处理请求。
接着webconfig配置类的 @ComponentScan 会扫描 @Controller 标注的类，并自动注册到 Spring MVC 容器，使 @RequestMapping 正常生效。
所以你听懂了吧，是servlet容器帮我们实现Spring配置类和SpringMvc配置类的区分，并且以此区分来创建对应容器，这里简单说明两个容器的区别。
Spring 容器分为 根容器（Root ApplicationContext）和 Spring MVC 容器（DispatcherServlet 创建的子容器）：

根容器（父容器）：通常用于管理业务 Bean（Service、Repository 等），不会主动加载 @EnableWebMvc 相关的 MVC 组件。
Spring MVC 容器（子容器）：由 DispatcherServlet 创建，主要管理 Controller、ViewResolver、HandlerMapping 等 MVC 组件。
这也回答了Spring 容器是否加载 MVC Bean，除非你手动设置，否则是不会用父容器去加载mvc的bean的，人家有专门的组件加载。

## @EnableWebMvc
由于上面篇幅过长，这里来讲解一下这个专门负责mvc的配置注解，那么这个注解有什么用的？一般负责做什么？
@EnableWebMvc 的作用
启用 Spring MVC 组件（如 RequestMappingHandlerMapping、RequestMappingHandlerAdapter）
允许使用 @Controller、@RequestMapping 等注解
启用 消息转换器（HttpMessageConverter），支持 JSON、XML 解析
启用 拦截器（HandlerInterceptor）
启用 静态资源映射（相当于 <mvc:resources/>）
允许 @ExceptionHandler 处理全局异常
可见基本涵盖了mvc的所有作用。但是我的源代码里面配置类没有加这个注解，为什么还可以实现mvc的功能，@RequestMappering？
这是因为 Spring MVC 默认会启用 RequestMappingHandlerMapping 和 RequestMappingHandlerAdapter
即使 没有 @EnableWebMvc，DispatcherServlet 也会默认注册：

RequestMappingHandlerMapping（处理 @RequestMapping）
RequestMappingHandlerAdapter（调用 @RequestMapping 标注的方法）

如果你不加 `@EnableWebMvc`，Spring MVC 仍然可以工作 **（默认启用 `RequestMappingHandlerMapping`）**，但某些高级功能不会默认开启：

| 功能 | 不加 `@EnableWebMvc` | 加了 `@EnableWebMvc` |
|---|---|---|
| `@RequestMapping` | ✅ 正常工作 | ✅ 正常工作 |
| `HttpMessageConverter`（支持 JSON/XML） | ❌ 需要手动配置 | ✅ 自动注册 Jackson 解析 JSON |
| `@ExceptionHandler` | ✅ 支持 | ✅ 支持 |
| 静态资源映射（`/static/**`） | ❌ 需要手动配置 | ✅ 默认支持 |
| 视图解析 | ❌ 需要手动配置 | ✅ 支持 `ViewResolver` |
| 拦截器（`HandlerInterceptor`） | ❌ 需要手动添加 | ✅ 默认支持 |


## post中文乱码
我们在post请求中，在请求体里如果加上中文内容，服务器是无法识别中文的，需要用到spring-web的功能,在SpringMVC的servlet配置类里从继承父类的方法中重写，获取过滤器，来帮助解决中文乱码问题
```java
public class ServletContainersInitConfig extends AbstractAnnotationConfigDispatcherServletInitializer{
  protected Filter[] getServletFilters(){
    CharacterEncodingFilter filter = new CharacterEncodingFilter();
    filter.setEncoding("utf-8");
    return new Filter[](filter);
  }
}
```
## 如何防止MVC拦截发送服务器的请求？
前面我们知道SpringMVC最主要的功能是将web请求拦截过来，用mvc的方法去控制controller层，来实现web相应。
但是有一个问题，假如我webapp下放着html的文件，我现在服务器启动，mvc也给我自动拦截请求，使我访问不到文件夹下html文件该怎么办？
这里提供一个方法，我们可以用WebMvcConfigurationSupport的父类方法资源处理器来解决
我们单独设置一个工具类

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class SpringMvcSupport extends WebMvcConfigurationSupport {
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        //当访问到/pages/?时候，走/pages目录下的内容
        registry.addResourceHandler("/pages/**").addResourceLocations("/pages/");
    }
}
```

## 全局异常处理器
全局异常处理器算是springmvc特点之一了，其处理异常的特点尤为重要，为怕忘记，在这里书写一下格式
首先我们要写异常处理器类，在文件中我是命名为ProjectExceptionAvice,接着在类名上写上注解@RestControllerAdvice代表这是要用异常处理器的
之后我们写个方法，在方法上写上@ExceptionHandler(SystemException.class)，这是代表要负责拦截异常种类，参数我们就写对应的异常类型，最后返回回去就可以了
这里写一个标准的：
```java
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
```
这里面的参数我都写在exception的包下了，两种exception都继承了RuntimeException，接着构造方法，我拿其中一个做例子：
```java
public class BusinessException extends RuntimeException{
    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException( Integer code,String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}

```
好了，现在自定义异常弄好了，接着只需要在服务层，逻辑层写一个异常，将我们自定义的异常抛出，我们的程序就可以捕捉到他了
```
public Book getById(Integer id) {
        if(id==1){
            throw new BusinessException(Code.BUSINESS_ERR,"业务逻辑错误");
        }
        //将可能出现的异常进行包装，转换成自定义异常
        try {
            int i = 1/0;
        }catch (Exception e){
            throw new SystemException(Code.SYSTEM_TIME_OUT_ERR,"服务器访问超时，请重试");
        }
        return bookDao.getById(id);
    }
```

