package com.qis.rpc.provider.handler;

import com.alibaba.fastjson.JSONObject;
import com.qis.rpc.common.RpcRequest;
import com.qis.rpc.common.RpcResponse;
import com.qis.rpc.provider.anno.RpcService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务端业务处理类
 * 1.将标有@RpcService注解的bean缓存
 * 2.接收客户端请求
 * 3.根据传递过来的beanName从缓存中查找到对应的bean
 * 4.解析请求中的方法名称. 参数类型 参数信息
 * 5.反射调用bean的方法
 * 6.给客户端进行响应
 *
 * @author qishuo1
 * @create 2021/4/12 17:49
 */
@Component
@ChannelHandler.Sharable
public class RpcServerHandler extends SimpleChannelInboundHandler<String> implements ApplicationContextAware {
    private static final Map<String, List<Object>> SERVICE_INSTANCE_MAP = new ConcurrentHashMap<>();
    private static AtomicInteger count = new AtomicInteger(0);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (CollectionUtils.isEmpty(beansWithAnnotation)) {
            return;
        }
        beansWithAnnotation.forEach((beanName, bean) -> {
            Class<?>[] interfaces = bean.getClass().getInterfaces();
            if (interfaces == null || interfaces.length == 0) {
                throw new RuntimeException("必须实现接口");
            }
            String name = interfaces[0].getName();
            //默认使用第一个接口作为缓存bean的名称
            if (SERVICE_INSTANCE_MAP.containsKey(name)) {
                SERVICE_INSTANCE_MAP.get(name).add(bean);
            } else {
                SERVICE_INSTANCE_MAP.put(name, new CopyOnWriteArrayList<Object>() {{
                    add(bean);
                }});

            }
        });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        RpcRequest rpcRequest = JSONObject.parseObject(msg, RpcRequest.class);
        RpcResponse rpcResponse = getRpcResponse(rpcRequest);
        ctx.writeAndFlush(JSONObject.toJSONString(rpcResponse));
    }

    private RpcResponse getRpcResponse(RpcRequest rpcRequest) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());
        String className = rpcRequest.getClassName();
        Object serverBean = getBean(className);
        if (Objects.isNull(serverBean)) {
            throw new RuntimeException("根据beanName找不到服务,beanName:" + rpcRequest.getClassName());
        }
        Class<?> serverBeanClass = serverBean.getClass();
        String methodName = rpcRequest.getMethodName();
        try {
            Method method = serverBeanClass.getMethod(methodName, rpcRequest.getParameterTypes());
            Object invoke = method.invoke(serverBean, rpcRequest.getParameters());
            rpcResponse.setResult(invoke);
        } catch (Exception e) {
            e.printStackTrace();
            rpcResponse.setError(e.getMessage());
        }
        return rpcResponse;
    }

    private Object getBean(String className) {
        List<Object> serverBeanList = SERVICE_INSTANCE_MAP.get(className);
        if (CollectionUtils.isEmpty(serverBeanList)) {
            throw new RuntimeException("根据beanName找不到服务,beanName:" + className);
        }
        return serverBeanList.get(count.incrementAndGet() % serverBeanList.size());
    }


}
