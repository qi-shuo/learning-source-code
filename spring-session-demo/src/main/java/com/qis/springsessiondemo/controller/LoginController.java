package com.qis.springsessiondemo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * @author qishuo
 * @date 2021/3/24 10:28 下午
 */
@Controller
@RequestMapping("/login")
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping("/toLogin")
    public String toLogin() {
        logger.info("===============跳转登录页面=================");
        return "login";
    }

    @RequestMapping("/loginSystem")
    public String loginSystem(String userName,  String password, HttpSession httpSession) {
        if ("admin".equals(userName) && "admin".equals(password)) {
            logger.info("===============合法用户====================");
            httpSession.setAttribute("userName", userName + System.currentTimeMillis());
            return "redirect:/demo/result";
        }
        logger.info("==========非法跳转===========");
        return "redirect:/login/toLogin";
    }
}
