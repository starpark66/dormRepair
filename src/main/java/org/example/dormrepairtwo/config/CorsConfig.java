package org.example.dormrepairtwo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//引入必要的包，包括Spring的配置注解和WebMvcConfigurer接口
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")//允许所有路径进行跨域访问
                .allowedOriginPatterns("*")//允许所有来源进行跨域访问
                .allowedMethods("GET", "POST", "PUT", "DELETE")//允许指定的HTTP方法进行跨域访问
                .allowedHeaders("*")//允许所有请求头进行跨域访问
                .allowCredentials(true)//允许携带凭证进行跨域访问
                .maxAge(3600);//设置预检请求的缓存时间，单位为秒，这里设置为1小时
    }
}