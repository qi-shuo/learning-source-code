package com.qis.mvcframework.controller;

import com.qis.mvcframework.annotation.Autowired;
import com.qis.mvcframework.annotation.Controller;
import com.qis.mvcframework.annotation.RequestMapping;
import com.qis.mvcframework.service.DemoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author qishuo
 * @date 2021/2/28 2:14 下午
 */

@Controller
@RequestMapping("/demo")
public class DemoController {


    @Autowired
    private DemoService demoService;


    /**
     * URL: /demo/query?name=lisi
     *
     * @param request
     * @param response
     * @param name
     * @return
     */
    @RequestMapping("/query")
    public String query(HttpServletRequest request, HttpServletResponse response, String name) {
        return demoService.demoService(name);
    }
}

