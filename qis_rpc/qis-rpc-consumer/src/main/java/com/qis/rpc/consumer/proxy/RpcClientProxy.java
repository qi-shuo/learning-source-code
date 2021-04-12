package com.qis.rpc.consumer.proxy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qis.rpc.common.RpcRequest;
import com.qis.rpc.common.RpcResponse;
import com.qis.rpc.consumer.clinet.RpcClient;

import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.UUID;

/**
 * @author qishuo1
 * @create 2021/4/12 17:52
 */
public class RpcClientProxy {

    public static Object getProxy(Class<?> serviceClass) {
        return Proxy.newProxyInstance(RpcClientProxy.class.getClassLoader(), new Class[]{serviceClass}, (proxy, method, args) -> {
            RpcRequest rpcRequest = new RpcRequest();
            rpcRequest.setRequestId(UUID.randomUUID().toString());
            rpcRequest.setClassName(serviceClass.getName());
            rpcRequest.setMethodName(method.getName());
            rpcRequest.setParameterTypes(method.getParameterTypes());
            rpcRequest.setParameters(args);
            RpcClient rpcClient = new RpcClient("127.0.0.1", 8899);
            try {
                Object responseMsg = rpcClient.send(JSONObject.toJSONString(rpcRequest));
                RpcResponse rpcResponse = JSONObject.parseObject(responseMsg.toString(), RpcResponse.class);
                if (rpcResponse.getError() != null) {
                    throw new RuntimeException(rpcResponse.getError());
                }
                Object result = rpcResponse.getResult();
                if(Objects.isNull(result)){
                    return null;
                }
                return JSON.parseObject(result.toString(), method.getReturnType());
            } finally {
                rpcClient.close();
            }

        });
    }
}
