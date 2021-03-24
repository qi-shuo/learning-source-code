package com.qis.springsessiondemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author qishuo
 * @date 2021/3/24 10:28 下午
 */
@Controller
@RequestMapping("/demo")
public class DemoController {
    @RequestMapping("/result")
    public String result() {
        return "result";
    }
}
