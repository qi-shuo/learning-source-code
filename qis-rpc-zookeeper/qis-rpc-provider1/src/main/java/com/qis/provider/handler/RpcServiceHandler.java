package com.qis.provider.handler;

import com.alibaba.fastjson.JSONObject;
import com.qis.rpc.pojo.RpcRequest;
import com.qis.rpc.pojo.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static com.qis.provider.ProviderBootstrap.application_context;


/**
 * @author qishuo
 * @date 2021/4/24 10:30 下午
 */
@Component
@ChannelHandler.Sharable
public class RpcServiceHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        RpcResponse response = getResponse(msg);
        channelHandlerContext.writeAndFlush(JSONObject.toJSONString(response));
    }

    private RpcResponse getResponse(String msg) throws Exception {
        RpcRequest rpcRequest = JSONObject.parseObject(msg, RpcRequest.class);
        String className = rpcRequest.getClassName();

        //获取对应的bean
        Object bean = application_context.getBean(Class.forName(className));
        Class<?> serverClass = bean.getClass();
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());
        try {
            //反射调用方法
            Method method = serverClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            Object invoke = method.invoke(bean, rpcRequest.getParameters());
            rpcResponse.setResult(invoke);
        } catch (Exception e) {
            e.printStackTrace();
            rpcResponse.setError(e.getMessage());
        }
        return rpcResponse;

    }

}
