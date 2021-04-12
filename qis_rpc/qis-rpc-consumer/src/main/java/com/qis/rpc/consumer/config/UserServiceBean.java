package com.qis.rpc.consumer.config;

import com.qis.rpc.api.IUserService;
import com.qis.rpc.consumer.proxy.RpcClientProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qishuo
 * @date 2021/4/12 10:28 下午
 */
@Configuration
public class UserServiceBean {
    @Bean
    public IUserService userService() {
        return (IUserService) RpcClientProxy.getProxy(IUserService.class);
    }
}
