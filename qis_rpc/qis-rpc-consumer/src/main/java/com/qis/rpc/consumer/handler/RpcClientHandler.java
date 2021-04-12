package com.qis.rpc.consumer.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.StringUtil;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @author qishuo1
 * @create 2021/4/12 17:51
 */
@ChannelHandler.Sharable
public class RpcClientHandler extends SimpleChannelInboundHandler<String> implements Callable<Object> {
    private ChannelHandlerContext ctx;
    private final Object lock = new Object();
    private Object result;
    private String requestMsg;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        synchronized (lock) {
            result = msg;
            lock.notify();
        }
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
