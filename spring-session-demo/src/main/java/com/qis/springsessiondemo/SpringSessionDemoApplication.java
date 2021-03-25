package com.qis.springsessiondemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

@SpringBootApplication
@EnableRedisHttpSession
public class SpringSessionDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSessionDemoApplication.class, args);
    }

}
