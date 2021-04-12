package com.qis.rpc.provider.service;

import com.qis.rpc.api.IUserService;
import com.qis.rpc.pojo.User;
import com.qis.rpc.provider.anno.RpcService;
import com.qis.rpc.provider.datasource.UserData;
import org.springframework.stereotype.Service;


/**
 * @author qishuo
 * @date 2021/4/12 9:54 下午
 */
@RpcService
@Service("userService1")
public class UserServiceImpl1 implements IUserService {
    @Override
    public User getById(int id) {
        System.out.println("调用的是userService1的方法.....");
        return UserData.getUser(id);
    }
}
