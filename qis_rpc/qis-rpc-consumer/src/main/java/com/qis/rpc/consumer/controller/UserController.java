package com.qis.rpc.consumer.controller;

import com.qis.rpc.api.IUserService;
import com.qis.rpc.pojo.User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author qishuo
 * @date 2021/4/12 10:27 下午
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private IUserService userService;

    @RequestMapping("/getById")
    public User getById(@RequestParam Integer id) {
        return userService.getById(id);
    }
}
