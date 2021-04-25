package com.qis.consumer;


import com.qis.rpc.registry.impl.RpcRegistryServiceImpl;
import com.qis.rpc.service.RpcRegistryService;
import com.qis.rpc.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端测试类
 *
 * @author qishuo
 * @date 2021/4/24 11:12 下午
 */

public class ClientTest {

    public static void main(String[] args) throws InterruptedException {
        RpcRegistryService rpcRegistryService = new RpcRegistryServiceImpl("127.0.0.1", 2181);
        List<Class<?>> rpcServiceList = new ArrayList<>();
        rpcServiceList.add(UserService.class);
        UserService userService = (UserService) new RpcConsumer(rpcServiceList, rpcRegistryService).createProxy(UserService.class);

        while (true) {
            System.out.println("执行结果: " + userService.sayHello("are you ok?"));
            Thread.sleep(3000);
        }
    }

}
