package com.qis.mvcframework.controller;

import com.qis.mvcframework.annotation.Controller;
import com.qis.mvcframework.annotation.RequestMapping;
import com.qis.mvcframework.annotation.Security;

/**
 * @author qishuo
 * @date 2021/2/28 10:18 下午
 */
@Controller
@RequestMapping("/demo/security")
@Security(values = {"tom", "qis","jerry"})
public class SecurityController {

    @RequestMapping("/name")
    public String securityController(String userName) {
        return "security: " + userName;
    }
}
