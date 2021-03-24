package com.qis.springsessiondemo.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author qishuo
 * @date 2021/3/24 10:55 下午
 */
@Configuration
public class MyWebAppConfigurer implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestInterceptor()).addPathPatterns("/**").excludePathPatterns("/login/**", "/error","/**/login.jsp","/favicon.ico");
    }
}
