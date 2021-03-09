package com.qis.springboot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qishuo
 * @date 2021/3/9 9:20 下午
 */
@RestController
public class DemoController {
    @RequestMapping("/demo")
    public String demo() {
        return "hello";
    }
}
