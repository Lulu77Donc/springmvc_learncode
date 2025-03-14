# SpringMVC相关内容
部分内容已经从项目里知道MVC的作用，但是这里还是把一些没有了解到的新的东西写在这里
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
