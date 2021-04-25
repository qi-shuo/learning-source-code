package com.qis.consumer.handler;

import com.alibaba.fastjson.JSONObject;
import com.qis.rpc.pojo.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @author qishuo
 * @date 2021/4/24 11:30 下午
 */
@Slf4j
public class RpcClientHandler extends SimpleChannelInboundHandler<String> implements Callable<Object> {
    private RpcResponse result;
    private final Object lock = new Object();
    private String requestMsg;
    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {

        RpcResponse rpcResponse = JSONObject.parseObject(msg, RpcResponse.class);


//        System.out.printf("请求ID=[%s],响应=[%s]", rpcResponse.getRequestId(), JSONObject.toJSONString(rpcResponse));
//        System.out.println();
        synchronized (lock) {
            result = rpcResponse;
            lock.notify();
        }
    }

    public RpcResponse getResult() {
        return result;
    }

    @Override
    public Object call() throws Exception {
        synchronized (lock) {
            if (Objects.isNull(ctx)) {
                throw new RuntimeException("通道未准备就绪");
            }
            if (StringUtil.isNullOrEmpty(requestMsg)) {
                throw new RuntimeException("发送的请求为空");
            }
            ctx.writeAndFlush(requestMsg);
            lock.wait();
            return result;
        }
    }

    public void setRequestMsg(String requestMsg) {
        this.requestMsg = requestMsg;
    }
}
