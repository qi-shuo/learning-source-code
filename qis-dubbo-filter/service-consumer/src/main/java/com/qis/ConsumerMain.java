package com.qis;

import com.qis.client.DemoClient;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;

/**
 * @author qishuo
 * @date 2021/5/16 2:24 下午
 */
public class ConsumerMain {
    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConsumerConfiguration.class);
        DemoClient client = context.getBean(DemoClient.class);
        while (true) {
            System.in.read();

            client.demo();
        }
    }

    @Configuration
    @PropertySource("classpath:/dubbo-consumer.properties")
    @ComponentScan(basePackages = "com.qis.client")
    @EnableDubbo
    static class ConsumerConfiguration {

    }
}
