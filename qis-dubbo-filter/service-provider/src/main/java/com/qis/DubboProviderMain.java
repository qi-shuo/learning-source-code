package com.qis;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;

/**
 * @author qishuo
 * @date 2021/5/16 1:58 下午
 */
public class DubboProviderMain {

    public static void main(String[] args) throws IOException {
        new AnnotationConfigApplicationContext(ProviderConfiguration.class);
        System.in.read();
    }

    @Configuration
    @EnableDubbo(scanBasePackages = "com.qis.service.impl")
    @PropertySource("classpath:/dubbo-provider.properties")
    static  class  ProviderConfiguration{

    }
}
