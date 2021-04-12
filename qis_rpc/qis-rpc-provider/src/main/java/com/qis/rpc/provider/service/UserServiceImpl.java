package com.qis.rpc.provider.service;

import com.qis.rpc.api.IUserService;
import com.qis.rpc.pojo.User;
import com.qis.rpc.provider.anno.RpcService;
import com.qis.rpc.provider.datasource.UserData;
import org.springframework.stereotype.Service;

/**
 * @author qishuo1
 * @create 2021/4/12 17:48
 */
@RpcService
@Service("userService")
public class UserServiceImpl implements IUserService {

    @Override
    public User getById(int id) {
        System.out.println("调用的是userService的方法.....");
        return UserData.getUser(id);
    }
}
