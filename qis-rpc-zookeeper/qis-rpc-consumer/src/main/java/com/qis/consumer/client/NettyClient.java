package com.qis.consumer.client;

import com.alibaba.fastjson.JSONObject;
import com.qis.consumer.handler.RpcClientHandler;
import com.qis.rpc.pojo.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * @author qishuo
 * @date 2021/4/24 11:20 下午
 */
public class NettyClient {
    private EventLoopGroup eventLoopGroup;
    private Channel channel;
    private String ip;
    private Integer port;
    private RpcClientHandler rpcClientHandler;
    private ExecutorService executorService = Executors.newCachedThreadPool();


    public NettyClient(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
        this.rpcClientHandler = new RpcClientHandler();
    }

    public void initClient() {
        try {
            eventLoopGroup = new NioEventLoopGroup();

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                            pipeline.addLast(rpcClientHandler);
                        }
                    });
            channel = bootstrap.connect(ip, port).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
            close();
        }

    }

    /**
     * 关闭
     */
    public void close() {
        if (this.channel != null && this.channel.isActive()) {
            this.channel.close();
        }
        if (this.eventLoopGroup != null && !this.eventLoopGroup.isShutdown()) {
            this.eventLoopGroup.shutdownGracefully();
        }
    }

    /**
     * 发送请求
     *
     * @param rpcRequest
     * @return
     * @throws InterruptedException
     */
    public Object send(RpcRequest rpcRequest) throws InterruptedException, ExecutionException {

        rpcClientHandler.setRequestMsg(JSONObject.toJSONString(rpcRequest));
        Future<Object> submit = executorService.submit(rpcClientHandler);
        return submit.get();
    }

    public String getIpAndPort() {
        return ip + "_" + port;
    }

}
