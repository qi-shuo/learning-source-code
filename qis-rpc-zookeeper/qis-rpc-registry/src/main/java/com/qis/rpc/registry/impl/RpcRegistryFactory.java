package com.qis.rpc.registry.impl;

import com.qis.rpc.service.RpcRegistryService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Service;

/**
 * @author qishuo
 * @date 2021/4/25 12:35 上午
 */
@Service
public class RpcRegistryFactory implements FactoryBean<RpcRegistryService>, DisposableBean {


    private RpcRegistryService rpcRegistryService;


    @Override
    public void destroy() throws Exception {
        if (null != rpcRegistryService) {
            rpcRegistryService.destroy();
        }
    }

    @Override
    public RpcRegistryService getObject() throws Exception {
        if (null != rpcRegistryService) {
            return rpcRegistryService;
        }
        rpcRegistryService = new RpcRegistryServiceImpl("127.0.0.1",2181);
        return rpcRegistryService;
    }

    @Override
    public Class<?> getObjectType() {
        return RpcRegistryService.class;
    }
}
