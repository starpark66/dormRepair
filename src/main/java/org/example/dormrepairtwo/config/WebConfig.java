package org.example.dormrepairtwo.config;

//引入必要的包，包括自定义的JWT拦截器和Spring的配置注解
import org.example.dormrepairtwo.interceptor.JwtInterceptor;
//定义一个WebConfig类，使用@Configuration注解标识为配置类，实现WebMvcConfigurer接口，重写addInterceptors方法，在其中添加JWT拦截器，并指定拦截的路径和排除的路径
import org.springframework.context.annotation.Configuration;
//
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//交给Spring管理，作为一个配置类，配置JWT拦截器，拦截所有/api/**路径，但排除/api/user/login和/api/user/register路径
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    public WebConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")//删了/api/**，因为现在只要不是登录注册接口都需要拦截
                .excludePathPatterns("/api/users/login", "/api/users/register","/users/login","/users/register"); //排除登录注册接口（增加了/users/开头的路径，兼容可能的路径变动）
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}